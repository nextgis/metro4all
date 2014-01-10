package com.nextgis.whichexit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.nextgis.whichexit.NearestExitsListFragment.OnExitSelectedListener;
import com.nextgis.whichexit.PoiListFragment.OnPOISelectedListener;
import com.testflightapp.lib.TestFlight;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WhichExitActivity extends FragmentActivity implements
		OnPOISelectedListener, OnExitSelectedListener {

	public static final String PREFS_DB_EXTRACTED_OK = "db_extracted";

	public static final String TAG = WhichExitActivity.class.getSimpleName();

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	WhichExitMainPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private SharedPreferences prefs;

	private MapController mMapController;

	private EditText mSearchText;

	private PoiListFragment poiListFragment;
	
	private NearestExitsListFragment newarestExitsListFragment;

	private NearestExitsListFragment mExitListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestFlight.takeOff(getApplication(), "ea4fc362-3bca-4aee-8c74-41e0fee92394");
		setContentView(R.layout.activity_which_exit);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new WhichExitMainPagerAdapter(this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);


		
		mSearchText = (EditText) findViewById(R.id.mapSearchText);
		mSearchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					mViewPager.setCurrentItem(1);
					if (poiListFragment == null) {
						Fragment f = mSectionsPagerAdapter.getItem(1);
						if (f != null && f instanceof PoiListFragment) {
							poiListFragment = (PoiListFragment) f;
						} else {
							return;
						}
					}
					poiListFragment.onTextChanged(s, start, before, count);
				}
			}

		});
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int actionId,
					KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_SEND
						|| keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					hideKeyboard(textView);
					
					if (poiListFragment == null) {
						Fragment f = mSectionsPagerAdapter.getItem(1);
						if (f != null && f instanceof PoiListFragment) {
							poiListFragment = (PoiListFragment) f;
						} else {
							return false;
						}
					}
					poiListFragment.startPOISearch();
					return true;
				}
				return false;
			}
		});

		((ImageView)findViewById(R.id.clearSearch)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSearchText.setText("");
			}
		});
		prefs = getSharedPreferences(TAG, Context.MODE_PRIVATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (prefs.contains(PREFS_DB_EXTRACTED_OK)) {
			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setupMapController();
				}

			}, 2000);
		} else {
			new DBExpandTask(this).execute();
		}
	}

	public void setupMapController() {
		if (mMapController == null) {
			// TODO Auto-generated method stub
			Fragment f = mSectionsPagerAdapter.getItem(0);
			if (f != null && f instanceof SupportMapFragment) {
				SupportMapFragment mMapFragment = (SupportMapFragment) f;
				GoogleMap map = mMapFragment.getMap();
				this.mMapController = new MapController(this, map);
				map.setOnCameraChangeListener(mMapController);
				map.setOnMapLoadedCallback(mMapController);
				map.setMyLocationEnabled(true);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						55.751667, 37.617778), 9.5f));
			}
		}
	}

	/**
	 * Require @String as argument, path to ZIP file with mertoaccess.sqlite3
	 * database file inside
	 * 
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
			DB_PATH = String.format("/data/data/%s/databases/",
					context.getPackageName());
			dbExpandProgressDialog = new ProgressDialog(context);
			dbExpandProgressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dbExpandProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
					"Отменить", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//
							DBExpandTask.this.cancel(true);
						}
					});
			dbExpandProgressDialog.setProgressPercentFormat(NumberFormat
					.getPercentInstance());
		}

		@Override
		protected void onPreExecute() {
			dbExpandProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {
			int progress = (int) progresses[0];
			int max = (int) progresses[1];
			dbExpandProgressDialog.setMax(max);
			dbExpandProgressDialog.setProgress(progress);
		}

		@Override
		protected String doInBackground(Void... args) {
			// String assetPath = arg0[0];
			try {
				// BufferedInputStream bis = new BufferedInputStream();
				ZipInputStream zis = new ZipInputStream(
						new BufferedInputStream(mContext.getAssets().open(
								"metroaccess.zip.bin")));
				ZipEntry zipEntry = zis.getNextEntry();
				while (zipEntry != null
						&& !zipEntry.getName().equals("metroaccess.sqlite3")) {
					zipEntry = zis.getNextEntry();
				}
				if (zipEntry != null) {
					long size = zipEntry.getSize();
					long processed = 0;
					int count = 0;
					byte buffer[] = new byte[1024];
					File dbFile = new File(String.format("%s/%s", this.DB_PATH,
							"metroaccess.sqlite3"));
					dbFile.getParentFile().mkdirs();
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(dbFile));
					while ((count = zis.read(buffer, 0, 1024)) > 0) {
						bos.write(buffer, 0, count);
						processed += count;
						this.publishProgress(Integer.valueOf((int) processed),
								Integer.valueOf((int) size));
					}
					bos.flush();
					bos.close();
					zis.close();
					return mContext
							.getString(com.nextgis.whichexit.R.string.ok);
				}
			} catch (IOException e) {
				//
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				dbExpandProgressDialog.dismiss();
				Log.d(TAG, "Database extracted OK");
				// TODO: DB Expanded
				prefs.edit().putBoolean(PREFS_DB_EXTRACTED_OK, true).commit();
				setupMapController();
			} else {
				dbExpandProgressDialog.dismiss();
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						mContext);
				alertBuilder.setMessage(
						com.nextgis.whichexit.R.string.db_extract_error)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										//
										dialog.dismiss();
										setResult(RESULT_CANCELED, getIntent());
										finish();
									}
				});
			}
		}
	}

	@Override
	public void onPOISelected(Address poi) {
		hideKeyboard(mSearchText);
		mViewPager.setCurrentItem(2);
		if(mExitListFragment == null) {
			mExitListFragment = (NearestExitsListFragment) this.mSectionsPagerAdapter.getItem(2);
		}
		mExitListFragment.showExits(poi);
	}

	/**
	 * @param textView
	 */
	private void hideKeyboard(TextView textView) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onExitSelected(SubStationExit exit) {
		// TODO Auto-generated method stub

		mMapController.showExit(exit);
	}

}
