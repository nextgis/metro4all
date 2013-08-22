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

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


public class MainActivity extends SherlockActivity {
	final static String TAG = "metroaccess";	
	final static String sUrl = "http://gis-lab.info/data/zp-gis/data/ma/";
	final static String META = "meta.json";
	final static String BUNDLE_MSG_KEY = "msg";
	final static String BUNDLE_PAYLOAD_KEY = "json";
	final static String BUNDLE_ERRORMARK_KEY = "error";
	
//TODO:	public final static int MENU_SETTINGS = 4;
	public final static int MENU_ABOUT = 5;

	//public final static int GET_META = 0;
	
	private static Handler moGetJSONHandler; 
	
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
            	if(bHaveErr){
            		Toast.makeText(MainActivity.this, resultData.getString(BUNDLE_MSG_KEY), Toast.LENGTH_LONG).show();
            	}
            	else{
            		if(IsRoutingDataExist()){
            			//check if updates available
//            			searchButton.setEnabled(true);
//            			demoButton.setEnabled(true);
            		}
            		else{
            			AskForDownloadData(resultData.getString(BUNDLE_PAYLOAD_KEY));
            		}
            	}
            }
        };		
		
		//check for data exist
		if(IsRoutingDataExist()){
			//else check for updates
			CheckForUpdates();
			setContentView(R.layout.activity_main);		
		}
		else{
			//ask to download data
			GetRoutingData();
		}
		
		

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
/*		PolicemanDatabaseHelper dbHelper = new PolicemanDatabaseHelper(this);
		SQLiteDatabase PolicemanDB = dbHelper.getWritableDatabase(); 
		boolean bExist = false;
		if(PolicemanDB != null){
			Cursor cursor = PolicemanDB.query(PolicemanDatabaseHelper.TABLE, null, null, null, null, null, null);
			if(cursor != null){
				if(cursor.getCount() > 0)
					bExist = true;
			}
		}
		return bExist;*/
		return false;
	}	
	
	protected void CheckForUpdates(){
		MetaUploader uploader = new MetaUploader(MainActivity.this, getResources().getString(R.string.sDownLoading), moGetJSONHandler, false);
		uploader.execute(sUrl + META);				
	}
	
	protected void GetRoutingData(){
		MetaUploader uploader = new MetaUploader(MainActivity.this, getResources().getString(R.string.sDownLoading), moGetJSONHandler, true);
		uploader.execute(sUrl + META);		
	}

	protected void AskForDownloadData(String sJSON){
	    try{
	    	JSONObject jsonMainObject = new JSONObject(sJSON);
		    JSONArray jsonArray = jsonMainObject.getJSONArray("packages");
		    
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
/*									StringBuilder state = new StringBuilder();
									for (int i = 0; i < checkCatsName.length; i++) {
										state.append("" + checkCatsName[i]);
										if (mCheckedItems[i])
											state.append(" выбран\n");
										else
											state.append(" не выбран\n");
									}
									Toast.makeText(getApplicationContext(),
											state.toString(), Toast.LENGTH_LONG)
											.show();
*/								}
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
}
