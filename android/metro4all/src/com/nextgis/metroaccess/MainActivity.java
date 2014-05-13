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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nextgis.metroaccess.data.DownloadData;
import com.nextgis.metroaccess.data.GraphDataItem;
import com.nextgis.metroaccess.data.MAGraph;
import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

//https://code.google.com/p/k-shortest-paths/

public class MainActivity extends SherlockActivity{
	public final static String TAG = "metro4all";
	
	public final static String META = "meta.json";
	final static String REMOTE_METAFILE = "remotemeta_v2.json";
	final static String ROUTE_DATA_DIR = "rdata_v2";
	
	public final static String CSV_CHAR = ";";
	
	final static String BUNDLE_MSG_KEY = "msg";
	final static String BUNDLE_PAYLOAD_KEY = "json";
	final static String BUNDLE_ERRORMARK_KEY = "error";
	final static String BUNDLE_EVENTSRC_KEY = "eventsrc";
	final static String BUNDLE_ENTRANCE_KEY = "in";
	final static String BUNDLE_PATHCOUNT_KEY = "pathcount";
	final static String BUNDLE_PATH_KEY = "path_";
	final static String BUNDLE_STATIONMAP_KEY = "stationmap";
	final static String BUNDLE_CROSSESMAP_KEY = "crossmap";
	final static String BUNDLE_STATIONID_KEY = "stationid";
	final static String BUNDLE_PORTALID_KEY = "portalid";
	final static String BUNDLE_METAMAP_KEY = "metamap";
	
	public final static int MENU_SEARCH = 3;
	public final static int MENU_SETTINGS = 4;
	public final static int MENU_ABOUT = 5;

	public final static int DEPARTURE_RESULT = 1;
	public final static int ARRIVAL_RESULT = 2;
	public final static int PREF_RESULT = 3;
	public final static int MAX_RECENT_ITEMS = 10;
	
	protected boolean m_bInterfaceLoaded;
	
	protected static Handler m_oGetJSONHandler; 
	
	protected Button m_oSearchButton;
	protected MenuItem m_oSearchMenuItem;
	
	protected Button mSelectFromStationButton;
	protected Button mSelectToStationButton;
	protected Button mSetButton;
	
	protected List<Pair<Integer, Integer>> m_aoDepRecentIds, m_aoArrRecentIds;
	protected int m_nDepartureStationId, m_nArrivalStationId;
	protected int m_nDeparturePortalId, m_nArrivalPortalId;
	
	protected TextView mtvDepartureStationName, mtvArrivalStationName; 
	protected TextView mtvDeparturePortalName, mtvArrivalPortalName;	
	
	protected List<DownloadData> m_asDownloadData;

	public static String m_sUrl = "http://metro4all.org/data/v2/";
	public static MAGraph m_oGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.empty_activity_main);
		m_nDepartureStationId = m_nArrivalStationId = -1;
		m_nDeparturePortalId = m_nArrivalPortalId = -1;
		
		m_aoDepRecentIds = new ArrayList<Pair<Integer, Integer>>();
		m_aoArrRecentIds = new ArrayList<Pair<Integer, Integer>>();
		
		m_bInterfaceLoaded = false;
 		
        // initialize the default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		m_sUrl = prefs.getString(PreferencesActivity.KEY_PREF_DOWNLOAD_PATH, m_sUrl);
		
		String sCurrentCity = prefs.getString(PreferencesActivity.KEY_PREF_CITY, "");
		String sCurrentCityLang = prefs.getString(PreferencesActivity.KEY_PREF_CITYLANG, Locale.getDefault().getLanguage());
		m_oGraph = new MAGraph(this.getBaseContext(), sCurrentCity, getExternalFilesDir(null), sCurrentCityLang);
		
		//create downloading queue empty initially
		m_asDownloadData = new ArrayList<DownloadData>();
		
		CreateHandler();
        	
		//check for data exist
		if(IsRoutingDataExist()){
			//else check for updates
			CheckForUpdates();		
		}
		else{
			//ask to download data
			GetRoutingData();
		}      

	}
	
	protected void CreateHandler(){
		
		m_oGetJSONHandler = new Handler() {
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
	            	case 2:
	            		if(IsRoutingDataExist())
	                    	LoadInterface();
	            		break;
            		default:
            			return;
	            	}
            	}

            	switch(nEventSource){
            	case 1://get remote meta
            		if(IsRoutingDataExist()){
            			//check if updates available
            			CheckUpdatesAvailable(sPayload);
            		}
            		else{
            			AskForDownloadData(sPayload);
            		}
            		break;
            	case 2:            		
            		if(m_asDownloadData.isEmpty()){   
            			m_oGraph.FillRouteMetadata();
            			LoadInterface();
            		}
            		else{
            			OnDownloadData();
            		}
            		break;
            	}
            }
        };		
	}
	
	protected void LoadInterface(){
		
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sCurrentCity = prefs.getString(PreferencesActivity.KEY_PREF_CITY, m_oGraph.GetCurrentCity());
        
        if(sCurrentCity.length() < 2){
        	//find first city and load it
        	m_oGraph.SetFirstCityAsCurrent();
        }
        else{
        	m_oGraph.SetCurrentCity( sCurrentCity );        	
        }
        
        if(!m_oGraph.IsValid())
        	return;
		
		m_bInterfaceLoaded = true;
		setContentView(R.layout.activity_main);
	    
		m_oSearchButton = (Button) findViewById(R.id.btSearch);
		m_oSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSearch();
             }
        });  
		m_oSearchButton.setEnabled(false);
		
		//from station
		mSelectFromStationButton = (Button) findViewById(R.id.btSetDepart);
		mSelectFromStationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSelectDepatrure();
             }
        });  		
		//to station
		mSelectToStationButton = (Button) findViewById(R.id.btSelArrival);
		mSelectToStationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSelectArrival();
             }
        });
		
		//settings
		mSetButton = (Button) findViewById(R.id.btSettings);
		mSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSettings();
             }
        });		
		
		mtvDepartureStationName = (TextView) findViewById(R.id.fromstationname);
		mtvArrivalStationName = (TextView) findViewById(R.id.tostationname);
		mtvDeparturePortalName = (TextView) findViewById(R.id.fromentrancename);
		mtvArrivalPortalName =  (TextView) findViewById(R.id.toentrancename);
		
		TextView tv1 = (TextView) findViewById(R.id.fromentrancenamelabel);
		tv1.setText(tv1.getText() + ": ");
		TextView tv2 = (TextView) findViewById(R.id.toentrancenamelabel);
		tv2.setText(tv2.getText() + ": ");
		
		mtvDepartureStationName.setText(getString(R.string.sNotSet));
		mtvDeparturePortalName.setText(getString(R.string.sNotSet));   
		mtvArrivalStationName.setText(getString(R.string.sNotSet));
		mtvArrivalPortalName.setText(getString(R.string.sNotSet));   
		
    	if(m_oSearchButton != null) 
    		m_oSearchButton.setEnabled(false);
    	if(m_oSearchMenuItem != null)
    		m_oSearchMenuItem.setEnabled(false);
    	
    	if(!m_oGraph.IsValid()){
    		Toast.makeText(this, m_oGraph.GetLastError(), Toast.LENGTH_SHORT).show();
    	}
    	else{
    		UpdateUI();
    	}

	}

	protected void onSettings() {
        Intent intentSet = new Intent(this, PreferencesActivity.class);
        intentSet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        
        Bundle bundle = new Bundle();
        //bundle.putParcelable(BUNDLE_METAMAP_KEY, m_oGraph);
        intentSet.putExtras(bundle);            
        startActivityForResult(intentSet, PREF_RESULT);		
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		m_oSearchMenuItem = menu.add(com.actionbarsherlock.view.Menu.NONE, MENU_SEARCH, com.actionbarsherlock.view.Menu.NONE, R.string.sSearch)
		.setIcon(R.drawable.ic_action_search);
		m_oSearchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		m_oSearchMenuItem.setEnabled(false);
		m_oSearchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);	

		menu.add(com.actionbarsherlock.view.Menu.NONE, MENU_SETTINGS, com.actionbarsherlock.view.Menu.NONE, R.string.sSettings)
       .setIcon(R.drawable.ic_action_settings)
       .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);		
		
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
        case MENU_SEARCH:
        	onSearch();
        	return true;
        case MENU_SETTINGS:
            // app icon in action bar clicked; go home
        	onSettings();
            return true;
        case MENU_ABOUT:
            Intent intentAbout = new Intent(this, AboutActivity.class);
            intentAbout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentAbout);
            return true;	  
        }
		return super.onOptionsItemSelected(item);
	}	
	
	protected void CheckForUpdates(){
		MetaDownloader uploader = new MetaDownloader(MainActivity.this, getResources().getString(R.string.sDownLoading), m_oGetJSONHandler, true);
		uploader.execute(GetDownloadURL() + META);				
	}
	
	protected void GetRoutingData(){
		MetaDownloader loader = new MetaDownloader(MainActivity.this, getResources().getString(R.string.sDownLoading), m_oGetJSONHandler, true);
		loader.execute(GetDownloadURL() + META);		
	}
	
	//check if data for routing is downloaded
	protected boolean IsRoutingDataExist(){	
		return m_oGraph.IsRoutingDataExist();
	}
	
	protected void CheckUpdatesAvailable(String sJSON){
		
		m_oGraph.OnUpdateMeta(sJSON, true);
		final List<GraphDataItem> items = m_oGraph.HasChanges();

		int count = items.size();
		if(count < 1){
			LoadInterface();
			return;
		}
		final boolean[] checkedItems = new boolean[count];
	    for(int i = 0; i < count; i++){
	    	checkedItems[i] = true;
	    }
	    
	    final CharSequence[] checkedItemStrings = new CharSequence[count];
	    for(int i = 0; i < count; i++){
	    	checkedItemStrings[i] = items.get(i).GetFullName();
	    }
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.sUpdateAvaliable)
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
					if (checkedItems[i]){
						m_asDownloadData.add(new DownloadData(MainActivity.this, items.get(i), GetDownloadURL() + items.get(i).GetPath() + ".zip", m_oGetJSONHandler));
					}
				}
				
				OnDownloadData();
				
			}
		})

		.setNegativeButton(R.string.sCancel,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				LoadInterface();
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();		    
    }

	protected void AskForDownloadData(String sJSON){
		//ask user for download
		m_oGraph.OnUpdateMeta(sJSON, false);
		final List<GraphDataItem> items = m_oGraph.HasChanges();
		    
	    int count = items.size();
	    if(count == 0)
	    	return;
	    
	    final boolean[] checkedItems = new boolean[count];
	    for(int i = 0; i < count; i++){
	    	checkedItems[i] = true;
	    }
	    
	    final CharSequence[] checkedItemStrings = new CharSequence[count];
	    for(int i = 0; i < count; i++){
	    	checkedItemStrings[i] = items.get(i).GetFullName();
	    }
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
									if (checkedItems[i]){
										m_asDownloadData.add(new DownloadData(MainActivity.this, items.get(i), GetDownloadURL() + items.get(i).GetPath() + ".zip", m_oGetJSONHandler));										
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
	}
	
	protected void OnDownloadData(){
		if(m_asDownloadData.isEmpty())
			return;
		DownloadData data = m_asDownloadData.get(0);
		m_asDownloadData.remove(0);
		
		data.OnDownload();
	}
	
	public static boolean writeToFile(File filePath, String sData){
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

	public static String readFromFile(File filePath) {

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
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }

	    return ret;
	}

	/*
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		mtvDepartureStationName.setText(getString(R.string.sNotSet));
		mtvDeparturePortalName.setText(getString(R.string.sNotSet));   
		mtvArrivalStationName.setText(getString(R.string.sNotSet));
		mtvArrivalPortalName.setText(getString(R.string.sNotSet));   
		
    	if(m_oSearchButton != null) 
    		m_oSearchButton.setEnabled(false);
    	if(m_oSearchMenuItem != null)
    		m_oSearchMenuItem.setEnabled(false);
    	
 	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      	//TODO: something to do
	    //Editor edit = prefs.edit();
	    //edit.putInt(CURRENT_METRO_SEL, itemPosition);
	    //edit.commit();

	    try {
	    	JSONObject mmeta = mmoRouteMetadata.get(itemPosition);
			msRDataPath = mmeta.getString("path");

			if(mmeta.has("directed")){
				mbDirected = mmeta.getBoolean("directed");
			}
	
			mGraph = new VariableGraph();
			mmoStations = new HashMap<Integer, StationItem>();

		    //fill with station list
			//TODO: add locale support in stations name
			//1. Get preferences value
		    String sLoc = prefs.getString(PreferencesActivity.KEY_PREF_DATA_LOCALE, Locale.getDefault().getLanguage());
			//2. Try to find appropriate file
		    String sFileName = "stations_" + sLoc + ".csv";		    
			//3. If none get stations_en.csv
		    File station_file = new File(msRDataPath, sFileName);
		    if(!station_file.exists())
	    		station_file = new File(msRDataPath, "stations_en.csv");
		    
			if (station_file != null) {
	        	InputStream in;
				in = new BufferedInputStream(new FileInputStream(station_file));
	       	
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	
		        String line = reader.readLine();
		        while ((line = reader.readLine()) != null) {
		             String[] RowData = line.split(CSV_CHAR);
		             
		             if(RowData.length < 4){
		     	    	 Toast.makeText(MainActivity.this, getString(R.string.sInvalidCSVData) + "stations.csv", Toast.LENGTH_LONG).show();
		            	 return false;
		             }
		             
					 String sName = RowData[3];
					 int nNode = Integer.parseInt(RowData[2]);
					 int nLine = Integer.parseInt(RowData[1]);
					 int nID = Integer.parseInt(RowData[0]);
	 					 
					 mGraph.add_vertex(new Vertex(nID));
					 StationItem st = new StationItem(nID, sName, nLine, nNode, 0);
	 				     
				     mmoStations.put(nID, st);
		        }
			        
		        reader.close();
		        if (in != null) {
		        	in.close();
		    	} 
			}
	
			File portals_file = new File(msRDataPath, "portals.csv");
			if (portals_file != null) {
			   	InputStream in;
				in = new BufferedInputStream(new FileInputStream(portals_file));
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	
		        String line = reader.readLine();
		        while ((line = reader.readLine()) != null) {
		             String[] RowData = line.split(CSV_CHAR);
		             
		             if(RowData.length < 4){
		     	    	 Toast.makeText(MainActivity.this, getString(R.string.sInvalidCSVData) + "portals.csv", Toast.LENGTH_LONG).show();
		            	 return false;
		             }
		             
					 int nID = Integer.parseInt(RowData[0]);
					 String sName = RowData[1];
					 int nStationId = Integer.parseInt(RowData[2]);
					 int nDirection = 0;
					 if(RowData[3].equals("in")){
						 nDirection = 1;
					 }
					 else if(RowData[3].equals("out")){
						 nDirection = 2;
					 }
					 else{
					 	nDirection = 3;
					 }						
					 
					 int min_width = 0;
					 int min_step = 0;
					 int min_step_ramp = 0;
					 int lift = 0;
					 int lift_minus_step = 0;
					 int min_rail_width = 0;
					 int max_rail_width = 0;
					 int max_angle = 0;
					 
					 if(RowData.length > 13)
					 {
						 String tmp = RowData[6];
						 min_width = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[7];
						 min_step = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[8];
						 min_step_ramp = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[9];
						 lift = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[10];
						 lift_minus_step = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[11];
						 min_rail_width = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[12];
						 max_rail_width = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
						 tmp = RowData[13];
						 max_angle = tmp.length() == 0 ? 0 : Integer.parseInt(tmp);
					 }
					 int [] detailes = {min_width, min_step, min_step_ramp, lift, lift_minus_step, min_rail_width, max_rail_width, max_angle};
					 PortalItem pt = new PortalItem(nID, sName, nStationId, nDirection, detailes);
	
					 mmoStations.get(nStationId).AddPortal(pt);
					 
					 Log.d(TAG, "#" + nID);
		        }
		        
		        reader.close();
			    if (in != null) {
			       	in.close();
			   	} 
			}	
		
		
			//fill routes
			File file_route = new File(msRDataPath, "graph.csv");
			if (file_route != null) {
	        	InputStream in;
				in = new BufferedInputStream(new FileInputStream(file_route));
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		        String line = reader.readLine();
		        while ((line = reader.readLine()) != null) {
		             String[] RowData = line.split(CSV_CHAR);
		             
		             if(RowData.length != 5){
		     	    	 Toast.makeText(MainActivity.this, getString(R.string.sInvalidCSVData) + "graph.csv", Toast.LENGTH_LONG).show();
		            	 return false;
		             }
		             
					 int nFromId = Integer.parseInt(RowData[0]);
					 int nToId = Integer.parseInt(RowData[1]);
					 int nCost = Integer.parseInt(RowData[4]);
	 					 
					 Log.d("Route", ">" + nFromId + "-" + nToId + ":" + nCost);
					 mGraph.add_edge(nFromId, nToId, nCost);
					 if(!mbDirected){
						 mGraph.add_edge(nToId, nFromId, nCost);
					 }
		        }
		        reader.close();
		        if (in != null) {
		        	in.close();
		    	}
			}
			
			m_yenAlg = new YenTopKShortestPathsAlg(mGraph);
			
			//fill interchanges.csv
			mmoCrosses = new HashMap<String, int[]>(); 
			File inter_route = new File(msRDataPath, "interchanges.csv");
			if (inter_route != null) {
	        	InputStream in = new BufferedInputStream(new FileInputStream(inter_route));
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		        String line = reader.readLine();
		        while ((line = reader.readLine()) != null) {
		             String[] RowData = line.split(CSV_CHAR);
		             
		             //station_from;station_to;max_width;min_step;min_step_ramp;lift;lift_minus_step;min_rail_width;max_rail_width;max_angle
		             
		             if(RowData.length != 10){
		     	    	 Toast.makeText(MainActivity.this, getString(R.string.sInvalidCSVData) + "interchanges.csv", Toast.LENGTH_LONG).show();
		            	 return false;
		             }
		             
					 int nFromId = Integer.parseInt(RowData[0]);
					 int nToId = Integer.parseInt(RowData[1]);
					 int[] naBarriers = {0,0,0,0,0,0,0,0};
					 for(int i = 2; i < 10; i++){
						 int nVal = Integer.parseInt(RowData[i]);
						 naBarriers[i - 2] = nVal;
					 }	 
					 mmoCrosses.put("" + nFromId + "->" + nToId, naBarriers);					 
		        }
		        reader.close();
		        if (in != null) {
		        	in.close();
		    	}
			}
	    }
	    catch (IOException ex) {
	    	ex.printStackTrace();
	    	return false;
		}
		catch(IllegalArgumentException ex){
			ex.printStackTrace();
			return false;
		}
		catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		UpdateUI();
		
		return true;
	}
*/
	@Override
	protected void onPause() {
		final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
	    
		//store departure and arrival
		
		edit.putInt("dep_" + BUNDLE_STATIONID_KEY, m_nDepartureStationId);
		edit.putInt("arr_" + BUNDLE_STATIONID_KEY, m_nArrivalStationId);
		edit.putInt("dep_" + BUNDLE_PORTALID_KEY, m_nDeparturePortalId);
		edit.putInt("arr_" + BUNDLE_PORTALID_KEY, m_nArrivalPortalId);
		
		int nbeg = m_aoDepRecentIds.size() < MAX_RECENT_ITEMS ? 0 : m_aoDepRecentIds.size() - MAX_RECENT_ITEMS;
		int nsize = m_aoDepRecentIds.size() - nbeg;
		int counter = 0;
		for(int i = nbeg; i < nsize; i++){
			edit.putInt("recent_dep_" + BUNDLE_STATIONID_KEY+counter, m_aoDepRecentIds.get(i).first);
			edit.putInt("recent_dep_" + BUNDLE_PORTALID_KEY+counter, m_aoDepRecentIds.get(i).second);
			
			counter++;
		}
		edit.putInt("recent_dep_counter",counter);
		
		nbeg = m_aoArrRecentIds.size() < MAX_RECENT_ITEMS ? 0 : m_aoArrRecentIds.size() - MAX_RECENT_ITEMS;
		nsize = m_aoArrRecentIds.size() - nbeg;
		counter = 0;
		for(int i = nbeg; i < nsize; i++){
			edit.putInt("recent_arr_" + BUNDLE_STATIONID_KEY+counter, m_aoArrRecentIds.get(i).first);
			edit.putInt("recent_arr_" + BUNDLE_PORTALID_KEY+counter, m_aoArrRecentIds.get(i).second);
			
			counter++;
		}
		edit.putInt("recent_arr_counter",counter);
		
		edit.commit();

		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    m_nDepartureStationId = prefs.getInt("dep_"+BUNDLE_STATIONID_KEY, -1);
	    m_nArrivalStationId = prefs.getInt("arr_"+BUNDLE_STATIONID_KEY, -1);
	    m_nDeparturePortalId = prefs.getInt("dep_"+BUNDLE_PORTALID_KEY, -1);
	    m_nArrivalPortalId = prefs.getInt("arr_"+BUNDLE_PORTALID_KEY, -1);

		int size = prefs.getInt("recent_dep_counter", 0);
		for(int i = 0; i < size; i++){
			int nB = prefs.getInt("recent_dep_"+BUNDLE_STATIONID_KEY+i, -1);
			int nE = prefs.getInt("recent_dep_"+BUNDLE_PORTALID_KEY+i, -1);
			
			Pair<Integer, Integer> pair = Pair.create(nB, nE);
			if(!m_aoDepRecentIds.contains(pair)){
				m_aoDepRecentIds.add(Pair.create(nB, nE));
			}
		}

		size = prefs.getInt("recent_arr_counter", 0);
		for(int i = 0; i < size; i++){
			int nB = prefs.getInt("recent_arr_"+BUNDLE_STATIONID_KEY+i, -1);
			int nE = prefs.getInt("recent_arr_"+BUNDLE_PORTALID_KEY+i, -1);
			Pair<Integer, Integer> pair = Pair.create(nB, nE);
			if(!m_aoArrRecentIds.contains(pair)){
				m_aoArrRecentIds.add(Pair.create(nB, nE));
			}
		}	    
		
		
		//check if routing data changed
		m_oGraph.FillRouteMetadata();		
		
		if(m_bInterfaceLoaded){
			if(m_oGraph.IsEmpty()){
				if(IsRoutingDataExist()){
					LoadInterface();
				}
			}
			UpdateUI();
		}
	}
	
	protected void 	onSelectDepatrure(){
	    Intent intent = new Intent(this, SelectStationActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putInt(BUNDLE_EVENTSRC_KEY, DEPARTURE_RESULT);
        //bundle.putSerializable(BUNDLE_STATIONMAP_KEY, (Serializable) mmoStations);
        bundle.putBoolean(BUNDLE_ENTRANCE_KEY, true);
	    intent.putExtras(bundle);
	    startActivityForResult(intent, DEPARTURE_RESULT);	
	}
	
	protected void 	onSelectArrival(){
	    Intent intent = new Intent(this, SelectStationActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putInt(BUNDLE_EVENTSRC_KEY, ARRIVAL_RESULT);
        //bundle.putSerializable(BUNDLE_STATIONMAP_KEY, (Serializable) mmoStations);
        bundle.putBoolean(BUNDLE_ENTRANCE_KEY, false);
	    intent.putExtras(bundle);
	    startActivityForResult(intent, ARRIVAL_RESULT);			
	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {
	    	return;
	    }
	    
	    if (resultCode != RESULT_OK) {
	    	return;
	    }
	    
    	int nStationId = data.getIntExtra(BUNDLE_STATIONID_KEY, -1);
    	int nPortalId = data.getIntExtra(BUNDLE_PORTALID_KEY, -1);
    	
    	
		final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
	    switch(requestCode){
	    case DEPARTURE_RESULT:
	    	m_aoDepRecentIds.add(Pair.create(nStationId, nPortalId));
	       	m_nDepartureStationId = nStationId;	    	
	    	m_nDeparturePortalId = nPortalId;
			edit.putInt("dep_"+BUNDLE_STATIONID_KEY, m_nDepartureStationId);
			edit.putInt("dep_"+BUNDLE_PORTALID_KEY, m_nDeparturePortalId);
	       	break;
	    case ARRIVAL_RESULT:
	    	m_aoArrRecentIds.add(Pair.create(nStationId, nPortalId));
	    	m_nArrivalStationId = nStationId;
	    	m_nArrivalPortalId = nPortalId;
			edit.putInt("arr_"+BUNDLE_STATIONID_KEY, m_nArrivalStationId);
			edit.putInt("arr_"+BUNDLE_PORTALID_KEY, m_nArrivalPortalId);
	    	break;
	    case PREF_RESULT:
	    	break;
    	default:
    		break;
	    }

	    edit.commit();
	    
	    UpdateUI();
	}	
	
	protected void UpdateUI(){
		if(m_oGraph.HasStations()){
	    	StationItem dep_sit = m_oGraph.GetStation(m_nDepartureStationId);
	    	if(dep_sit != null && mtvDepartureStationName != null){    		
	    		mtvDepartureStationName.setText(dep_sit.GetName());
	    		PortalItem pit = dep_sit.GetPortal(m_nDeparturePortalId);
	    		if(pit != null && mtvDeparturePortalName != null){
	    			mtvDeparturePortalName.setText(pit.GetName());
	    		}
	    		else{
	    			mtvDeparturePortalName.setText(getString(R.string.sNotSet));  
	    			m_nDeparturePortalId = -1;
	    		}
	    	}
	    	else{
	    		mtvDepartureStationName.setText(getString(R.string.sNotSet));
	    		mtvDeparturePortalName.setText(getString(R.string.sNotSet)); 
	    		m_nDepartureStationId = -1;
	    	}
	
	    	StationItem arr_sit = m_oGraph.GetStation(m_nArrivalStationId);
	    	if(arr_sit != null && mtvArrivalStationName != null){
	    		mtvArrivalStationName.setText(arr_sit.GetName());
	    		PortalItem pit = arr_sit.GetPortal(m_nArrivalPortalId);
	    		if(pit != null && mtvArrivalPortalName != null){
	    			mtvArrivalPortalName.setText(pit.GetName());
	    		}
	    		else{
	    			mtvArrivalPortalName.setText(getString(R.string.sNotSet));
	    			m_nArrivalPortalId = -1;
	    		}
	    	}
	    	else{
	    		mtvArrivalStationName.setText(getString(R.string.sNotSet));
	    		mtvArrivalPortalName.setText(getString(R.string.sNotSet)); 
	    		m_nArrivalStationId = -1;
	    	}
		}

	    if(m_nDepartureStationId != m_nArrivalStationId && m_nDepartureStationId != -1 && m_nArrivalStationId != -1 && m_nDeparturePortalId != -1 && m_nArrivalPortalId != -1){
	    	if(m_oSearchButton != null) 
	    		m_oSearchButton.setEnabled(true);
    		if(m_oSearchMenuItem != null)
	    		m_oSearchMenuItem.setEnabled(true);
	    }
	    else{
	    	if(m_oSearchButton != null) 
	    		m_oSearchButton.setEnabled(false);
	    	if(m_oSearchMenuItem != null)
	    		m_oSearchMenuItem.setEnabled(false);
	    }
	}
	
	protected void onSearch(){
		
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getString(R.string.sSearching));
		progressDialog.show();
		
		new Thread() {

			public void run() {

				//BellmanFordShortestPath
				/*List<DefaultWeightedEdge> path = BellmanFordShortestPath.findPathBetween(mGraph, stFrom.getId(), stTo.getId());
				if(path != null){
					for(DefaultWeightedEdge edge : path) {
		                	Log.d("Route", mmoStations.get(mGraph.getEdgeSource(edge)) + " - " + mmoStations.get(mGraph.getEdgeTarget(edge)) + " " + edge);
		                }
				}*/
				//DijkstraShortestPath
				/*List<DefaultWeightedEdge> path = DijkstraShortestPath.findPathBetween(mGraph, stFrom.getId(), stTo.getId());
				if(path != null){
					for(DefaultWeightedEdge edge : path) {
		                	Log.d("Route", mmoStations.get(mGraph.getEdgeSource(edge)) + " - " + mmoStations.get(mGraph.getEdgeTarget(edge)) + " " + edge);
		                }
				}*/	
		        //KShortestPaths
				/*
				KShortestPaths<Integer, DefaultWeightedEdge> kPaths = new KShortestPaths<Integer, DefaultWeightedEdge>(mGraph, stFrom.getId(), 2);
		        List<GraphPath<Integer, DefaultWeightedEdge>> paths = null;
		        try {
		            paths = kPaths.getPaths(stTo.getId());
		            for (GraphPath<Integer, DefaultWeightedEdge> path : paths) {
		                for (DefaultWeightedEdge edge : path.getEdgeList()) {
		                	Log.d("Route", mmoStations.get(mGraph.getEdgeSource(edge)) + " - " + mmoStations.get(mGraph.getEdgeTarget(edge)) + " " + edge);
		                }
		                Log.d("Route", "Weight: " + path.getWeight());
		            }
		        } catch (IllegalArgumentException e) {
		        	e.printStackTrace();
		        }*/
				
				//YenTopKShortestPaths
				
			    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			    int nMaxRouteCount = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_ROUTE_COUNT, 3);

				List<Path> shortest_paths_list = m_oGraph.GetShortestPaths(m_nDepartureStationId, m_nArrivalStationId, nMaxRouteCount);
				
				if(shortest_paths_list.size() == 0){
					Toast.makeText(MainActivity.this.getBaseContext(), R.string.sCannotGetPath, Toast.LENGTH_SHORT).show();
					//Toast.makeText(MainActivity.this, R.string.sCannotGetPath, Toast.LENGTH_SHORT).show();
					//Log.d(MainActivity.TAG, MainActivity.this.getString(R.string.sCannotGetPath));
				}
				else {
			        Intent intentView = new Intent(MainActivity.this, com.nextgis.metroaccess.StationListView.class);
			        //intentView.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				
			        int nCounter = 0;
			        Bundle bundle = new Bundle();
			        bundle.putInt("dep_" + BUNDLE_PORTALID_KEY, m_nDeparturePortalId);
			        bundle.putInt("arr_" + BUNDLE_PORTALID_KEY, m_nArrivalPortalId);			

			        for (Path path : shortest_paths_list) {
						ArrayList<Integer> IndexPath = new  ArrayList<Integer>();
						Log.d(TAG, "Route# " + nCounter);
			            for (BaseVertex v : path.get_vertices()) {
			            	IndexPath.add(v.get_id());
			            	Log.d(TAG, "<" + m_oGraph.GetStation(v.get_id()));
			            }
			            intentView.putIntegerArrayListExtra(BUNDLE_PATH_KEY + nCounter, IndexPath);
			            nCounter++;
			        }	        
			        
			        bundle.putInt(BUNDLE_PATHCOUNT_KEY, nCounter);
			        //bundle.putSerializable(BUNDLE_STATIONMAP_KEY, (Serializable) mmoStations);
			        //bundle.putSerializable(BUNDLE_CROSSESMAP_KEY, (Serializable) mmoCrosses);
					
					intentView.putExtras(bundle);
			        
			        MainActivity.this.startActivity(intentView);
			       
				}

				progressDialog.dismiss();

			}

		}.start();

	}
	
	public static String GetDownloadURL(){
		return m_sUrl;
	}
	
	public static String GetRouteDataDir(){
		return ROUTE_DATA_DIR;
	}
	
	public static String GetMetaFileName(){
		return META;
	}
	
	public static String GetRemoteMetaFile(){
		return REMOTE_METAFILE;
	}
	
	public static MAGraph GetGraph(){
		return m_oGraph;
	}
	
	public static void SetDownloadURL(String sURL){
		m_sUrl = sURL;
	}

}
