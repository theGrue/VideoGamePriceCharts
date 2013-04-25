package com.jgrue.vgpc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Window;
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

public class PriceListActivity extends SherlockListActivity implements ActionBar.OnNavigationListener {
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
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.pricelist);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
		if(searchQuery != null && !searchQuery.equals("")) {
			setSearchMode();
			getSupportActionBar().setTitle("Search results for \"" + searchQuery + "\"");
		} else {
			setBrowseMode();
			browseAlias = getIntent().getStringExtra("BROWSE_CONSOLE_ALIAS");
			browseName = getIntent().getStringExtra("BROWSE_CONSOLE_NAME");
			browseType = getIntent().getStringExtra("BROWSE_TYPE");
			if(browseType == null || browseType.equals("")) {
				browseType = "name";
			}
			
			getSupportActionBar().setTitle(browseName);
			
			Context context = getSupportActionBar().getThemedContext();
	        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.sort_by, R.layout.sherlock_spinner_item);
	        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
	        
	        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        getSupportActionBar().setListNavigationCallbacks(list, this);
	        
	        if(browseType.equals("name"))
	        	getSupportActionBar().setSelectedNavigationItem(0);
			else if(browseType.equals("popular"))
				getSupportActionBar().setSelectedNavigationItem(1);
			else if(browseType.equals("lowest-price"))
				getSupportActionBar().setSelectedNavigationItem(2);
			else if(browseType.equals("highest-price"))
				getSupportActionBar().setSelectedNavigationItem(3);
	        
		}
		
		gameList = new ArrayList<Game>();
		setListAdapter(new PriceListAdapter(gameList));
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
			
			setSupportProgressBarIndeterminateVisibility(false);
		}
		
		@Override
		protected View getPendingView(ViewGroup parent) {
			setSupportProgressBarIndeterminateVisibility(true);
			return getLayoutInflater().inflate(R.layout.pending, null);
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

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		String newBrowseType = browseType;
        
    	switch (itemPosition) {
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
    	
    	return true;
	}
}
