package org.ajack.audiomark;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyRadioMarksListAdapter extends ArrayAdapter<Audiomark> {

	private ArrayList<Audiomark> audiomarks;
	private LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	public MyRadioMarksListAdapter(Context context, int textViewResourceId, ArrayList<Audiomark> items) {
		super(context, textViewResourceId, items);
		this.audiomarks = items;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = li.inflate(R.layout.myradiomarksrow, parent, false);
		}
		
		Audiomark audiomark = audiomarks.get(position);
		
		if (audiomark != null) {
			
			TextView title = (TextView) convertView.findViewById(R.id.my_radiomarks_row_title);
			TextView subtitle = (TextView) convertView.findViewById(R.id.my_radiomarks_row_subtitle);
			
			// If the audiomark has a title, the audiomark has been retrieved from the YOURLS service and cached, so show all the details
			// Otherwise we've just got the shortcode and timestamp
			
			if (audiomark.getTitle() != null) {
				
				title.setText(audiomark.getTitle());
				subtitle.setText(audiomark.getSubtitle());
				
			} else {
				
				title.setText(audiomark.getShortcode());
				
			}


		}
		return convertView;
    }


}