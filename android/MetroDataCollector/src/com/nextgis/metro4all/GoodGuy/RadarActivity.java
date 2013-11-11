package com.nextgis.metro4all.GoodGuy;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nextgis.metro4all.GoodGuy.utils.SubLineAdapter;
import com.nextgis.metro4all.GoodGuy.utils.SubStationAdapter;
import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;
import com.nextgis.metro4all.GoodGuy.utils.db.DBWrapper;

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
					mScanningText.setText(String.format("Миссия выполнена! Station id: %d network cell location: %s", mSubStation.getId_station(), cellLocation.toString()));
					mNextMissionButton.setVisibility(View.VISIBLE);
					mRadar.setVisibility(View.GONE);
					List<NeighboringCellInfo> neighbors = mTelephonyManager.getNeighboringCellInfo();
					for(NeighboringCellInfo neighbor: neighbors) {
						Log.d(TAG, String.format("Station id: %d nearest cell: %s", mSubStation.getId_station(), neighbor.toString()));
					}
					mDB.addcelldata(Calendar.getInstance().getTime(), mSubStation.getId_station(), mSubStation.getId_line(), cellLocation.getCid(), cellLocation.getLac(), cellLocation.getPsc(), "unknown");
				} else {
					mRadar.setVisibility(View.GONE);
					mScanningText.setText("Миссия провалена! Не могу получить данные. Попробуйте еще раз");
				}
				
			}
		}
	};
	
	private Handler mTelephonyHandler;

	private ProgressBar mRadar;

	private TextView mScanningText;

	private Button mNextMissionButton;

	private DBWrapper mDB;

	private SubStation mSubStation;

	private Spinner radarStationSpinner;

	private Spinner radarLineSpinner;

	protected boolean radarLineSpinnerFromUser = false;

	protected boolean radarStationSpinnerFromUser = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		// Show the Up button in the action bar.
		
		mDB = new DBWrapper(this);
		mDB.open();
		
		mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		
		
		mRadar = (ProgressBar) findViewById(R.id.radarProgress);
		mScanningText = (TextView) findViewById(R.id.radarTitle);
		radarLineSpinner = (Spinner) findViewById(R.id.radarStationSpinner);
		radarLineSpinner.setAdapter(new SubLineAdapter(this, mDB));
		radarLineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = mDB.getlines(id);
				c.moveToFirst();
				if(c.getCount() > 0) {
					int lineId = c.getInt(c.getColumnIndexOrThrow(DBHelper.LINES_ID_LINE_COLUMN));
					radarStationSpinner.setAdapter(new SubStationAdapter(RadarActivity.this, mDB, lineId));
					radarStationSpinner.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				radarStationSpinner.setVisibility(View.INVISIBLE);
				
			}
		});
		radarStationSpinner = (Spinner) findViewById(R.id.radarLineSpinner);
		radarStationSpinner.setVisibility(View.INVISIBLE);
		radarStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = mDB.getstations(id);
				c.moveToFirst();
				if(c.getCount() > 0) {
					mSubStation = SubStation.fromCursor(c);
					mNextMissionButton.setText("Сканировать...");
					mNextMissionButton.setVisibility(View.VISIBLE);
				} else {
					mSubStation = null;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mScanningText.setText("Выберите линию метро и станцию, на которой вы сейчас находитесь...");
				mRadar.setVisibility(View.INVISIBLE);
			}
		});
		mScanningText.setText("Выберите линию метро и станцию, на которой вы сейчас находитесь...");
		mRadar.setVisibility(View.INVISIBLE);
		mNextMissionButton = (Button) findViewById(R.id.radarMissionButton);
		mNextMissionButton.setVisibility(View.GONE);
		mNextMissionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mSubStation == null) {
					mScanningText.setText("Что-то пошло не так... Не могу понять что, попробуйте еще раз...");
					mNextMissionButton.setVisibility(View.VISIBLE);
				} else {
					mScanningText.setText(R.string.radarTitle);
					mRadar.setVisibility(View.VISIBLE);
					mTelephonyHandler = new Handler();
					mTelephonyHandler.postDelayed(mTelephonyStateRunnable, 3000);
				}
			}
		});
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

		MenuItem sendItem = menu.findItem(R.id.radarSendData);

		MenuItemCompat.setShowAsAction(sendItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
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
		case R.id.radarSendData : {
			new CellDataExportTask(this, new CellDataExportTask.CellDataExportListener() {
				
				@Override
				public void onCellDataExportFinished(String csvFile) {
					// TODO Auto-generated method stub
					Intent shareIntent = new Intent();
					shareIntent.setAction(Intent.ACTION_SEND);

					shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(csvFile)));
//					shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"suntehnik@gmail.com"});
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CellInfo CSV Data");
					shareIntent.setType("text/plain");
					startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
				}
			}).execute();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
