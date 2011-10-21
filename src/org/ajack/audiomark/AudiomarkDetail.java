package org.ajack.audiomark;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AudiomarkDetail extends Activity {
	
	private TextView titleView, subtitleView, urlView, radioStationView, radioStationUrlView, detailsView;
	private AudiomarkDBHelper dbHelper;
	private String shortcode;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audiomarkdetail);
        
        // Get the shortcode from the intent
        shortcode = getIntent().getExtras().getString("shortcode");
        
        // Do an HTTP request to get the information
        String yoursUrl = getString(R.string.yourls_url) + shortcode;
        new YourlsGetter().execute(yoursUrl);
        
        titleView = (TextView) findViewById(R.id.audiomark_detail_title);
        subtitleView = (TextView) findViewById(R.id.audiomark_detail_subtitle);
        urlView = (TextView) findViewById(R.id.audiomark_detail_url);
        radioStationView = (TextView) findViewById(R.id.audiomark_detail_radio_station);
        radioStationUrlView = (TextView) findViewById(R.id.audiomark_detail_radio_station_url);
        detailsView = (TextView) findViewById(R.id.audiomark_detail_details);

	}
	
	
	private class YourlsGetter extends AsyncTask<String, Void, Void> {
        private final HttpClient client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(AudiomarkDetail.this);
        
        protected void onPreExecute() {
            Dialog.setMessage(getString(R.string.yourls_dialog));
            Dialog.show();
        }

        protected Void doInBackground(String... urls) {
            try {
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = client.execute(httpget, responseHandler);
            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }
            
            return null;
        }
        
        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            
            if (Error != null) {
                Toast.makeText(AudiomarkDetail.this, getString(R.string.yours_error), Toast.LENGTH_LONG).show();
            } else {
                // Populate the view with the relevant data
            	JSONObject jsonObj = null;
            	try {
					jsonObj = new JSONObject(Content);
					JSONObject audiomarkObj = jsonObj.getJSONObject("audiomark");
					String title = audiomarkObj.getString("title");
					String subtitle = audiomarkObj.getString("subtitle");
					String url = audiomarkObj.getString("url");
					String radiostation = audiomarkObj.getString("radiostation");
					String radiostationurl = audiomarkObj.getString("radiostationurl");
					String details = audiomarkObj.getString("details");
					
					titleView.setText(title);
					subtitleView.setText(subtitle);
					urlView.setText(url);
					radioStationView.setText(radiostation);
					radioStationUrlView.setText(radiostationurl);
					detailsView.setText(details);
					
					// Set some onClickListners for the TextViews which contain URLs
					urlView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							String url;
							if (urlView.getText().toString().startsWith("http://")) { 
								url = urlView.getText().toString();
							} else {
								url = "http://" + urlView.getText().toString(); 
							}
							
							Intent browserIntent = new Intent(Intent.ACTION_VIEW); 
					    	Uri uri = Uri.parse(url);
					    	browserIntent.setData(uri); 
					    	startActivity(browserIntent);
						}
					});
					
					radioStationUrlView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							String url;
							if (radioStationUrlView.getText().toString().startsWith("http://")) { url = radioStationUrlView.getText().toString(); }
							else 																{ url = "http://" + radioStationUrlView.getText().toString(); }
							
							Intent browserIntent = new Intent(Intent.ACTION_VIEW); 
					    	Uri uri = Uri.parse(url);
					    	browserIntent.setData(uri); 
					    	startActivity(browserIntent);
						}
					});
					
					// See if the audiomark needs caching					
					dbHelper = new AudiomarkDBHelper(AudiomarkDetail.this);

					if (!dbHelper.audiomarkCacheExists(shortcode)) {
						// Cache the shortcode
						Audiomark audiomark = new Audiomark();
						audiomark.setShortcode(shortcode);
						audiomark.setTitle(title);
						audiomark.setSubtitle(subtitle);
						audiomark.setUrl(url);
						audiomark.setRadio_station(radiostation);
						audiomark.setRadio_station_url(radiostationurl);
						audiomark.setDetails(details);
						
						dbHelper.updateAudiomark(audiomark);
					}
					
					dbHelper.close();
					
					
				} catch (Exception e) { }
				
				
            }
        }
        
    }

}



