package com.jgrue.vgpc.scrapers;

import android.net.Uri;
import android.util.Log;

import com.jgrue.vgpc.data.FullGame;
import com.jgrue.vgpc.data.Game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchScraper {
	private static final String TAG = "SearchScraper";
	
	public static List<Game> getSearchResults(String query) {
		ArrayList<Game> gameList = new ArrayList<Game>();
		
		// Get the HTML page and parse it with jsoup. 
		try {
			URL url = new URL("https://www.pricecharting.com/search-products?type=videogames&submit=Go&q=" + URLEncoder.encode(query, "ISO-8859-1"));
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			// Check to see whether we got redirected straight to a game.
			String results[] = Uri.parse(document.baseUri()).getPath().split("/");
			if(results.length == 4 && results[1].equals("game")) {
				FullGame newGame = GameScraper.getFullGame(results[3], results[2]);
				gameList.add(newGame);
			} else if(!(results.length == 3 && results[1].equals("search") && results[2].equals("no_hits"))) {
				Elements tableRows = document.select("table#games_table tr");
				 
				for(int i = 1; i < tableRows.size(); i++) {
					Elements tableData = tableRows.get(i).select("td");
					String[] consoleGameAlias = Uri.parse(tableData.get(0).select("a").first().attr("href")).getPath().split("/");
					
					Game newGame = new Game();
					newGame.setGameName(tableData.get(0).text());
					newGame.setGameAlias(consoleGameAlias[consoleGameAlias.length - 1]);
					newGame.setConsoleName(tableData.get(1).text());
					newGame.setConsoleAlias(consoleGameAlias[consoleGameAlias.length - 2]);
					try {
						newGame.setUsedPrice(Float.parseFloat(tableData.get(2).text().substring(1).replace(",", "")));
					} catch (NumberFormatException e) {
						Log.e(TAG, "Error parsing price (" + tableData.get(2).text() + ") for " + newGame.getGameName() + ".");
						newGame.setUsedPrice(0.0f);
					}
					
					gameList.add(newGame);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gameList;
	}
}
