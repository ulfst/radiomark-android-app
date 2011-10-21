package org.ajack.audiomark;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	private final int AM_NOTIFICATION_ID = 1337;
	
	private NotificationManager mNotificationManager;
	private boolean isScanning = false;
	

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
        
        // Instantiate a nofitication manager object
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Make the checkbox do something when we change its state
        CheckBox mCheckbox = (CheckBox) findViewById(R.id.home_scanning_checkbox);
        mCheckbox.setChecked(isScanning);
        mCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isScanning = true;
					// Start the listener service
					//startService(new Intent(HomeActivity.this, AudiomarkListenerService.class));
					
					// Show the notification
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notification = new Notification(R.drawable.logo, "Radiomark is listening for audiomarks", System.currentTimeMillis());
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					long[] vibrate = {0,100,200,300};
					notification.vibrate = vibrate;
					notification.flags |= Notification.FLAG_NO_CLEAR;
					
					Context context = getApplicationContext();
					CharSequence contentTitle = "Radiomark";
					CharSequence contentText = "Radiomark is listening for audiomarks";
					Intent notificationIntent = new Intent(HomeActivity.this, HomeActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);

					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					mNotificationManager.notify(AM_NOTIFICATION_ID, notification);
					
				} else {
					isScanning = false;
					//stopService(new Intent(HomeActivity.this, AudiomarkListenerService.class));
					
					// Show a toast notification
					Toast.makeText(HomeActivity.this, "Your phone has stopped scanning for Audiomarks", Toast.LENGTH_LONG).show();
					
					// Cancel the notification
					mNotificationManager.cancel(AM_NOTIFICATION_ID);
				}
			}
        });
        
        RelativeLayout myRadiomarks = (RelativeLayout) findViewById(R.id.home_my_radiomarks);
        myRadiomarks.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		startActivity(new Intent(HomeActivity.this, MyRadioMarksActivity.class));	
        	}
        });

        
    }
    
    public void onDestroy() {
    	super.onDestroy();
    	mNotificationManager.cancel(AM_NOTIFICATION_ID);
    }
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
		  super.onCreateOptionsMenu(menu);
		  
		  menu.add(Menu.NONE, 0, Menu.NONE, "Exit");
		  //MenuItem itemThree = menu.add(Menu.NONE, 0, Menu.NONE, "Exit");
		  //itemThree.setIcon(R.);
		  
		  return true;
	}
    
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case 0:
	    		System.exit(0);
	    		return true;
	    }
		return false;
	}
}