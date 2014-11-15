package com.jgrue.vgpc.scrapers;

import android.util.Log;

import com.jgrue.vgpc.data.Game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BrowseScraper {
	private static final String TAG = "BrowseScraper";
	
	public static List<Game> getBrowseResults(String consoleAlias, String sortBy, int page) {
		ArrayList<Game> gameList = new ArrayList<Game>();
		
		// Get the HTML page and parse it with jsoup. 
		try {
			URL url = new URL("http://videogames.pricecharting.com/console/" + consoleAlias + 
					"?sort-by=" + sortBy + "&page=" + page + "&per-page=30");
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			Elements tableRows = document.select("table#games_table tr");
			 
			for(int i = 1; i < tableRows.size() - 1; i++) {
				Elements tableData = tableRows.get(i).select("td");
				String consoleGameAlias = tableData.get(0).select("a").first().attr("href").substring(6);
				
				Game newGame = new Game();
				newGame.setGameName(tableData.get(0).text());
				newGame.setGameAlias(consoleGameAlias.split("/")[1]);
				newGame.setConsoleAlias(consoleGameAlias.split("/")[0]);
				newGame.setGenre(tableData.get(1).text());
				try {
					newGame.setUsedPrice(Float.parseFloat(tableData.get(2).text().substring(1).replace(",", "")));
				} catch (NumberFormatException e) {
					Log.e(TAG, "Error parsing price (" + tableData.get(2).text() + ") for " + newGame.getGameName() + ".");
					newGame.setUsedPrice(0.0f);
				}
				
				gameList.add(newGame);
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
	
	public static int getNumPages(String consoleAlias) {
		int numPages = 0;
		
		// Get the HTML page and parse it with jsoup. 
		try {
			URL url = new URL("http://videogames.pricecharting.com/console/" + consoleAlias + "?sort-by=name");
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			Element paragraph = document.select("div#console-text p strong").first();
			String[] words = paragraph.text().split(" ");
			int numGames = Integer.parseInt(words[words.length - 2]);
			numPages = (int) Math.ceil(numGames / 30.0f);
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
		
		return numPages;
	}
}
