package org.ajack.audiomark;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MyRadioMarksActivity extends ListActivity {
	
	private AudiomarkDBHelper db = null;
	private ArrayList<Audiomark> audiomarks = null;
	private MyRadioMarksListAdapter adapter;
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myradiomarksactivity);
        
		if (db == null) {
			db = new AudiomarkDBHelper(this);
		}
		
		if (audiomarks == null) {
			audiomarks = db.getAllAudiomarks();
			if (audiomarks.size() == 0) {
				Toast.makeText(MyRadioMarksActivity.this, "No audiomarks", Toast.LENGTH_LONG).show();
			}
		}
		
		adapter = new MyRadioMarksListAdapter(this, R.layout.myradiomarksrow, audiomarks);
		setListAdapter(adapter);
		
		// Register the list activity for a context menu (allows long-tapping)
		registerForContextMenu(getListView());
	}
	
	public void onPause() {
		super.onPause();

		db.close();
	}
	
	public void onResume() {
		super.onResume();
		
		db.open();
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Tapped on a list item, so open up the details view
		Intent i = new Intent(this, AudiomarkDetail.class);
		// Pass the shortcode along with the intent
		i.putExtra("shortcode", audiomarks.get(position).getShortcode());
		startActivity(i);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		// Long tap on a list item, show the context menu
		menu.add(Menu.NONE, 0, Menu.NONE, "Open in browser");
	}
	
	public boolean onContextItemSelected (MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch (item.getItemId()) {
			case 0:
				// Get the url stem and append the shortcode to it
				String url = getString(R.string.yourls_url);
				String shortcode = audiomarks.get(menuInfo.position).getShortcode();
				
		    	Intent browserIntent = new Intent(Intent.ACTION_VIEW); 
		    	Uri yourlsUri = Uri.parse(url + shortcode);
		    	browserIntent.setData(yourlsUri); 
		    	startActivity(browserIntent);
				return true;
			default:
				return false;
		}
	}
}
