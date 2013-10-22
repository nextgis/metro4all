package com.nextgis.metro4all.GoodGuy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.nextgis.metro4all.GoodGuy.utils.Consts;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;

public class CitySelectionActivity extends ActionBarActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public static final String TAG = CitySelectionActivity.class.getSimpleName();

	private Spinner citySelector;

	private ConnectionResult connectionResult;

	private LocationClient locationClient;

	private Handler playServicesTimeoutHandler;

	private Runnable playServicesTimeoutRunnable;
	private TextView cityName;
	public int cityIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_selection);
		// Show the Up button in the action bar.
		setupActionBar();
		(citySelector = (Spinner) findViewById(R.id.citySelector))
				.setVisibility(View.GONE);
		(cityName = (TextView) findViewById(R.id.cityText))
				.setVisibility(View.INVISIBLE);
		
		Button cityGoNextButton = (Button) findViewById(R.id.cityGoNext);
		cityGoNextButton.setVisibility(View.GONE);
		cityGoNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 
				goNext();
			}
		});
		locationClient = new LocationClient(this, this, this);
		playServicesTimeoutHandler = new Handler();
		playServicesTimeoutRunnable = new Runnable() {

			@Override
			public void run() {
				// 
				if (servicesConnected()) {
					updateLocation();
				} else {
					locationUpdateFailed();
				}
			}

		};
		playServicesTimeoutHandler.postDelayed(playServicesTimeoutRunnable,
				CONNECTION_FAILURE_RESOLUTION_REQUEST);
	}

	protected void goNext() {
		// Start DB extract task and finish current activity on success
		
		
		
		final DBExpandTask dbExpandTask = new DBExpandTask(this);
		dbExpandTask.execute(Consts.CITY_PATH[cityIndex]);
	}

	protected void locationUpdateFailed() {
		// 
		((TextView)findViewById(R.id.cityWelcomeText)).setText(R.string.cityWelcomeNotFound);
		findViewById(R.id.cityProgressBar).setVisibility(View.GONE);
		citySelector.setVisibility(View.VISIBLE);
		citySelector
				.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_expandable_list_item_1, Consts.CITIES));
		citySelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> selector, View arg1,
					int position, long id) {
				// 
				cityIndex = (int)id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void updateLocation() {
		// 
		Location mCurrentLocation;
		mCurrentLocation = locationClient.getLastLocation();
		if(mCurrentLocation != null) {
			GetAddressTask locationEncoderTask = new GetAddressTask();
			locationEncoderTask.execute(mCurrentLocation);
		} else {
			locationUpdateFailed();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		locationClient.disconnect();
		super.onStop();
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
		getMenuInflater().inflate(R.menu.city_selection, menu);
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

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Location Updates");
			}
		}
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult pConnectionResult) {
		// 
		connectionResult = pConnectionResult;
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	private void showErrorDialog(int errorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		playServicesTimeoutHandler
				.removeCallbacks(this.playServicesTimeoutRunnable);
		updateLocation();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	public class GetAddressTask extends AsyncTask<Location, Void, String> {

		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(getBaseContext(),
					Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 30);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				String city = null;
				for (Address address : addresses) {
					if (address.getLocality() != null) {
						city = address.getLocality();
						break;
					}
				}

				return city;
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				locationUpdateFailed();
			}
			cityIndex = -1;
			for (int i = 0; i < Consts.CITIES.length; i++) {
				if (Consts.CITIES[i].equals(result)) {
					findViewById(R.id.cityProgressBar).setVisibility(View.GONE);
					cityIndex = i;
					break;
				}
			}
			if (cityIndex == -1) {
				locationUpdateFailed();
			} else {
				cityName.setText(Consts.CITIES[cityIndex]);
				cityName.setVisibility(View.VISIBLE);
				findViewById(R.id.cityProgressBar).setVisibility(View.GONE);
				findViewById(R.id.cityGoNext).setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Require @String as argument, path to ZIP file with mertoaccess.sqlite3 database file inside
	 * @author valetin
	 *
	 */
	public class DBExpandTask extends AsyncTask<String, Integer, String> {
		private String DB_PATH;
		
		private ProgressDialog dbExpandProgressDialog;
		private Context mContext;
		
		@SuppressLint("SdCardPath")
		public DBExpandTask(Context context) {
			mContext = context;
			DB_PATH = String.format("/data/data/%s/databases/", context.getPackageName());
			dbExpandProgressDialog = new ProgressDialog(context);
			dbExpandProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dbExpandProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отменить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 
					DBExpandTask.this.cancel(true);
				}
			});
			dbExpandProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
		}
		
		@Override
		protected void onPreExecute() {
			dbExpandProgressDialog.show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... progresses) {
			int progress = (int)progresses[0];
			int max = (int) progresses[1];
			dbExpandProgressDialog.setMax(max);
			dbExpandProgressDialog.setProgress(progress);
		}
		@Override
		protected String doInBackground(String... arg0) {
			String assetPath = arg0[0];
			try {
//				BufferedInputStream bis = new BufferedInputStream();
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(mContext.getAssets().open(String.format("%s/%s", assetPath, "metroaccess.zip.bin"))));
				ZipEntry zipEntry = zis.getNextEntry();
				while(zipEntry != null && ! zipEntry.getName().equals("metroaccess.sqlite3")) {
					zipEntry = zis.getNextEntry();
				}
				if(zipEntry != null) {
					long size = zipEntry.getSize();
					long processed = 0;
					int count = 0;
					byte buffer[] = new byte[1024];
					File dbFile = new File(String.format("%s/%s", this.DB_PATH, "metroaccess.sqlite3"));
					dbFile.getParentFile().mkdirs();
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dbFile));
					while((count = zis.read(buffer, 0, 1024)) > 0) {
						bos.write(buffer, 0, count);
						processed += count;
						this.publishProgress(Integer.valueOf((int)processed), Integer.valueOf((int)size));
					}
					bos.flush();
					bos.close();
					zis.close();
					return mContext.getString(R.string.ok);
				}
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result != null) {
				dbExpandProgressDialog.dismiss();
				Log.d(TAG,  "Database extracted OK");
				getIntent().putExtra(Consts.PREFS_CITY, Consts.CITIES[cityIndex]);
				setResult(RESULT_OK, getIntent());
				finish();
			} else {
				dbExpandProgressDialog.dismiss();
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
				alertBuilder.setMessage(R.string.cityAlertErrorMsg)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 
						dialog.dismiss();
						setResult(RESULT_CANCELED, getIntent());
						finish();
					}
				});
			}
			
		}
		
	}
}
