package com.jgrue.vgpc.data;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils.InsertHelper;

public class VGPCData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "vgpc.db";
	private static final int DATABASE_VERSION = 1;
	
	/** Create a helper object for the Events database */
	public VGPCData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create "consoles" table
		db.execSQL("CREATE TABLE consoles (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"console_name TEXT NOT NULL, console_alias TEXT NOT NULL, category INTEGER NOT NULL);");
		
		/* I had some big plans to do lots of database caching to learn more about Sqlite,
		 * but the app is already snappy as it as and prices could update daily, so I don't
		 * really see the reason to do it at this time. Maybe in the future.
		 * 
		// Create "games" table
		db.execSQL("CREATE TABLE games (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"console_id INTEGER NOT NULL, game_name TEXT NOT NULL, game_alias TEXT NOT NULL, " +
				"genre TEXT NOT NULL, used_price REAL NOT NULL, new_price REAL, volume INTEGER, " +
				"last_observation TEXT, asin TEXT);");
		
		// Create "prices" table
		db.execSQL("CREATE TABLE prices (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"game_id INTEGER NOT NULL, price REAL NOT NULL, price_date TEXT NOT NULL);");
		
		// Create "stores" table
		db.execSQL("CREATE TABLE stores (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"game_id INTEGER NOT NULL, store_name TEXT NOT NULL, store_price REAL NOT NULL, " +
				"store_link TEXT NOT NULL);");
		
		// Create "pages" table
		db.execSQL("CREATE TABLE pages (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"game_id INTEGER NOT NULL, page_number INTEGER NOT NULL, page_type INTEGER NOT NULL," +
				"priority INTEGER NOT NULL, last_modified INTEGER NOT NULL);");
		*/
		
		// Populate static tables
		insertConsoles(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Just drop all the tables.
		db.execSQL("DROP TABLE IF EXISTS consoles");
		db.execSQL("DROP TABLE IF EXISTS games");
		db.execSQL("DROP TABLE IF EXISTS prices");
		db.execSQL("DROP TABLE IF EXISTS stores");
		db.execSQL("DROP TABLE IF EXISTS pages");
		
		onCreate(db);
	}

	private void insertConsoles(SQLiteDatabase db) {
		// Create a single InsertHelper to handle this set of insertions.
        InsertHelper ih = new InsertHelper(db, "consoles");
        
        // Get the numeric indexes for each of the columns that we're updating.
        final int nameColumn = ih.getColumnIndex("console_name");
        final int aliasColumn = ih.getColumnIndex("console_alias");
        final int categoryColumn = ih.getColumnIndex("category");
        
        // Insert Nintendo consoles.
        ih.prepareForInsert();
        ih.bind(nameColumn, "Nintendo NES"); ih.bind(aliasColumn, "nes"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Super Nintendo"); ih.bind(aliasColumn, "super-nintendo"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Nintendo 64"); ih.bind(aliasColumn, "nintendo-64"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Gamecube"); ih.bind(aliasColumn, "gamecube"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Wii"); ih.bind(aliasColumn, "wii"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Gameboy Color"); ih.bind(aliasColumn, "gameboy-color"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Gameboy Advance"); ih.bind(aliasColumn, "gameboy-advance"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Nintendo DS"); ih.bind(aliasColumn, "nintendo-ds"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Nintendo 3DS"); ih.bind(aliasColumn, "nintendo-3ds"); ih.bind(categoryColumn, 1);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Virtual Boy"); ih.bind(aliasColumn, "virtual-boy"); ih.bind(categoryColumn, 1);
        ih.execute();
        
        // Insert Sega consoles.
        ih.prepareForInsert();
        ih.bind(nameColumn, "Sega Genesis"); ih.bind(aliasColumn, "sega-genesis"); ih.bind(categoryColumn, 2);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Sega Master System"); ih.bind(aliasColumn, "sega-master-system"); ih.bind(categoryColumn, 2);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Sega Saturn"); ih.bind(aliasColumn, "sega-saturn"); ih.bind(categoryColumn, 2);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Sega Dreamcast"); ih.bind(aliasColumn, "sega-dreamcast"); ih.bind(categoryColumn, 2);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Sega CD"); ih.bind(aliasColumn, "sega-cd"); ih.bind(categoryColumn, 2);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Sega Game Gear"); ih.bind(aliasColumn, "sega-game-gear"); ih.bind(categoryColumn, 2);
        ih.execute();

        // Insert Atari consoles.
        ih.prepareForInsert();
        ih.bind(nameColumn, "Atari 2600"); ih.bind(aliasColumn, "atari-2600"); ih.bind(categoryColumn, 3);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Atari 5200"); ih.bind(aliasColumn, "atari-5200"); ih.bind(categoryColumn, 3);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Atari 7800"); ih.bind(aliasColumn, "atari-7800"); ih.bind(categoryColumn, 3);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Atari Lynx"); ih.bind(aliasColumn, "atari-lynx"); ih.bind(categoryColumn, 3);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Atari Jaguar"); ih.bind(aliasColumn, "jaguar"); ih.bind(categoryColumn, 3);
        ih.execute();

        // Insert PlayStation consoles.
        ih.prepareForInsert();
        ih.bind(nameColumn, "PlayStation 1"); ih.bind(aliasColumn, "playstation"); ih.bind(categoryColumn, 4);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "PlayStation 2"); ih.bind(aliasColumn, "playstation-2"); ih.bind(categoryColumn, 4);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "PlayStation 3"); ih.bind(aliasColumn, "playstation-3"); ih.bind(categoryColumn, 4);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "PSP"); ih.bind(aliasColumn, "psp"); ih.bind(categoryColumn, 4);
        ih.execute();

        // Insert Other consoles.
        ih.prepareForInsert();
        ih.bind(nameColumn, "3DO"); ih.bind(aliasColumn, "3do"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "CD-i"); ih.bind(aliasColumn, "cd-i"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Colecovision"); ih.bind(aliasColumn, "colecovision"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Commodore 64"); ih.bind(aliasColumn, "commodore-64"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Intellivision"); ih.bind(aliasColumn, "intellivision"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Mac"); ih.bind(aliasColumn, "macintosh"); ih.bind(categoryColumn, 5);

        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "N-Gage"); ih.bind(aliasColumn, "n-gage"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Neo Geo"); ih.bind(aliasColumn, "neo-geo"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Neo Geo Pocket Color"); ih.bind(aliasColumn, "neo-geo-pocket-color"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Odyssey 2"); ih.bind(aliasColumn, "odyssey-2"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "PC"); ih.bind(aliasColumn, "pc-games"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "TurboGrafx-16"); ih.bind(aliasColumn, "turbografx-16"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Vectrex"); ih.bind(aliasColumn, "vectrex"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Xbox"); ih.bind(aliasColumn, "xbox"); ih.bind(categoryColumn, 5);
        
        ih.execute(); ih.prepareForInsert();
        ih.bind(nameColumn, "Xbox 360"); ih.bind(aliasColumn, "xbox-360"); ih.bind(categoryColumn, 5);
        ih.execute();
	}
	
}
