/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Author:   Baryshnikov Dmitriy (aka Bishop), polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013 NextGIS
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/
package com.nextgis.metroaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_USER_TYPE = "user_type";
	public static final String KEY_PREF_MAX_WIDTH = "max_width";
	public static final String KEY_PREF_WHEEL_WIDTH = "wheel_width";
	public static final String KEY_PREF_DOWNLOAD_PATH = "download_path";
	public static final String KEY_PREF_UPDROUTEDATA = "update_route_data";
	
	//ListPreference mlsNaviType;
	protected EditTextPreference metWheelWidth;
	protected EditTextPreference metMaxWidth;
	protected EditTextPreference metDownloadPath;
	
	protected Map<String, JSONObject> moRemoteData;  
	protected Map<String, CheckBoxPreference> mDBs;
	
	protected String msUrl;
	
	protected List<String> aoRouteMetadata;
	
	protected List<DownloadData> masDownloadData;
	protected static Handler moGetJSONHandler; 
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        
        masDownloadData = new ArrayList<DownloadData>();
		
		moGetJSONHandler = new Handler() {
            public void handleMessage(Message msg) {
            	super.handleMessage(msg);
            	
            	Bundle resultData = msg.getData();
            	boolean bHaveErr = resultData.getBoolean(MainActivity.BUNDLE_ERRORMARK_KEY);
            	int nEventSource = resultData.getInt(MainActivity.BUNDLE_EVENTSRC_KEY);
            	String sPayload = resultData.getString(MainActivity.BUNDLE_PAYLOAD_KEY);
            	if(bHaveErr){
            		Toast.makeText(PreferencesActivity.this, resultData.getString(MainActivity.BUNDLE_MSG_KEY), Toast.LENGTH_LONG).show();
            	}

            	switch(nEventSource){
            	case 2:            		
            		if(!masDownloadData.isEmpty()){            			
            			OnDownloadData();
            		}
            		break;
        		default:
        			break;
            	}
            }
        };
        
        addPreferencesFromResource(R.xml.preferences);
        
	    Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	    	aoRouteMetadata = (List<String>) extras.getSerializable(MainActivity.BUNDLE_METAMAP_KEY);
	    }        
        
        /*mlsNaviType = (ListPreference) findPreference(KEY_PREF_USER_TYPE);
        
        int index = Integer.parseInt((String) mlsNaviType.getValue()) - 1;           
        if(index >= 0){
        	mlsNaviType.setSummary((String) mlsNaviType.getEntries()[index]);
        }
        */
        metMaxWidth = (EditTextPreference) findPreference(KEY_PREF_MAX_WIDTH);
        metMaxWidth.setSummary((String) metMaxWidth.getText() + " " + getString(R.string.sCM));
	    
	    metWheelWidth = (EditTextPreference) findPreference(KEY_PREF_WHEEL_WIDTH);
	    metWheelWidth.setSummary((String) metWheelWidth.getText() + " " + getString(R.string.sCM));
	    
	    //metDownloadPath = (EditTextPreference) findPreference(KEY_PREF_DOWNLOAD_PATH);
	    //msUrl = (String) metDownloadPath.getText();
	    //metDownloadPath.setSummary(msUrl);
	    
	    //add button update data
	    PreferenceCategory targetCategory = (PreferenceCategory)findPreference("data_cat");
	    
		File file = new File(getExternalFilesDir(null), MainActivity.REMOTE_METAFILE);
		String sPayload = MainActivity.readFromFile(file, this);
		moRemoteData = new HashMap<String, JSONObject>();
		mDBs = new HashMap<String, CheckBoxPreference>();
		try{
		    	JSONObject oJSONMetaRemote = new JSONObject(sPayload);
				
			    final JSONArray jsonArray = oJSONMetaRemote.getJSONArray("packages");
			    
			    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String sLocaleKeyName = "name_" + Locale.getDefault().getLanguage();
					String sLocName = jsonObject.getString(sLocaleKeyName);	
					String sName = jsonObject.getString("name");
					if(sLocName.length() == 0)
						sLocName = sName;
					// = jsonObject.getInt("ver");
					
					//int nVer = 0;
					
					final String sKey = "db_" + i;
					moRemoteData.put(sKey, jsonObject);
					  
					//check is exist
					CheckBoxPreference db = new CheckBoxPreference(this);
					db.setKey(sKey); //Refer to get the pref value
					db.setTitle(sLocName);
					//db.setSummary("ver." + nVer);
					//
					boolean bChecked = false;
					if(aoRouteMetadata != null){
						for(String sExistName : aoRouteMetadata){
							String[] RowData = sExistName.split(MainActivity.CSV_CHAR);
							String sExName = RowData[0];
							String sVer = RowData[1];
							if(sExName.equals(sName)){
								bChecked = true;
								db.setSummary(sVer);
								break;
							}
						}
					}
					db.setChecked(bChecked);			
					db.setOnPreferenceChangeListener(new MyOnPreferenceChangeListener(sKey));

					targetCategory.addPreference(db);
					
					mDBs.put(sKey,  db);

			    }
			    
			    Preference checkUpd = new Preference(this);
			    checkUpd.setKey(KEY_PREF_UPDROUTEDATA);
			    checkUpd.setTitle(R.string.sPrefUpdDataTitle);
			    checkUpd.setSummary(R.string.sPrefUpdDataSummary);
			    checkUpd.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        	public boolean onPreferenceClick(Preference preference) {
		        		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);		
		        		msUrl = sharedPref.getString(KEY_PREF_DOWNLOAD_PATH, MainActivity.sUrl);
		        		
		        		masDownloadData.clear();
		        		
		        		for(JSONObject jsonObject : moRemoteData.values()){
		        			if(jsonObject != null){	
		        				    				
		    					//download and unzip
		    					try {
		    						int nVer = jsonObject.getInt("ver");
		    					
		    						String sPath = jsonObject.getString("path");
		    						String sName = jsonObject.getString("name");
		    						String sLocName = sName;
		    						if(jsonObject.has("name_" + Locale.getDefault().getLanguage())){
		    							sLocName = jsonObject.getString("name_" + Locale.getDefault().getLanguage());
		    						}
		    						boolean bDirected = false;
		    						if(jsonObject.has("directed")){
		    							bDirected = jsonObject.getBoolean("directed");
		    						}
		    						if(sLocName.length() == 0){
		    							sLocName = sName;
		    						}
		    						

									boolean bChecked = false;
									if(PreferencesActivity.this.aoRouteMetadata != null){
										for(String sExistName : PreferencesActivity.this.aoRouteMetadata){
											String[] RowData = sExistName.split(MainActivity.CSV_CHAR);
											String sExName = RowData[0];
											String sVer = RowData[1];
											if(sExName.equals(sName)){
												bChecked = true;
												break;
											}
										}
									}
									
									if(!bChecked)
										continue;   
		    						
									masDownloadData.add(new DownloadData(PreferencesActivity.this, sName, sPath, sLocName, msUrl + sPath + ".zip", nVer, bDirected, moGetJSONHandler));
									
		    					} catch (JSONException e) {
		    						e.printStackTrace();
		    					}	
		        			}
		        		}

		        		OnDownloadData();
		        		
						return true;
		        	}
		        });
			    
			    targetCategory.addPreference(checkUpd);
			    
		 } 
		 catch (Exception e) {
			 Toast.makeText(this, R.string.sNetworkInvalidData, Toast.LENGTH_LONG).show();
		}		 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
    
	@Override
	protected void onPause() {	
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
		sharedPref.unregisterOnSharedPreferenceChangeListener(this);
		
		super.onPause();
	}
	
    @Override
	public void onResume() {
        super.onResume();
        
 		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
 		sharedPref.registerOnSharedPreferenceChangeListener(this);
    }	
    
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		CharSequence newVal = "";
		Preference Pref = findPreference(key);
		if(key.equals(KEY_PREF_WHEEL_WIDTH) || key.equals(KEY_PREF_MAX_WIDTH))
		{
			newVal = sharedPreferences.getString(key, "40");
        	String toIntStr = (String) newVal;
    		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    		editor.putInt(key + "_int", Integer.parseInt(toIntStr) * 10);
    		editor.commit();
    		
    		if(newVal.length() > 0)
            	Pref.setSummary(newVal  + " " + getString(R.string.sCM));
        }
		/*else if(key.equals(KEY_PREF_USER_TYPE))
		{
			newVal = sharedPreferences.getString(key, "1");
        	String toIntStr = (String) newVal;
    		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    		int index = Integer.parseInt(toIntStr);
    		editor.putInt(key + "_int", index);
    		editor.commit();
    		
    		index--;           
            if(index >= 0){
            	mlsNaviType.setSummary((String) mlsNaviType.getEntries()[index]);
            }
        }	*/
		else if(key.equals(KEY_PREF_DOWNLOAD_PATH)){
			msUrl = sharedPreferences.getString(key, MainActivity.sUrl);			
    		if(msUrl.length() > 0)
            	Pref.setSummary(msUrl);			
		}
		else if(key.startsWith("db_")){
			//update interface
			//set or not set check
			boolean bVal = sharedPreferences.getBoolean(key, true);
			CheckBoxPreference db = mDBs.get(key);
			if(db != null){
				db.setChecked(bVal);
			}

			JSONObject jsonObject = moRemoteData.get(key);
			if(jsonObject != null){
				if(bVal){
					//download and unzip
					try {
						int nVer = jsonObject.getInt("ver");
					
						String sPath = jsonObject.getString("path");
						String sName = jsonObject.getString("name");
						String sLocName = sName;
						if(jsonObject.has("name_" + Locale.getDefault().getLanguage())){
							sLocName = jsonObject.getString("name_" + Locale.getDefault().getLanguage());
						}
						boolean bDirected = false;
						if(jsonObject.has("directed")){
							bDirected = jsonObject.getBoolean("directed");
						}
						if(sLocName.length() == 0){
							sLocName = sName;
						}
						
						msUrl = sharedPreferences.getString(KEY_PREF_DOWNLOAD_PATH, MainActivity.sUrl);
						
						DataDownloader uploader = new DataDownloader(this, sPath, sName, sLocName, nVer, bDirected, getResources().getString(R.string.sDownLoading), null);
						uploader.execute(msUrl + sPath + ".zip");
						

            			
            			aoRouteMetadata.add(sName + ";ver." + nVer);
            			
					} catch (JSONException e) {
						e.printStackTrace();
					}			
				}
				else {
					try {
						String sPath = jsonObject.getString("path");
						String sFullPath = getExternalFilesDir(MainActivity.ROUTE_DATA_DIR) + File.separator + sPath;
						DeleteRecursive(new File(sFullPath));
						
						String sName = jsonObject.getString("name");
						int nVer = jsonObject.getInt("ver");
						aoRouteMetadata.remove(sName + ";ver." + nVer);
					}
					catch (JSONException e) {
						e.printStackTrace();
					}		
				}
    		}
		}
	}	
	
	
	private void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}

	class MyOnPreferenceChangeListener implements OnPreferenceChangeListener{
		protected String msKey;
		protected SharedPreferences mSharedPref;
		
		public MyOnPreferenceChangeListener(String sKey) {
			msKey = sKey;
			mSharedPref = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
		}

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean currentVal = mSharedPref.getBoolean(msKey, false);
			boolean newVal = (Boolean) newValue;
			if(currentVal == newVal){
				return true;
			}
			else if(newVal == true){
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
				builder.setTitle(R.string.sDownload)
				.setMessage(R.string.sDownloadData)
				.setCancelable(false)
				.setPositiveButton(R.string.sDownload, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id){
						Editor editor = MyOnPreferenceChangeListener.this.mSharedPref.edit();
						editor.putBoolean(msKey,  true);
						editor.commit();
					}								
				})
				.setNegativeButton(R.string.sCancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder.create();
				builder.show();
			}
			else  if(newVal == false){
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
				builder.setTitle(R.string.sDelete)
				.setMessage(R.string.sDeleteData)
				.setCancelable(false)
				.setPositiveButton(R.string.sDelete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id){
						Editor editor = MyOnPreferenceChangeListener.this.mSharedPref.edit();
						editor.putBoolean(msKey,  false);
						editor.commit();
					}								
				})
				.setNegativeButton(R.string.sCancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog dlg = builder.create();
				dlg.setCancelable(false);
				dlg.setCanceledOnTouchOutside(false);
				dlg.show();
			}
			return false;
		}
	}
	
	protected void OnDownloadData(){
		if(masDownloadData.isEmpty())
			return;
		DownloadData data = masDownloadData.get(0);
		masDownloadData.remove(0);
		
		data.OnDownload();
	}
}