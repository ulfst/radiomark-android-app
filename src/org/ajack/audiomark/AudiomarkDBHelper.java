package org.ajack.audiomark;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AudiomarkDBHelper {
	
	private final String TAG = "AudiomarkDBHelper";
	
	private final String DB_NAME = "audiomarks";
	private final String TABLE_NAME = "audiomarks";
	private final int DB_VER = 1;
	
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private Context mContext;
	
	public AudiomarkDBHelper(Context context) {
		mContext = context;
		dbHelper = new DBHelper(context);
		db = dbHelper.getDatabase();
	}
	
	public void close() {
		if (db.isOpen()) {
			db.close();
		}
	}
	
	public void open() {
		if (!db.isOpen()) {
			db = dbHelper.getDatabase();
		}
	}
	
	public ArrayList<Audiomark> getAllAudiomarks() {

		ArrayList<Audiomark> audiomarks = new ArrayList<Audiomark>();
		
		Cursor c = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY timestamp ASC", null);
		
		if (c.getCount() == 0) {
		
			db.execSQL("INSERT INTO \"audiomarks\" (shortcode, timestamp) VALUES ('x0FDcyRW','1306442739')");
			db.execSQL("INSERT INTO \"audiomarks\" (shortcode, timestamp) VALUES ('Lo3Xrbo4','1306442729')");
			db.execSQL("INSERT INTO \"audiomarks\" (shortcode, timestamp) VALUES ('PO3g5TKs','1306442719')");
			
		}
		
		c.deactivate();
		
		c = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY timestamp ASC", null);
		 
		if (c.getCount() > 0) {
			 c.moveToFirst();
			 do {
				 Audiomark mark = new Audiomark();
				 
				 // Every audiomark will have at least a shortcode and timestamp
				 mark.setShortcode(c.getString(c.getColumnIndex("shortcode")));
				 mark.setTimestamp(c.getString(c.getColumnIndex("timestamp")));
				 
				 // If the audiomark has been viewed on the device, it'll also have the rest of the details too
				 if (!c.isNull(c.getColumnIndex("title"))) 				{ mark.setTitle(c.getString(c.getColumnIndex("title"))); }
				 if (!c.isNull(c.getColumnIndex("subtitle"))) 			{ mark.setSubtitle(c.getString(c.getColumnIndex("subtitle"))); }
				 if (!c.isNull(c.getColumnIndex("radio_station"))) 		{ mark.setRadio_station(c.getString(c.getColumnIndex("radio_station"))); }
				 if (!c.isNull(c.getColumnIndex("radio_station_url")))	{ mark.setRadio_station_url(c.getString(c.getColumnIndex("radio_station_url"))); }
				 if (!c.isNull(c.getColumnIndex("url"))) 				{ mark.setUrl(c.getString(c.getColumnIndex("url"))); }
				 if (!c.isNull(c.getColumnIndex("details"))) 			{ mark.setDetails(c.getString(c.getColumnIndex("details"))); }

				 audiomarks.add(mark);
				 
			 } while (c.moveToNext());
		 }
		 c.close();

		 return audiomarks;
		
	}
	
	public long addAudiomark(Audiomark mAudiomark) {

		ContentValues values = new ContentValues();
		 
		 values.put("title", mAudiomark.getTitle());
		 values.put("subtitle", mAudiomark.getSubtitle());
		 values.put("url", mAudiomark.getUrl());
		 values.put("radio_station", mAudiomark.getRadio_station());
		 values.put("radio_station_url", mAudiomark.getRadio_station_url());
		 values.put("details", mAudiomark.getDetails());
		 		 
		 long ins = db.insert(TABLE_NAME, "", values);
		 
		 return ins; // Returns -1 if an error occurred
	}
	
	public boolean audiomarkCacheExists(String shortcode) {
		// Check if an audiomark cache exists in the database by checking to see if it has a title
		Cursor c = db.rawQuery("select shortcode, title from " + TABLE_NAME + " WHERE shortcode = '" + shortcode + "'", null);		
		c.moveToFirst();
		
		int count = c.getCount();
		boolean nullTitle = c.isNull(c.getColumnIndex("title"));
		c.close();
		
		if (count == 0) return false;
		
		if (!nullTitle) {
			return true;
		}
		
		return false;
		

	}
	
	public void updateAudiomark(Audiomark audiomark) {
		// Update an audiomark in the database
		// Overwrite all values (except shortcode)
		
		ContentValues values = new ContentValues();
		values.put("title", audiomark.getTitle());
		values.put("subtitle", audiomark.getSubtitle());
		values.put("url", audiomark.getUrl());
		values.put("radio_station", audiomark.getRadio_station());
		values.put("radio_station_url", audiomark.getRadio_station_url());
		values.put("details", audiomark.getDetails());
		
		db.update(TABLE_NAME, values, "shortcode='" + audiomark.getShortcode() + "'", null);
	}
	
	
	public void deleteAllRecords() {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		
	}
	
	
	public void deleteSingleRecord(int id) {
		
		String[] args = {String.valueOf(id)};
		db.delete(TABLE_NAME, "id = ?", args);
		
	}
	
	
	
	 private class DBHelper extends SQLiteOpenHelper {
		 
		private SQLiteDatabase db = null;

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VER);
			db = getWritableDatabase();
		}

		public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE " + TABLE_NAME + " (shortcode TEXT PRIMARY KEY, timestamp TEXT, title TEXT, subtitle TEXT, url TEXT, radio_station TEXT, radio_station_url TEXT, details TEXT);");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			 onCreate(db);
		}
		
		public SQLiteDatabase getDatabase() {
			if (db == null) { db = getWritableDatabase(); }
			
			return db;
		}
		
		public void close() {
			if (db.isOpen()) {
				db.close();
			}
		}
		
	 }
}