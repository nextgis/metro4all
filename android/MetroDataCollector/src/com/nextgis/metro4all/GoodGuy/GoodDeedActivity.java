package com.nextgis.metro4all.GoodGuy;

import com.nextgis.metro4all.GoodGuy.utils.Consts;
import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;
import com.nextgis.metro4all.GoodGuy.utils.db.DBWrapper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class GoodDeedActivity extends Activity {

	private DBWrapper mDB;
	private SubStation station;
	private SubLine line;
	private long stationId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_good_deed);
		Intent intent = getIntent();
		mDB = new DBWrapper(this);
		mDB.open();
		if(intent.hasExtra(Consts.PARAM_STATION_ID)) {
			
			stationId = intent.getLongExtra(Consts.PARAM_STATION_ID, -1);
			
		}
		Cursor c = mDB.getstations(stationId);
		c.moveToFirst();
		station = SubStation.fromCursor(c);
		c.close();
		c = mDB.getLineByLineId(station.getId_line());
		c.moveToFirst();
		line = SubLine.fromCursor(c);
		TextView tv = (TextView) findViewById(R.id.gootText);
		tv.setText(String.format(getString(R.string.goodTask), station.getName(), line.getName()));
		// Show the Up button in the action bar.
		((Button) findViewById(R.id.goodGoNext)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startRadarActivity();
			}
		});;
		setupActionBar();
	}

	protected void startRadarActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, RadarActivity.class);
		intent.putExtra(Consts.PARAM_STATION_ID, stationId);
		startActivity(intent);
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.good_deed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
