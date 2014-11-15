package com.jgrue.vgpc.scrapers;

import android.util.Log;

import com.jgrue.vgpc.data.Game;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BrowseJsonScraper {
	private static final String TAG = "BrowseJsonScraper";
	
	public static List<Game> getBrowseResults(String consoleAlias, String sortBy, int page) {
		ArrayList<Game> gameList = new ArrayList<Game>();
		
		// Get the HTML page and parse it with jsoup. 
		try {
			URL url = new URL("http://videogames.pricecharting.com/console/" + consoleAlias + 
					"?sort-by=" + sortBy + "&page=" + page + "&per-page=30&format=json");
			Log.i(TAG, "Target URL: " + url.toString());
			
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url.toString()));
            
            if (response.getStatusLine().getStatusCode() == 200) {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        
		        String line;
		        StringBuilder builder = new StringBuilder();
		        while ((line = reader.readLine()) != null) {
		        	builder.append(line);
		        }
		        
				JSONObject browseData = new JSONObject(builder.toString());
				JSONArray products = browseData.getJSONArray("products");
				JSONObject genres = browseData.getJSONObject("genres");
				
				for (int i = 0; i < products.length(); i++)
				{
					JSONArray productInfo = products.getJSONArray(i);
					
					Game newGame = new Game();
					newGame.setGameName(productInfo.getString(0));
					newGame.setGameAlias(getGameAlias(newGame.getGameName()));
					newGame.setConsoleAlias(consoleAlias);
					newGame.setGenre(genres.getString(productInfo.getString(1)));
					try {
						newGame.setUsedPrice((float)productInfo.getDouble(2));
					} catch (JSONException e) {
						Log.e(TAG, "Error parsing used price (" + productInfo.getString(2) + ") for " + newGame.getGameName() + ".");
						newGame.setUsedPrice(0.0f);
					}
					try {
						newGame.setNewPrice((float)productInfo.getDouble(5));
					} catch (JSONException e) {
						Log.e(TAG, "Error parsing new price (" + productInfo.getString(5) + ") for " + newGame.getGameName() + ".");
						newGame.setNewPrice(0.0f);
					}
					
					gameList.add(newGame);
				}
		    }
		} catch (Exception e) { 
			Log.e(TAG, e.getMessage());
		}
				
		return gameList;
	}
	
	public static int getNumPages(String consoleAlias) {
		// i dunno lol
		return BrowseScraper.getNumPages(consoleAlias);
	}
	
	private static String getGameAlias(String gameName) {
		return encodeURIComponent(gameName
				.replaceAll(",\\s", " ")
				.replaceAll("\\s+", "-")
				.replaceAll("[.!?/:]", "")
				.replaceAll("-{2,}", "-")
				.toLowerCase());
	}
	
	/**
	   * Encodes the passed String as UTF-8 using an algorithm that's compatible
	   * with JavaScript's <code>encodeURIComponent</code> function. Returns
	   * <code>null</code> if the String is <code>null</code>.
	   * 
	   * @param s The String to be encoded
	   * @return the encoded String
	   */
	  public static String encodeURIComponent(String s)
	  {
	    String result = null;

	    try
	    {
	      result = URLEncoder.encode(s, "UTF-8")
	                         .replaceAll("\\+", "%20")
	                         .replaceAll("\\%21", "!")
	                         .replaceAll("\\%27", "'")
	                         .replaceAll("\\%28", "(")
	                         .replaceAll("\\%29", ")")
	                         .replaceAll("\\%7E", "~");
	    }

	    // This exception should never occur.
	    catch (UnsupportedEncodingException e)
	    {
	      result = s;
	    }

	    return result;
	  }  
}
