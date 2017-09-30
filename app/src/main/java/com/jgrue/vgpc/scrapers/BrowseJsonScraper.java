package com.jgrue.vgpc.scrapers;

import android.util.Log;

import com.jgrue.vgpc.data.Game;
import com.jgrue.vgpc.data.GameList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class BrowseJsonScraper {
	private static final String TAG = "BrowseJsonScraper";
	
	public static GameList getBrowseResults(String consoleAlias, String sortBy, String cursor) {
		ArrayList<Game> gameList = new ArrayList<Game>();
		String nextCursor = null;
		
		// Get the HTML page and parse it with jsoup. 
		try {
			URL url = new URL("https://www.pricecharting.com/console/" + consoleAlias +
					"?sort=" + sortBy + "&cursor=" + (cursor == null ? "" : cursor) + "&format=json");
			Log.i(TAG, "Target URL: " + url.toString());

			Connection.Response res = Jsoup.connect(url.toString()).timeout(30000).execute();
            
            if (res.statusCode() == 200) {
				JSONObject browseData = new JSONObject(res.body());
				JSONArray products = browseData.getJSONArray("products");
				if (browseData.has("cursor")) {
					nextCursor = browseData.getString("cursor");
				}
				
				for (int i = 0; i < products.length(); i++)
				{
					JSONObject productInfo = products.getJSONObject(i);
					
					Game newGame = new Game();
					newGame.setGameName(productInfo.getString("productName"));
					newGame.setGameAlias(productInfo.getString("productUri"));
					newGame.setConsoleAlias(consoleAlias);
					try {
						newGame.setUsedPrice(Float.parseFloat(productInfo.getString("price1").substring(1).replace(",", "")));
					} catch (NumberFormatException e) {
						Log.e(TAG, "Error parsing used price (" + productInfo.getString("price1").substring(1) + ") for " + newGame.getGameName() + ".");
						newGame.setUsedPrice(0.0f);
					} catch (IndexOutOfBoundsException e) {
						Log.e(TAG, "Used price was not found.");
					}
					try {
						newGame.setNewPrice(Float.parseFloat(productInfo.getString("price2").substring(1).replace(",", "")));
					} catch (NumberFormatException e) {
						Log.e(TAG, "Error parsing new price (" + productInfo.getString("price2").substring(1) + ") for " + newGame.getGameName() + ".");
						newGame.setNewPrice(0.0f);
					} catch (IndexOutOfBoundsException e) {
						Log.e(TAG, "New price was not found.");
					}
					
					gameList.add(newGame);
				}
		    }
		} catch (Exception e) { 
			Log.e(TAG, e.getMessage());
		}

		GameList result = new GameList();
		result.setCursor(nextCursor);
		result.setProducts(gameList);
		return result;
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
