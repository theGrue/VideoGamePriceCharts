package com.jgrue.vgpc;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jgrue.vgpc.R;
import com.jgrue.vgpc.data.VGPCData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class VGPCActivity extends SherlockActivity implements OnClickListener, OnKeyListener {
	private static final String TAG = "VGPCActivity";
	private VGPCData vgpcData;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getSupportActionBar().setSubtitle(getResources().getString(R.string.vgpc_activity_label));
        
        vgpcData = new VGPCData(this);
        
    	// Fill in all the console tables.
    	fillTableFromCursor((TableLayout)findViewById(R.id.nintendo_table), getConsolesByCategory(1));
    	fillTableFromCursor((TableLayout)findViewById(R.id.sega_table), getConsolesByCategory(2));
    	fillTableFromCursor((TableLayout)findViewById(R.id.atari_table), getConsolesByCategory(3));
    	fillTableFromCursor((TableLayout)findViewById(R.id.playstation_table), getConsolesByCategory(4));
    	fillTableFromCursor((TableLayout)findViewById(R.id.other_table), getConsolesByCategory(5));
    	fillTableFromCursor((TableLayout)findViewById(R.id.xbox_table), getConsolesByCategory(6));
        
    	findViewById(R.id.barcode_button).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.search_text).setOnKeyListener(this);
    }
    
    @Override
    public void onDestroy() {
    	if(vgpcData != null)
    		vgpcData.close();
    	super.onDestroy();
    }
    
    private Cursor getConsolesByCategory(int category) {
    	final String[] from = { "console_name, console_alias, " + _ID };
    	final String[] where = { Integer.toString(category) };
    	
    	Log.i(TAG, "Looking up console information for category = " + category);
    	
    	SQLiteDatabase db = vgpcData.getReadableDatabase();
    	Cursor cursor = db.query("consoles", from, "category = ?", where, null, null, _ID + " ASC");
    	startManagingCursor(cursor);
    	
    	Log.i(TAG, "Returned " + cursor.getCount() + " rows for query.");
    	
    	return cursor;
    }
    
    private Cursor getConsoleById(int id) {
    	final String[] from = { "console_name, console_alias"};
    	final String[] where = { Integer.toString(id) };
    	
    	Log.i(TAG, "Looking up console information for _ID = " + id);
    	
    	SQLiteDatabase db = vgpcData.getReadableDatabase();
    	Cursor cursor = db.query("consoles", from, _ID + " = ?", where, null, null, _ID + " ASC");
    	startManagingCursor(cursor);
    	
    	Log.i(TAG, "Returned " + cursor.getCount() + " rows for query.");
    	
    	return cursor;
    }
    
    private void fillTableFromCursor(TableLayout table, Cursor cursor) {
    	int currentRow = 0;
    	ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    	
    	while(cursor.moveToNext()) {
    		if ((cursor.getCount() % 2 == 0 && currentRow < cursor.getCount() / 2) ||
    			(cursor.getCount() % 2 == 1 && currentRow <= cursor.getCount() / 2)) {
    			// Create the TableRows and insert the data that makes up the left column.
    			TableRow tableRow = new TableRow(this);
    			tableRow.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    			
    			TextView textView = new TextView(this);
    			textView.setLayoutParams(new LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    			textView.setTextColor(getResources().getColor(R.drawable.text));
    			textView.setText(cursor.getString(0));
    			textView.setId(cursor.getInt(2));
    			textView.setClickable(true);
    			textView.setOnClickListener(this);
    			tableRow.addView(textView);
    			
    			tableRows.add(tableRow);
    		} else {
    			// Retrieve the TableRows and add data to the right column.
    			TableRow tableRow;
    			if(cursor.getCount() % 2 == 0)
    				tableRow = tableRows.get(currentRow % (cursor.getCount() / 2));
    			else
    				tableRow = tableRows.get(currentRow % ((cursor.getCount() + 1) / 2));
    			
    			TextView textView = new TextView(this);
    			textView.setLayoutParams(new LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    			textView.setTextColor(getResources().getColor(R.drawable.text));
    			textView.setText(cursor.getString(0));
    			textView.setId(cursor.getInt(2));
    			textView.setClickable(true);
    			textView.setOnClickListener(this);
    			tableRow.addView(textView);
    		}
    		
    		currentRow++;
    	}
    	
    	// Add the finished rows to the TableView.
    	for(int i = 0; i < tableRows.size(); i++)
    		table.addView(tableRows.get(i), new TableLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.search_button) {
			String searchGame = ((EditText)findViewById(R.id.search_text)).getText().toString();
			Intent myIntent = new Intent(v.getContext(), PriceListActivity.class);
			myIntent.putExtra("SEARCH_QUERY", searchGame.trim());
			startActivityForResult(myIntent, 0);
		} else if(v.getId() == R.id.barcode_button) {
			IntentIntegrator integrator = new IntentIntegrator(VGPCActivity.this);
			integrator.initiateScan();
		} else {
			// Look up what console this is.
			Cursor cursor = getConsoleById(v.getId());
			if(cursor.moveToNext()) {
				Intent myIntent = new Intent(v.getContext(), PriceListActivity.class);
				myIntent.putExtra("BROWSE_CONSOLE_NAME", cursor.getString(0));
				myIntent.putExtra("BROWSE_CONSOLE_ALIAS", cursor.getString(1));
				startActivityForResult(myIntent, 0);
			}
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(v.getId() == R.id.search_text && event.getAction() == KeyEvent.ACTION_DOWN &&
				keyCode == KeyEvent.KEYCODE_ENTER) {
			// Hide the keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			
			// Perform action on key press
			onClick(findViewById(R.id.search_button));
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && resultCode != RESULT_CANCELED) {
			// handle scan result
			Intent myIntent = new Intent(VGPCActivity.this, FullGameActivity.class);
			myIntent.putExtra("GAME_UPC", scanResult.getContents());
			startActivityForResult(myIntent, 0);
		}
	}
	/*
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Search")
        	.setIcon(R.drawable.ic_search)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}
	*/
}

