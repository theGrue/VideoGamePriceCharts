package com.jgrue.vgpc.data;

import java.util.ArrayList;

/**
 * Created by grue on 5/6/16.
 */
public class GameList {
    private String cursor;
    private ArrayList<Game> products;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public ArrayList<Game> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Game> products) {
        this.products = products;
    }
}
