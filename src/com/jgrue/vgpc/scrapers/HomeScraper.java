package com.jgrue.vgpc.scrapers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.jgrue.vgpc.data.Category;
import com.jgrue.vgpc.data.Console;

public class HomeScraper {
	private static final String TAG = "SearchScraper";

	public static List<Category> getCategoryList(String query) {
		List<Category> categoryList = new ArrayList<Category>();
		
		try {
			URL url = new URL("http://videogames.pricecharting.com/");
			Log.i(TAG, "Target URL: " + url.toString());
			Document document = Jsoup.parse(url, 30000);
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			Elements menu = document.select(".menu > ul > li");
			
			for(int i = 0; i < menu.size(); i++) {
				Category category = new Category(menu.get(i).select("a").first().text(), i);
				Elements items = menu.get(i).select("ul > li > a");
				
				for(int j = 0; j < items.size(); j++) {
					URL href = new URL(items.get(j).attr("href"));
					if (href.getPath().startsWith("/console/")) {
						category.getConsoles().add(new Console(items.get(j).text(), href.getPath().substring("/console/".length()), j));
					}
				}
				
				if (category.getConsoles().size() > 0) {
					categoryList.add(category);	
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return categoryList;
	}
}
