/*******************************************************************************
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
*******************************************************************************/

package com.nextgis.metroaccess;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class StationListView extends SherlockActivity {
	
    private ListView mPathListView;
    private ArrayList <StationItem> mStationList = new ArrayList<StationItem>();
    protected StationListAdapter mListAdapter;
    private int mnPathCount, mnDeparturePortalId, mnArrivalPortalId;
    
	protected HashMap<Integer, StationItem> mmoStations;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mbFilled = false;
        setContentView(R.layout.station_list_view);
        
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	    Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	    	mnDeparturePortalId = extras.getInt("dep_" + MainActivity.BUNDLE_PORTALID_KEY);
	    	mnArrivalPortalId = extras.getInt("arr_" + MainActivity.BUNDLE_PORTALID_KEY);
	        
	    	mnPathCount = extras.getInt(MainActivity.BUNDLE_PATHCOUNT_KEY);
	    	mmoStations = (HashMap<Integer, StationItem>) extras.getSerializable(MainActivity.BUNDLE_STATIONMAP_KEY);
	    	//TODO:
	    	/*
	    	if(mnPathCount == 3){
	    		
	    	}
	    	else if(mnPathCount == 2){
	    		
	    	}
	    	else if(mnPathCount == 1){
	    		
	    	}
	    	*/
	    	if(mnPathCount > 0){
	    		ArrayList<Integer> list = extras.getIntegerArrayList(MainActivity.BUNDLE_PATH_KEY + 0);
	    		boolean bCross = false;
	    		if(list != null){
	    			for(int i = 0; i < list.size(); i++){
	    				int nId = list.get(i);
	    				int nType = 5;
	    				if(i == list.size() - 1){//check dst
	    					nType = 2;
	    					//check cross
							int nNextId = list.get(i - 1);
							int nLineFrom = mmoStations.get(nId).GetLine();
							int nLineTo = mmoStations.get(nNextId).GetLine();
							if(nLineFrom != nLineTo){
								nType = 7;
							}
	    				}
	    				else{
	    					if(i == 0){//check src
	    						nType = 1;
	    						//check cross
    							int nNextId = list.get(i + 1);
    							int nLineFrom = mmoStations.get(nId).GetLine();
    							int nLineTo = mmoStations.get(nNextId).GetLine();
    							if(nLineFrom != nLineTo){
   									bCross = true;
    								nType = 6;
    							}
	    					}
	    					else{
	    						if(bCross){
	    							bCross = false;
	    							nType = 3;
	    						}
    							int nNextId = list.get(i + 1);
    							int nLineFrom = mmoStations.get(nId).GetLine();
    							int nLineTo = mmoStations.get(nNextId).GetLine();
    							if(nLineFrom != nLineTo){
   									bCross = true;
    								nType = 4;
    							}
	    					}
	    				}
	    				StationItem entry = mmoStations.get(nId);
	    				entry.SetType(nType);
	    				mStationList.add(entry);
	    			}
	    		}
	    	}
	    }

	    
	    // load list
	    mPathListView = (ListView)findViewById(R.id.pathlistview);
        // create new adapter
	    mListAdapter = new StationListAdapter(this, mStationList, mnDeparturePortalId, mnArrivalPortalId);
        // set adapter to list view
	    mPathListView.setAdapter(mListAdapter);	

	    //mListAdapter.notifyDataSetChanged();
    }
    
    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                //Intent intent = new Intent(this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            	finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
