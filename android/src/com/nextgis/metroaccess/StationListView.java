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
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class StationListView extends SherlockActivity {
	
	protected int mnType;
	protected int mnMaxWidth, mnWheelWidth;	
	protected ExpandableListView mExpListView;
	protected int mnPathCount, mnDeparturePortalId, mnArrivalPortalId;
    
	protected Map<Integer, StationItem> mmoStations;
	protected Map<String, int[]> mmoCrosses;
    protected List<RouteItem> mRouteList;
	public static final char DEGREE_CHAR = (char) 0x00B0;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mbFilled = false;
        setContentView(R.layout.station_list_view);
        
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 1);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);	        

		mRouteList = new ArrayList<RouteItem>();
	    Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	    	mnDeparturePortalId = extras.getInt("dep_" + MainActivity.BUNDLE_PORTALID_KEY);
	    	mnArrivalPortalId = extras.getInt("arr_" + MainActivity.BUNDLE_PORTALID_KEY);
	    	
	    	mnPathCount = extras.getInt(MainActivity.BUNDLE_PATHCOUNT_KEY);
	    	mmoStations = (Map<Integer, StationItem>) extras.getSerializable(MainActivity.BUNDLE_STATIONMAP_KEY);
	    	mmoCrosses = (Map<String, int[]>) extras.getSerializable(MainActivity.BUNDLE_CROSSESMAP_KEY);
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
	    			
	    			//add entrance
			    	RouteItem oEntrance = new RouteItem(mnDeparturePortalId, getString(R.string.sEntranceName), list.get(0), 6);
			    	mRouteList.add(FillBarriersForEntrance(oEntrance, list.get(0)));	    		
	    			
		    		for(int i = 0; i < list.size(); i++){
	    				int nId = list.get(i);
	    				int nType = 5;
						if(bCross){
							bCross = false;
							nType = 3;
						}
						
						if(i != list.size() - 1){
							int nNextId = list.get(i + 1);
							int nLineFrom = mmoStations.get(nId).GetLine();
							int nLineTo = mmoStations.get(nNextId).GetLine();
							if(nLineFrom != nLineTo){
								bCross = true;
								nType = 4;
							}
						}
						
	    				StationItem entry = mmoStations.get(nId);
			    		RouteItem oSta = new RouteItem(entry.GetId(), entry.GetName(), entry.GetLine(), nType);
			    		if(i == list.size() - 1){
			    			mRouteList.add(FillBarriersForExit(oSta, mnArrivalPortalId));
			    		}
			    		else{
			    			mRouteList.add(FillBarriers(oSta, entry.GetId(), list.get(i + 1)));
			    		}
	    			}
		    		
		    		//add exit
		    		StationItem sit = mmoStations.get(list.get(list.size() - 1));
			    	RouteItem oExit = new RouteItem(mnArrivalPortalId, getString(R.string.sExitName), sit.GetLine(), 7);
		    		mRouteList.add(oExit);	 
	    		}	    		
	    	}
	    }

	    
	    // load list
    	mExpListView = (ExpandableListView) findViewById(R.id.lvPathList);
        // create new adapter
	    final RouteExpandableListAdapter expListAdapter = new RouteExpandableListAdapter(this, mRouteList);
        // set adapter to list view
	    mExpListView.setAdapter(expListAdapter);	

        mExpListView.setFastScrollEnabled(true);
 
        mExpListView.setGroupIndicator(null);
    }
	
	protected RouteItem FillBarriers(RouteItem it, int StationFromId, int StationToId){
		int[] naBarriers = mmoCrosses.get("" + StationFromId + "->" + StationToId);
		if(naBarriers != null && naBarriers.length == 8){
			FillWithData(naBarriers, it);
		}
		return it;
	}

	protected RouteItem FillBarriersForEntrance(RouteItem it, int StationId){
		StationItem sit = mmoStations.get(StationId);
		if(sit != null){
			PortalItem pit = sit.GetPortal(it.GetId());
			if(pit != null){
				FillWithData(pit.GetDetailes(), it);		
			}
		}
		it.SetLine(sit.GetLine());
		return it;
	}	
	
	protected RouteItem FillBarriersForExit(RouteItem it, int PortalId){
		StationItem sit = mmoStations.get(it.GetId());
		if(sit != null){
			PortalItem pit = sit.GetPortal(PortalId);
			if(pit != null){
				FillWithData(pit.GetDetailes(), it);		
			}
		}
		it.SetLine(sit.GetLine());
		return it;
	}

	protected void FillWithData(int[] naBarriers, RouteItem it){
		if(naBarriers[0] > 0){//max_width
			boolean bProblem = naBarriers[0] < mnMaxWidth;
			String sName = getString(R.string.sMaxWCWidth) + ": " + naBarriers[0] / 10 + " " + getString(R.string.sCM);
			BarrierItem bit = new BarrierItem(0, sName, bProblem, naBarriers[0]);
			it.AddBarrier(bit);
		}
		if(naBarriers[1] > 0){//min_step
			String sName = getString(R.string.sStairsCount) + ": " + naBarriers[1];
			BarrierItem bit = new BarrierItem(1, sName, false, naBarriers[1]);
			it.AddBarrier(bit);
		}
		if(naBarriers[2] > 0){//min_step_ramp
			String sName = getString(R.string.sStairsWORails) + ": " + naBarriers[2];
			BarrierItem bit = new BarrierItem(2, sName, false, naBarriers[2]);
			it.AddBarrier(bit);
		}
		if(naBarriers[3] > 0){//lift
			String sName = getString(R.string.sLift) + ": " + naBarriers[3];
			BarrierItem bit = new BarrierItem(3, sName, false, naBarriers[3]);
			it.AddBarrier(bit);
		}				
		if(naBarriers[4] > 0){//lift_minus_step
			String sName = getString(R.string.sLiftEconomy) + ": " + naBarriers[4];
			BarrierItem bit = new BarrierItem(4, sName, false, naBarriers[4]);
			it.AddBarrier(bit);
		}
		if(naBarriers[5] > 0){//min_rail_width
			String sName = getString(R.string.sMinRailWidth) + ": " + naBarriers[5] / 10 + " " + getString(R.string.sCM);
			boolean bCanRoll = naBarriers[5] < mnWheelWidth && naBarriers[6] > mnWheelWidth;
			BarrierItem bit = new BarrierItem(5, sName, !bCanRoll, naBarriers[5]);
			it.AddBarrier(bit);
		}
		if(naBarriers[6] > 0){//max_rail_width
			String sName = getString(R.string.sMaxRailWidth) + ": " + naBarriers[6] / 10 + " " + getString(R.string.sCM);
			boolean bCanRoll = naBarriers[5] < mnWheelWidth && naBarriers[6] > mnWheelWidth;
			BarrierItem bit = new BarrierItem(6, sName, !bCanRoll, naBarriers[6]);
			it.AddBarrier(bit);
		}
		if(naBarriers[7] > 0){//max_angle
			String sName = getString(R.string.sMaxAngle) + ": " + naBarriers[7] + DEGREE_CHAR;
			BarrierItem bit = new BarrierItem(7, sName, false, naBarriers[7]);
			it.AddBarrier(bit);
		}		
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
