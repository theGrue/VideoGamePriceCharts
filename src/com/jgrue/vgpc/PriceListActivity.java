package com.jgrue.vgpc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.commonsware.cwac.endless.EndlessAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jgrue.vgpc.R;
import com.jgrue.vgpc.data.Game;
import com.jgrue.vgpc.scrapers.BrowseScraper;
import com.jgrue.vgpc.scrapers.SearchScraper;

public class PriceListActivity extends ListActivity {
	private static final String TAG = "PriceListActivity";
	private static final DecimalFormat moneyFormat = new DecimalFormat("$0.00");
	private List<Game> gameList;
	private boolean searchMode = true;
	private boolean browseMode = false;
	private String searchQuery;
	private String browseAlias;
	private String browseName;
	private String browseType;
	private int numPages = -1;
	private int nextPage = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pricelist);
		
		TextView priceListLabel = (TextView)findViewById(R.id.pricelist_label);
		searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
		if(searchQuery != null && !searchQuery.equals("")) {
			setSearchMode();
			setTitle(getTitle() + " : Search");
			priceListLabel.setText("Search results for \"" + searchQuery + "\"");
		} else {
			setBrowseMode();
			browseAlias = getIntent().getStringExtra("BROWSE_CONSOLE_ALIAS");
			browseName = getIntent().getStringExtra("BROWSE_CONSOLE_NAME");
			browseType = getIntent().getStringExtra("BROWSE_TYPE");
			if(browseType == null || browseType.equals("")) {
				browseType = "name";
			}
			
			priceListLabel.setVisibility(View.GONE);
			Spinner priceListSpinner = (Spinner)findViewById(R.id.pricelist_spinner);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		            this, R.array.sort_by, android.R.layout.simple_spinner_item);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    priceListSpinner.setAdapter(adapter);
			priceListSpinner.setVisibility(View.VISIBLE);
			
			if(browseType.equals("name"))
				priceListSpinner.setSelection(0);
			else if(browseType.equals("popular"))
				priceListSpinner.setSelection(1);
			else if(browseType.equals("lowest-price"))
				priceListSpinner.setSelection(2);
			else if(browseType.equals("highest-price"))
				priceListSpinner.setSelection(3);
			
			setTitle(getTitle() + " : " + browseName);
		}
		
		gameList = new ArrayList<Game>();
		setListAdapter(new PriceListAdapter(gameList));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Spinner priceListSpinner = (Spinner)findViewById(R.id.pricelist_spinner);
		priceListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		        String newBrowseType = browseType;
		        
		    	switch (position) {
			        case 0: newBrowseType = "name";
			        	break;
			        case 1: newBrowseType = "popular";
			        	break;
			        case 2: newBrowseType = "lowest-price";
			        	break;
			        case 3: newBrowseType = "highest-price";
			        	break;
		        }
		        
		    	if(!newBrowseType.equals(browseType)) {
			        Intent myIntent = new Intent(PriceListActivity.this, PriceListActivity.class);
					myIntent.putExtra("BROWSE_CONSOLE_NAME", browseName);
					myIntent.putExtra("BROWSE_CONSOLE_ALIAS", browseAlias);
					myIntent.putExtra("BROWSE_TYPE", newBrowseType);
					startActivityForResult(myIntent, 0);
					finish();
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) { }
		});
	}

	private class PriceListAdapter extends EndlessAdapter {
		
		private List<Game> gameListToLoad = new ArrayList<Game>();
		
		public PriceListAdapter(List<Game> gameList) {
			super(new ArrayAdapter<Game>(PriceListActivity.this, R.layout.pending, R.id.pending_text, gameList));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.pricerow, null);
			}
			
			Game game = null;
			if(position < gameList.size())
				game = gameList.get(position);
			
			if(game != null) {
				TextView gameName = (TextView)convertView.findViewById(R.id.game_name);
				if(gameName != null)
					gameName.setText(game.getGameName());
				
				TextView consoleGenre = (TextView)convertView.findViewById(R.id.console_genre);
				if(consoleGenre != null && searchMode)
					consoleGenre.setText(game.getConsoleName() + ", " + game.getGenre());
				else if(consoleGenre != null && browseMode)
					consoleGenre.setText(game.getGenre());
				
				TextView usedPrice = (TextView)convertView.findViewById(R.id.used_price);
				if(usedPrice != null)
				{
					if(game.getUsedPrice() > 0.0f)
						usedPrice.setText(moneyFormat.format(game.getUsedPrice()));
					else
						usedPrice.setText("none");
				}
			} else {
				convertView = super.getView(position, convertView, parent);
			}
						
			return convertView;
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if(searchMode)
				gameListToLoad = SearchScraper.getSearchResults(searchQuery);
			else if(browseMode)
			{
				if(numPages == -1)
					numPages = BrowseScraper.getNumPages(browseAlias);
				gameListToLoad = BrowseScraper.getBrowseResults(browseAlias, browseType, nextPage);
				nextPage++;
			}	
			
			if(searchMode && gameListToLoad.size() == 1) {
				// Redirect straight to the game, just like the website does.
				Intent myIntent = new Intent(PriceListActivity.this, FullGameActivity.class);
				myIntent.putExtra("GAME_NAME", gameListToLoad.get(0).getGameName());
				myIntent.putExtra("GAME_ALIAS", gameListToLoad.get(0).getGameAlias());
				myIntent.putExtra("CONSOLE_NAME", gameListToLoad.get(0).getConsoleName());
				myIntent.putExtra("CONSOLE_ALIAS", gameListToLoad.get(0).getConsoleAlias());
				myIntent.putExtra("USED_PRICE", gameListToLoad.get(0).getUsedPrice());
				startActivityForResult(myIntent, 0);
				finish();
			}
			
			return !searchMode && (browseMode && nextPage < numPages);
		}

		@Override
		protected void appendCachedData() {
			if(gameListToLoad.size() > 0) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Game> wrappedAdapter = (ArrayAdapter<Game>)getWrappedAdapter();
				
				for (int i = 0; i < gameListToLoad.size(); i++) { 
					wrappedAdapter.add(gameListToLoad.get(i));
				}
			} else if(searchMode) {
				AlertDialog alertDialog = new AlertDialog.Builder(PriceListActivity.this).create();
	        	alertDialog.setMessage("No results found.");
	        	alertDialog.setButton("OK", new DialogInterface.OnClickListener() { @Override
					public void onClick(DialogInterface dialog, int which) { finish(); return; } });
	        	alertDialog.show();
			}
		}
		
		@Override
		protected View getPendingView(ViewGroup parent) {
			View row = getLayoutInflater().inflate(R.layout.pending, null);
			return row;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	  
	  	Intent myIntent = new Intent(v.getContext(), FullGameActivity.class);
		myIntent.putExtra("GAME_NAME", gameList.get(position).getGameName());
		myIntent.putExtra("GAME_ALIAS", gameList.get(position).getGameAlias());
		myIntent.putExtra("CONSOLE_NAME", gameList.get(position).getConsoleName());
		myIntent.putExtra("CONSOLE_ALIAS", gameList.get(position).getConsoleAlias());
		myIntent.putExtra("USED_PRICE", gameList.get(position).getUsedPrice());
		startActivityForResult(myIntent, 0);
	}
	
	private void setSearchMode() {
		searchMode = true;
		browseMode = false;
	}
	
	private void setBrowseMode() {
		searchMode = false;
		browseMode = true;
	}
}
