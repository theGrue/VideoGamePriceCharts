package com.jgrue.vgpc.scrapers;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class BrowseScraper {
	private static final String TAG = "BrowseScraper";
	
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
