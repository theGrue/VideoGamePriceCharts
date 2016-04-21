package com.jgrue.vgpc.scrapers;

import android.net.Uri;
import android.util.Log;

import com.jgrue.vgpc.data.FullGame;
import com.jgrue.vgpc.data.Price;
import com.jgrue.vgpc.data.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameScraper {
	private static final String TAG = "GameScraper";
	
	public static FullGame getFullGame(String upc) {
		return getFullGameInternal("https://www.pricecharting.com/search/?q=" + upc);
	}
	
	public static FullGame getFullGame(String gameAlias, String consoleAlias) {
		if (consoleAlias.equals("game")) {
			return getFullGameInternal("https://www.pricecharting.com/game/" + gameAlias);
		}
		else {
			return getFullGameInternal("https://www.pricecharting.com/game/" + consoleAlias + "/" + gameAlias);
		}
	}
	
	private static FullGame getFullGameInternal(String urlString) {
		FullGame game = new FullGame();
		
		try {
			URL url = new URL(urlString);
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			// Check to see whether we found anything.
			String results[] = Uri.parse(document.baseUri()).getPath().split("/");
			
			if(!results[2].startsWith("no_hits")) {
				game.setDocument(document);
				game.setConsoleName(document.select("#game-page .chart_title a").get(0).text());
				game.setConsoleAlias(results[results.length - 2]);
				game.setGameName(document.select("#product_name").get(0).text().replaceAll(" Prices " + game.getConsoleName() + "$", "").replaceAll(" Prices$", ""));
				game.setGameAlias(results[results.length - 1]);
				//game.setLastObservation(document.select("div#price_data div.prices_now p").get(0).html().substring(18).split("<")[0]);
				game.setImageUrl(document.select("#product_details .cover img").get(0).attr("src"));
				try {
					game.setUsedVolume(document.select("#volume_link_used").get(0).text());
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Error parsing used volume for " + game.getGameName() + ".");
					game.setUsedVolume("unknown");
				}
				try {
					game.setCompleteVolume(document.select("#volume_link_cib").get(0).text());
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Error parsing complete volume for " + game.getGameName() + ".");
					game.setCompleteVolume("unknown");
				}
				try {
					game.setNewVolume(document.select("#volume_link_new").get(0).text());
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Error parsing new volume for " + game.getGameName() + ".");
					game.setNewVolume("unknown");
				}
				try {
					game.setUsedPrice(Float.parseFloat(document.select("#used_price .price").get(0).text().substring(1).replace(",", "")));
				} catch (NumberFormatException e) {
					Log.e(TAG, "Error parsing used price (" + document.select("#used_price .price").get(0).text().substring(1) + ") for " + game.getGameName() + ".");
					game.setUsedPrice(0.0f);
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Used price was not found.");
				}
				try {
					game.setCompletePrice(Float.parseFloat(document.select("#complete_price .price").get(0).text().substring(1).replace(",", "")));
				} catch (NumberFormatException e) {
					Log.e(TAG, "Error parsing complete price (" + document.select("#complete_price .price").get(0).text().substring(1) + ") for " + game.getGameName() + ".");
					game.setCompletePrice(0.0f);
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "Complete price was not found.");
				}
				try {
					game.setNewPrice(Float.parseFloat(document.select("#new_price .price").get(0).text().substring(1).replace(",", "")));
				} catch (NumberFormatException e) {
					Log.e(TAG, "Error parsing new price (" + document.select("#new_price .price").get(0).text().substring(1) + ") for " + game.getGameName() + ".");
					game.setNewPrice(0.0f);
				} catch (IndexOutOfBoundsException e) {
					Log.e(TAG, "New price was not found.");
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

		return game;
	}
	
	public static List<Store> getStores(FullGame game) {
		String gameAlias = game.getGameAlias(), consoleAlias = game.getConsoleAlias();
		List<Store> storeList = new ArrayList<Store>();
		
		try {
			if (game.getDocument() == null) {
				URL url = new URL("http://videogames.pricecharting.com/game/" + consoleAlias + "/" + gameAlias);
				Log.i(TAG, "Target URL: " + url.toString());
				Document document = Jsoup.parse(url, 30000);
				Log.i(TAG, "Retrieved URL: " + document.baseUri());
				game.setDocument(document);
			}
			
			Elements tableRows = game.getDocument().select("div#price_comparison > div.tab-frame > table.used-prices > tbody:eq(1) > tr");
			for(int i = 0; i < tableRows.size(); i++) {
				Elements tableData = tableRows.get(i).select("td");
				 
				Store newStore = new Store();
				newStore.setStoreName(tableData.get(0).text());
				newStore.setStoreLink(tableData.get(2).select("a").first().attr("href"));

				if(tableData.get(1).text().startsWith("$"))
					newStore.setStorePrice(Float.parseFloat(tableData.get(1).text().substring(1).replace(",", "")));
				else
					newStore.setStorePrice(0.0f);
				 
				storeList.add(newStore);
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
		
		return storeList;
	}
	
	public static List<Price> getPriceHistory(String type, String gameAlias, String consoleAlias) {
		List<Price> priceList = new ArrayList<Price>();
		
		try {
			URL url = new URL("http://videogames.pricecharting.com/game/" + consoleAlias + "/" + gameAlias);
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			String javaScript = document.select("div.container div.content div.mid_col script").first().html();
			Matcher matcher = Pattern.compile("VGPC\\.chart_data = \\{.*\\}").matcher(javaScript);
			
			if(matcher.find()) {
				String jString = matcher.group().substring(matcher.group().indexOf("{"));
				JSONObject chartData = new JSONObject(jString);
				JSONArray jsPriceArray = chartData.getJSONArray(type);
				
				for(int i = 0; i < jsPriceArray.length(); i++) {
					Calendar priceCal = Calendar.getInstance();
					priceCal.setTimeInMillis(jsPriceArray.getJSONArray(i).getLong(0));
					
					Price newPrice = new Price();
					newPrice.setPrice((float)jsPriceArray.getJSONArray(i).getDouble(1));
					newPrice.setPriceDate(priceCal.getTime());
					priceList.add(newPrice);
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i(TAG, "Returning from getPriceHistory with " + priceList.size() + " items.");
		return priceList;
	}
}
