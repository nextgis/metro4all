package com.nextgis.metro4all.GoodGuy;

import com.nextgis.metro4all.GoodGuy.utils.Consts;
import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class RadarActivity extends Activity {

	protected static final String TAG = RadarActivity.class.getSimpleName();
	
	private TelephonyManager mTelephonyManager;
	private Runnable mTelephonyStateRunnable = new Runnable() {
		@Override
		public void run() {
			if(mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM &&
					// check we are connected to the network...
					mTelephonyManager.getNetworkOperatorName() != null) {
				if(mTelephonyManager.getCellLocation() instanceof GsmCellLocation) {
					GsmCellLocation cellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
					Log.d(TAG, String.format("Station id: %d network cell location: cid=%d lac=%d", mSubStation.getId_station(), cellLocation.getCid(), cellLocation.getLac()));
					onPostRadarData(mSubStation.getId_station(), cellLocation.getCid(), cellLocation.getLac());
					mScanningText.setText("Миссия выполнена! Сканирование завершено успешно!");
					mNextMissionButton.setVisibility(View.VISIBLE);
					mRadar.setVisibility(View.INVISIBLE);
				} else {
					
				}
				
			}
		}
	};
	
	private Handler mTelephonyHandler;

	private ProgressBar mRadar;

	private TextView mScanningText;

	private Button mNextMissionButton;

	private DBHelper mDB;

	private SubStation mSubStation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		// Show the Up button in the action bar.
		mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		
		
		mRadar = (ProgressBar) findViewById(R.id.radarProgress);
		mScanningText = (TextView) findViewById(R.id.radarTitle);
		mNextMissionButton = (Button) findViewById(R.id.radarMissionButton);
		mNextMissionButton.setVisibility(View.GONE);
		mNextMissionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NavUtils.navigateUpFromSameTask(RadarActivity.this);
			}
		});
		Intent intent = getIntent();
		if(intent.hasExtra(Consts.PARAM_STATION_ID)) {
			mDB = new DBHelper(this);
			mDB.open();
			Cursor c = mDB.getstations(intent.getLongExtra(Consts.PARAM_STATION_ID, -1));
			c.moveToFirst();
			if(c.getCount() > 0) {
				mSubStation = SubStation.fromCursor(c);
			}
		}
		
		if(mSubStation == null) {
			mScanningText.setText("Что-то пошло не так... Не могу понять что, попробуйте еще раз...");
			mRadar.setVisibility(View.INVISIBLE);
			mNextMissionButton.setVisibility(View.VISIBLE);
		} else {
			mTelephonyHandler = new Handler();
			mTelephonyHandler.postDelayed(mTelephonyStateRunnable, 3000);
		}
		setupActionBar();
	}

	protected void onPostRadarData(int id_station, int cid, int lac) {
		// TODO Auto-generated method stub
		
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
		getMenuInflater().inflate(R.menu.radar, menu);
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
