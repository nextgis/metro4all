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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


public class MainActivity extends SherlockActivity implements OnNavigationListener{
	final static String TAG = "metroaccess";	
	final static String sUrl = "http://gis-lab.info/data/zp-gis/data/ma/";
	final static String META = "meta.json";
	final static String BUNDLE_MSG_KEY = "msg";
	final static String BUNDLE_PAYLOAD_KEY = "json";
	final static String BUNDLE_ERRORMARK_KEY = "error";
	final static String BUNDLE_EVENTSRC_KEY = "eventsrc";
	
	final static String REMOTE_METAFILE = "remotemeta.json";
	final static String ROUTE_DATA_DIR = "rdata";
	
	final static String CURRENT_METRO_SEL = "metro_selection";
	
//TODO:	public final static int MENU_SETTINGS = 4;
	public final static int MENU_ABOUT = 5;

	//public final static int GET_META = 0;
	
	private static Handler moGetJSONHandler; 
	//protected JSONObject moJSONMetaRemote;
	//protected JSONObject moJSONMeta;
	protected HashMap<Integer, JSONObject> mmoRouteMetadata = new HashMap<Integer, JSONObject>();
	protected String msRDataPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // initialize the default settings
//TODO:        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		
		moGetJSONHandler = new Handler() {
            public void handleMessage(Message msg) {
            	super.handleMessage(msg);
            	
            	Bundle resultData = msg.getData();
            	boolean bHaveErr = resultData.getBoolean(BUNDLE_ERRORMARK_KEY);
            	int nEventSource = resultData.getInt(BUNDLE_EVENTSRC_KEY);
            	String sPayload = resultData.getString(BUNDLE_PAYLOAD_KEY);
            	if(bHaveErr){
            		Toast.makeText(MainActivity.this, resultData.getString(BUNDLE_MSG_KEY), Toast.LENGTH_LONG).show();
	            	switch(nEventSource){
	            	case 1://get remote meta
	            		File file = new File(getExternalFilesDir(null), REMOTE_METAFILE);
	            		sPayload = readFromFile(file);
	            		break;
            		default:
            			return;
	            	}
            	}

            	switch(nEventSource){
            	case 1://get remote meta
            		if(IsRoutingDataExist()){
            			//check if updates available
//            			searchButton.setEnabled(true);
//            			demoButton.setEnabled(true);
            		}
            		else{
            			AskForDownloadData(sPayload);
            		}
            		break;
            	case 2://create meta.json in routing data folder
                    String sPath = resultData.getString("path");
                    String sName = resultData.getString("name");
                    String sLocalName = resultData.getString("locname");
                    int nVer = resultData.getInt("ver");

                    JSONObject oJSONRoot = new JSONObject();
                    try {
						oJSONRoot.put("name", sName);
						oJSONRoot.put("name_" + Locale.getDefault().getLanguage(), sLocalName);
						oJSONRoot.put("ver", nVer);
					} catch (JSONException e) {
						e.printStackTrace();
					}
                    
                    String sJSON = oJSONRoot.toString();
                    File file = new File(sPath, META);
                    writeToFile(file, sJSON);
                    
                    LoadInterface();
            		break;
            	}
            }
        };		
		
		//check for data exist
		if(IsRoutingDataExist()){
			//else check for updates
			CheckForUpdates();
			LoadInterface();		
		}
		else{
			//ask to download data
			GetRoutingData();
		}
	}
	
	private void LoadInterface(){
		ArrayList<String> items = new ArrayList<String>();
		File f = new File(getExternalFilesDir(ROUTE_DATA_DIR).getPath());
		File[] files = f.listFiles();
		int nCounter = 0;
		for (File inFile : files) {
		    if (inFile.isDirectory()) {
		        File metafile = new File(inFile, META);
		        if(metafile.isFile()){
		        	String sJSON = readFromFile(metafile);
		        	JSONObject oJSON;
					try {
						oJSON = new JSONObject(sJSON);
			        	String sName = oJSON.getString("name_" + Locale.getDefault().getLanguage());
			        	oJSON.put("path", inFile.getPath());
			        	items.add(sName);
			        	mmoRouteMetadata.put(nCounter, oJSON);
			        	nCounter++;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    }
		}
		
		
	    ActionBar actionBar = getSupportActionBar();
		Context context = actionBar.getThemedContext();		
		ArrayAdapter<CharSequence> adapter= new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_dropdown_item, items.toArray(new String[items.size()]));
		//ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.views, R.layout.sherlock_spinner_dropdown_item);
		adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
	    
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
	    actionBar.setListNavigationCallbacks((SpinnerAdapter)adapter, this);
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    int nCurrentMetro = prefs.getInt(CURRENT_METRO_SEL, 0);
	    actionBar.setSelectedNavigationItem(nCurrentMetro);
	    
	    try {
			msRDataPath = mmoRouteMetadata.get(nCurrentMetro).getString("path");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		//TODO:        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.sSettings)
//      .setIcon(R.drawable.ic_action_settings)
//      .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);		
		menu.add(com.actionbarsherlock.view.Menu.NONE, MENU_ABOUT, com.actionbarsherlock.view.Menu.NONE, R.string.sAbout)
		.setIcon(R.drawable.ic_action_about)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);	
		  
		return true;
//		return super.onCreateOptionsMenu(menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            return false;
//TODO:        case MENU_SETTINGS:
            // app icon in action bar clicked; go home
//            Intent intentSet = new Intent(this, PreferencesActivity.class);
//            intentSet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intentSet);
//            return true;
        case MENU_ABOUT:
            Intent intentAbout = new Intent(this, AboutActivity.class);
            intentAbout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentAbout);
            return true;	  
        }
		return super.onOptionsItemSelected(item);
	}	
	
	//check if data for routing is downloaded
	protected boolean IsRoutingDataExist(){		
		File f = new File(getExternalFilesDir(ROUTE_DATA_DIR).getPath());
		File[] files = f.listFiles();
		for (File inFile : files) {
		    if (inFile.isDirectory()) {
		        File metafile = new File(inFile, META);
		        if(metafile.isFile())
		        	return true;
		    }
		}
		return false;
	}	
	
	protected void CheckForUpdates(){
		MetaDownloader uploader = new MetaDownloader(MainActivity.this, getResources().getString(R.string.sDownLoading), moGetJSONHandler, false);
		uploader.execute(sUrl + META);				
	}
	
	protected void GetRoutingData(){
		MetaDownloader uploader = new MetaDownloader(MainActivity.this, getResources().getString(R.string.sDownLoading), moGetJSONHandler, true);
		uploader.execute(sUrl + META);		
	}

	protected void AskForDownloadData(String sJSON){
		//ask user for download
	    try{
	    	JSONObject oJSONMetaRemote = new JSONObject(sJSON);
	    	
			//save remote meta to file
			if(oJSONMetaRemote != null ){
				File file = new File(getExternalFilesDir(null), REMOTE_METAFILE);
				writeToFile(file, sJSON);
			}
			
		    final JSONArray jsonArray = oJSONMetaRemote.getJSONArray("packages");
		    
		    ArrayList<String> items = new ArrayList<String>();
		    
		    for (int i = 0; i < jsonArray.length(); i++) {
		    	  JSONObject jsonObject = jsonArray.getJSONObject(i);
		    	  String sLocaleKeyName = "name_" + Locale.getDefault().getLanguage();
		    	  String sName = jsonObject.getString(sLocaleKeyName);	
		    	  if(sName.length() == 0)
		    		  sName = jsonObject.getString("name");
		    	  items.add(sName);
		    }
		    
		    int count = items.size();
		    final boolean[] checkedItems = new boolean[count];
		    
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.sSelectDataToDownload)
				   .setCancelable(false)
				   .setMultiChoiceItems(items.toArray(new String[items.size()]), checkedItems,
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
									for (int i = 0; i < checkedItems.length; i++) {
										if (checkedItems[i]){
											try {
												JSONObject jsonObject = jsonArray.getJSONObject(i);
												//download and unzip
												int nVer = jsonObject.getInt("ver");
												String sPath = jsonObject.getString("path");
												String sLocName = jsonObject.getString("name_" + Locale.getDefault().getLanguage());
												String sName = jsonObject.getString("name");
												if(sLocName.length() == 0){
													sLocName = sName;
												}
												DataDownloader uploader = new DataDownloader(MainActivity.this, sPath, sName, sLocName, nVer, getResources().getString(R.string.sDownLoading), moGetJSONHandler);
												uploader.execute(sUrl + sPath + ".zip");
												
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
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
	    	
	    } 
	    catch (Exception e) {
	    	Toast.makeText(MainActivity.this, R.string.sNetworkInvalidData, Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean writeToFile(File filePath, String sData){
		try{
			FileOutputStream os = new FileOutputStream(filePath, false);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
	        outputStreamWriter.write(sData);
	        outputStreamWriter.close();
	        return true;
		}
		catch(IOException e){
			return false;
		}		
	}

	private String readFromFile(File filePath) {

	    String ret = "";

	    try {
	    	FileInputStream inputStream = new FileInputStream(filePath);

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	    	Toast.makeText(MainActivity.this, getString(R.string.sFileNotFound) + ": " + e.toString(), Toast.LENGTH_LONG).show();
	    } catch (IOException e) {
	    	Toast.makeText(MainActivity.this, getString(R.string.sCannotReadFile) + ": " + e.toString(), Toast.LENGTH_LONG).show();
	    }

	    return ret;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockActivity#onPause()
	 */
	@Override
	protected void onPause() {
	    ActionBar actionBar = getSupportActionBar();
		if(ActionBar.NAVIGATION_MODE_LIST == actionBar.getNavigationMode()){
			final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		    
			edit.putInt(CURRENT_METRO_SEL, actionBar.getSelectedNavigationIndex());
			
			edit.commit();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    ActionBar actionBar = getSupportActionBar();
		if(ActionBar.NAVIGATION_MODE_LIST == actionBar.getNavigationMode()){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		    int nCurrentMetro = prefs.getInt(CURRENT_METRO_SEL, 0);
		    actionBar.setSelectedNavigationItem(nCurrentMetro);
		}
	}
	
}
