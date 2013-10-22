package com.nextgis.metro4all.GoodGuy;

import com.nextgis.metro4all.GoodGuy.utils.Consts;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	
	private static final int ACTIVITY_CITY_SELECTION_ID = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button mainButtonGoNext = (Button) findViewById(R.id.mainButtonGoNext);
		final SharedPreferences prefs = this.getSharedPreferences(Consts.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		mainButtonGoNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 
				if(prefs.contains(Consts.PREFS_CITY)) {
					startDataCollection();
				} else {
					startDBPrepareActivity();
				}
			}
		});
	}
	
	private void startDBPrepareActivity() {
		Intent intent = new Intent(this, CitySelectionActivity.class);
		startActivityForResult(intent, ACTIVITY_CITY_SELECTION_ID);
	}
	
	private void startDataCollection() {
		Intent intent = new Intent(this, RadarActivity.class);
//		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
		case ACTIVITY_CITY_SELECTION_ID: {
			if(resultCode == RESULT_OK) {
				Bundle bundle = intent.getExtras();
				String city = bundle.getString(Consts.PREFS_CITY);
				SharedPreferences prefs = getSharedPreferences(Consts.PREFS_FILE_NAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Consts.PREFS_CITY, city).commit();
				startDataCollection();
			} else {
				finish();
			}
			break;
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
