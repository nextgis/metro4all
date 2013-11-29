package com.nextgis.whichexit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;


public class MomentsMapActivity extends FragmentActivity {

	private static final String PREFS_DB_EXTRACTED_OK = "DB_EXTRACTED_OK";
	public static final String TAG = MomentsMapActivity.class.getSimpleName();
	private SharedPreferences prefs;
	private MomentsMapController mMapController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moments_map);
//		FrameLayout mMapsFragmentHolder = (FrameLayout) findViewById(R.id.mMapsFragmentHolder);
		getSupportFragmentManager().beginTransaction().add(R.id.mMapsFragmentHolder, new SupportMapFragment(), "maps").commit();
		prefs = this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!prefs.contains(PREFS_DB_EXTRACTED_OK)) {
			DBExpandTask mDBExpandTask = new DBExpandTask(this);
			mDBExpandTask.execute();
		} else {
			setupMapController();
		}
	}

	private synchronized void setupMapController() {
		if(mMapController != null) {
			// Be safe for race condition between async task and onResume
			return;
		}
		SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("maps");
		if(mMapFragment != null) {
			GoogleMap mGoogleMap = mMapFragment.getMap();
			if(mGoogleMap != null) {
				mMapController = new MomentsMapController(this, mGoogleMap);
				mGoogleMap.setOnCameraChangeListener(mMapController);
				mGoogleMap.setOnMapLoadedCallback(mMapController);
			} else {
				
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mMapController.release();
		mMapController = null;
	}

	
	/**
	 * Require @String as argument, path to ZIP file with mertoaccess.sqlite3 database file inside
	 * @author valetin
	 *
	 */
	public class DBExpandTask extends AsyncTask<Void, Integer, String> {
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
		protected String doInBackground(Void... args) {
//			String assetPath = arg0[0];
			try {
//				BufferedInputStream bis = new BufferedInputStream();
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(mContext.getAssets().open( "metroaccess.zip.bin")));
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
					return mContext.getString(com.nextgis.whichexit.R.string.ok);
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
				// TODO: DB Expanded
				prefs.edit().putBoolean(PREFS_DB_EXTRACTED_OK, true).commit();
				setupMapController();
			} else {
				dbExpandProgressDialog.dismiss();
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
				alertBuilder.setMessage(com.nextgis.whichexit.R.string.db_extract_error)
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
