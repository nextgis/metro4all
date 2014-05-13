/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Author:   Baryshnikov Dmitriy (aka Bishop), polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013,2014 NextGIS
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nextgis.metroaccess.data.DownloadData;
import com.nextgis.metroaccess.data.GraphDataItem;
import com.nextgis.metroaccess.data.MAGraph;

public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_USER_TYPE = "user_type";
	public static final String KEY_PREF_MAX_WIDTH = "max_width";
	public static final String KEY_PREF_WHEEL_WIDTH = "wheel_width";
	public static final String KEY_PREF_DOWNLOAD_PATH = "download_path";
	public static final String KEY_PREF_UPDROUTEDATA = "update_route_data";
	public static final String KEY_PREF_CHANGE_CITY_BASES = "change_city_bases";
	public static final String KEY_PREF_DATA_LOCALE = "data_loc";
	public static final String KEY_PREF_HAVE_LIMITS = "limits";
	public static final String KEY_PREF_CITY = "city";
	public static final String KEY_PREF_CITYLANG = "city_lang";
	public static final String KEY_PREF_MAX_ROUTE_COUNT = "max_route_count";
	
	protected List<DownloadData> m_asDownloadData;
	protected static Handler m_oGetJSONHandler; 
	
	protected ListPreference m_CityLangPref;
	protected ListPreference m_CityPref;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        
        m_asDownloadData = new ArrayList<DownloadData>();
		
		m_oGetJSONHandler = new Handler() {
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
            		if(!m_asDownloadData.isEmpty()){            			
            			OnDownloadData();
            		}
            		else{
            			if(!MainActivity.GetGraph().IsEmpty()){
            				m_CityPref.setEnabled(true);
            			}
            		}
            		break;
        		default:
        			break;
            	}
            }
        };
        
        addPreferencesFromResource(R.xml.preferences);
        
	    /*Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	    	aoRouteMetadata = (List<String>) extras.getSerializable(MainActivity.BUNDLE_METAMAP_KEY);
	    }   */     
        
        /*mlsNaviType = (ListPreference) findPreference(KEY_PREF_USER_TYPE);
        
        int index = Integer.parseInt((String) mlsNaviType.getValue()) - 1;           
        if(index >= 0){
        	mlsNaviType.setSummary((String) mlsNaviType.getEntries()[index]);
        }
        */
        EditTextPreference etMaxWidth = (EditTextPreference) findPreference(KEY_PREF_MAX_WIDTH);
        etMaxWidth.setSummary((String) etMaxWidth.getText() + " " + getString(R.string.sCM));
	    
        EditTextPreference etWheelWidth = (EditTextPreference) findPreference(KEY_PREF_WHEEL_WIDTH);
	    etWheelWidth.setSummary((String) etWheelWidth.getText() + " " + getString(R.string.sCM));
  
	    //add button update data
	    PreferenceCategory targetCategory = (PreferenceCategory)findPreference("data_cat");
	    
	    MAGraph oGraph = MainActivity.GetGraph();
	    
	    m_CityPref = (ListPreference) findPreference(KEY_PREF_CITY);
        if(m_CityPref != null){
        	Map<String, GraphDataItem> oRouteMetadata = oGraph.GetRouteMetadata();        	
        	if(oRouteMetadata.size() > 0){
        		CharSequence[] ent = new CharSequence[oRouteMetadata.size()];
        		CharSequence[] ent_val = new CharSequence[oRouteMetadata.size()];
        		int nCounter = 0;
        		for (Map.Entry<String, GraphDataItem> entry : oRouteMetadata.entrySet()) {
    				ent[nCounter] = entry.getValue().GetLocaleName();
    				ent_val[nCounter] = entry.getKey();
    				nCounter++;
				}
        		
        		m_CityPref.setEntries(ent);
        		m_CityPref.setEntryValues(ent_val);
        	}
        	else{
        		m_CityPref.setEnabled(false);
        	}
            int index = m_CityPref.findIndexOfValue( m_CityPref.getValue() );           
            if(index >= 0){
            	m_CityPref.setSummary(m_CityPref.getEntries()[index]);
            }
            else{
            	m_CityPref.setSummary((String) m_CityPref.getSummary()); //.getValue()
            }
        }
        
        m_CityLangPref = (ListPreference) findPreference(KEY_PREF_CITYLANG);
        if(m_CityLangPref != null){
            int index = m_CityLangPref.findIndexOfValue( m_CityLangPref.getValue() );           
            if(index >= 0){
            	m_CityLangPref.setSummary(m_CityLangPref.getEntries()[index]);
            }
            else{
            	m_CityLangPref.setSummary((String) m_CityLangPref.getSummary()); 
            }
        }
        
	    /*
		File file = new File(getExternalFilesDir(null), MainActivity.GetRemoteMetaFile());
		String sPayload = MainActivity.readFromFile(file);
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
		        		m_sUrl = sharedPref.getString(KEY_PREF_DOWNLOAD_PATH, MainActivity.GetDownloadURL());
		        		
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
		    						
									masDownloadData.add(new DownloadData(PreferencesActivity.this, sName, sPath, sLocName, m_sUrl + sPath + ".zip", nVer, bDirected, moGetJSONHandler));
									
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
		}		 */
	    
	    
	    Preference checkUpd = new Preference(this);
	    checkUpd.setKey(KEY_PREF_UPDROUTEDATA);
	    checkUpd.setTitle(R.string.sPrefUpdDataTitle);
	    checkUpd.setSummary(R.string.sPrefUpdDataSummary);
	    checkUpd.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);		
        		String sUrl = sharedPref.getString(KEY_PREF_DOWNLOAD_PATH, MainActivity.GetDownloadURL());
        		
        		m_asDownloadData.clear();
        		
        		MAGraph oGraph = MainActivity.GetGraph();
        		
        		for(GraphDataItem oItem : oGraph.GetRouteMetadata().values()){
        			m_asDownloadData.add(new DownloadData(PreferencesActivity.this, oItem, sUrl + oItem.GetPath() + ".zip", m_oGetJSONHandler));
        		}

        		OnDownloadData();
        		
				return true;
        	}
        });
	    
	    targetCategory.addPreference(checkUpd);
	    
	    Preference changeCityBases = new Preference(this);
	    changeCityBases.setKey(KEY_PREF_CHANGE_CITY_BASES);
	    changeCityBases.setTitle(R.string.sPrefChangeCityBasesTitle);
	    changeCityBases.setSummary(R.string.sPrefChangeCityBasesSummary);
	    changeCityBases.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		//Add and remove bases
        		
        		MAGraph oGraph = MainActivity.GetGraph();
        		final List<GraphDataItem> new_items = oGraph.HasChanges();
        		final List<GraphDataItem> exist_items = new ArrayList<GraphDataItem>(oGraph.GetRouteMetadata().values()); 
    		    
        	    int count = new_items.size() + exist_items.size();
        	    if(count == 0)
        	    	return false;
        	    
        	    final boolean[] checkedItems = new boolean[count];
        	    final CharSequence[] checkedItemStrings = new CharSequence[count];
        	    
        	    for(int i = 0; i < new_items.size(); i++){
        	    	checkedItems[i] = false;
        	    }
        	    
        	    for(int i = 0; i < new_items.size(); i++){
        	    	checkedItemStrings[i] = new_items.get(i).GetFullName();
        	    }
        	    
        	    for(int i = 0; i < exist_items.size(); i++){
        	    	checkedItems[i + new_items.size()] = true;
        	    }
        	    
        	    for(int i = 0; i < exist_items.size(); i++){
        	    	checkedItemStrings[i + new_items.size()] = exist_items.get(i).GetLocaleName();
        	    }       	    
        	    
        	    
        	    AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
        		builder.setTitle(R.string.sSelectDataToDownload)
        			   .setCancelable(false)
        			   .setMultiChoiceItems(checkedItemStrings, checkedItems,
        						new DialogInterface.OnMultiChoiceClickListener() {
        							@Override
        							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        								checkedItems[which] = isChecked;
        							}
        						})
        				.setPositiveButton(R.string.sDownload,
        						new DialogInterface.OnClickListener() {
        							@Override
        							public void onClick(DialogInterface dialog, int id) {
        								
        								m_asDownloadData.clear();
        								
        								for (int i = 0; i < checkedItems.length; i++) {
        									//check if no change
        									//1. item is unchecked and was unchecked
        									if(!checkedItems[i] && i < new_items.size()){
        										continue;
        									}
        									else if(i < new_items.size()){
        										m_asDownloadData.add(new DownloadData(PreferencesActivity.this, new_items.get(i), MainActivity.GetDownloadURL() + new_items.get(i).GetPath() + ".zip", m_oGetJSONHandler));										
        									}
        									//2. item is checked and was checked
        									else if (checkedItems[i] && i >= new_items.size()){
        										continue;
        									}
        									else{//delete
        										File oDataFolder = new File(MainActivity.GetRouteDataDir(), exist_items.get(i + new_items.size()).GetPath());
        										DeleteRecursive(oDataFolder);
        									}
        								}
        								OnDownloadData();
        							}
        						})

        				.setNegativeButton(R.string.sCancel,
        						new DialogInterface.OnClickListener() {
        							@Override
        							public void onClick(DialogInterface dialog, int id) {
        								dialog.cancel();

        							}
        						});
        		builder.create();
        		builder.show();
				return true;
        	}
        });
	    
	    targetCategory.addPreference(changeCityBases);
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
		else if(key.equals(KEY_PREF_CITY)){
			newVal = sharedPreferences.getString(key, "msk");
			int nIndex = m_CityPref.findIndexOfValue((String) newVal);
            if(nIndex >= 0){
            	m_CityPref.setSummary((String) m_CityPref.getEntries()[nIndex]);
            }
            MainActivity.GetGraph().SetCurrentCity((String) newVal);
            return;
		}
		else if(key.equals(KEY_PREF_CITYLANG)){
			newVal = sharedPreferences.getString(key, "en");
			int nIndex = m_CityLangPref.findIndexOfValue((String) newVal);
            if(nIndex >= 0){
            	m_CityLangPref.setSummary((String) m_CityLangPref.getEntries()[nIndex]);
            }
            MainActivity.GetGraph().SetLocale((String) newVal);
            return;
			
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
        }
		else if(key.equals(KEY_PREF_HAVE_LIMITS)){
			boolean bNewVal = sharedPreferences.getBoolean(key, false);
			
		}	*/
		else if(key.equals(KEY_PREF_DOWNLOAD_PATH)){
			String sUrl = sharedPreferences.getString(key, MainActivity.GetDownloadURL());			
    		if(sUrl.length() > 0){
            	Pref.setSummary(sUrl);	
            	MainActivity.SetDownloadURL(sUrl);
    		}
		}
		/*else if(key.startsWith("db_")){
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
						
						m_sUrl = sharedPreferences.getString(KEY_PREF_DOWNLOAD_PATH, MainActivity.GetDownloadURL());
						
						DataDownloader uploader = new DataDownloader(this, sPath, sName, sLocName, nVer, bDirected, getResources().getString(R.string.sDownLoading), null);
						uploader.execute(m_sUrl + sPath + ".zip");
						

            			
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
		}*/
	}	
	
	/*
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
	}*/
	
	protected void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	protected void OnDownloadData(){
		if(m_asDownloadData.isEmpty())
			return;
		DownloadData data = m_asDownloadData.get(0);
		m_asDownloadData.remove(0);
		
		data.OnDownload();
	}
}