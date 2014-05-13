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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.nextgis.metroaccess.data.BarrierItem;
import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.RouteItem;
import com.nextgis.metroaccess.data.StationItem;

public class StationListView extends SherlockActivity implements OnNavigationListener{
	
	protected int mnType;
	protected int mnMaxWidth, mnWheelWidth;	
	protected ExpandableListView mExpListView;
	protected int mnPathCount, mnDeparturePortalId, mnArrivalPortalId;
    
	protected Map<Integer, StationItem> mmoStations;
	protected Map<String, int[]> mmoCrosses;
	public static final char DEGREE_CHAR = (char) 0x00B0;
	
	protected RouteExpandableListAdapter moAdapters[];

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mbFilled = false;
        setContentView(R.layout.station_list_view);
        
	    ActionBar actionBar = getSupportActionBar();
		Context context = actionBar.getThemedContext();		
        
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 2);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);	        

	    Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	    	mnDeparturePortalId = extras.getInt("dep_" + MainActivity.BUNDLE_PORTALID_KEY);
	    	mnArrivalPortalId = extras.getInt("arr_" + MainActivity.BUNDLE_PORTALID_KEY);
	    	
	    	mnPathCount = extras.getInt(MainActivity.BUNDLE_PATHCOUNT_KEY);
	    	mmoStations = MainActivity.GetGraph().GetStations();//(Map<Integer, StationItem>) extras.getSerializable(MainActivity.BUNDLE_STATIONMAP_KEY);
	    	mmoCrosses = MainActivity.GetGraph().GetCrosses();//(Map<String, int[]>) extras.getSerializable(MainActivity.BUNDLE_CROSSESMAP_KEY);

	    	String[] data = new String[mnPathCount];
	    	for(int i = 0; i < mnPathCount; i++){
	    		data[i] = getString(R.string.sRoute) + " " + (i + 1);
	    	}
	    			
			ArrayAdapter<CharSequence> adapter= new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_dropdown_item, data);
			adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		    
		    actionBar.setDisplayShowTitleEnabled(false);
		    actionBar.setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
		    actionBar.setListNavigationCallbacks((SpinnerAdapter)adapter, this);   
		    

		    if(mnPathCount > 0){
		    	moAdapters = new RouteExpandableListAdapter[mnPathCount];
			    for(int i = 0; i < mnPathCount; i++){
			    	List<Integer> list = extras.getIntegerArrayList(MainActivity.BUNDLE_PATH_KEY + i);
			    	moAdapters[i] = CreateAndFillAdapter(list);
			    }
		    }
	    }

	    
	    // load list
    	mExpListView = (ExpandableListView) findViewById(R.id.lvPathList);

    	// set adapter to list view
	    mExpListView.setAdapter(moAdapters[0]);

        mExpListView.setFastScrollEnabled(true);
 
        mExpListView.setGroupIndicator(null);
        
        //mExpListView.setOnGroupClickListener(this);
    }
	
	protected RouteExpandableListAdapter CreateAndFillAdapter(List<Integer> list) {
   		boolean bCross = false;
   		boolean bCrossCross = false;
   		if(list != null){
    			
			//add entrance
	   		List<RouteItem> routeList = new ArrayList<RouteItem>();
	   		RouteItem oEntrance = new RouteItem(mnDeparturePortalId, getString(R.string.sEntranceName), list.get(0), -1, 6);
	   		routeList.add(FillBarriersForEntrance(oEntrance, list.get(0)));	    		
	    			
		    for(int i = 0; i < list.size(); i++){
		    	bCrossCross = false;
	    		int nId = list.get(i);
	    		int nType = 5;
				if(bCross){
					bCross = false;
					if(i != list.size() - 1){
						int nNextId = list.get(i + 1);
						int nLineFrom = mmoStations.get(nId).GetLine();
						int nLineTo = mmoStations.get(nNextId).GetLine();
						if(nLineFrom != nLineTo){
							nType = 9;
							bCrossCross = true;
							bCross = true;
						}
						else{
							bCross = false;
							nType = 3;
						}
					}
					else{
						bCross = false;
						nType = 3;
					}
				}
				
				if(!bCrossCross && i != list.size() - 1){
					int nNextId = list.get(i + 1);
					int nLineFrom = mmoStations.get(nId).GetLine();
					int nLineTo = mmoStations.get(nNextId).GetLine();
					if(nLineFrom != nLineTo){
						bCross = true;
						nType = 4;
					}
				}
				
	    		StationItem entry = mmoStations.get(nId);
				RouteItem oSta = new RouteItem(entry.GetId(), entry.GetName(), entry.GetLine(), entry.GetNode(), nType);
				if(i == list.size() - 1){
					routeList.add(FillBarriersForExit(oSta, mnArrivalPortalId));
				}
				else{
					routeList.add(FillBarriers(oSta, entry.GetId(), list.get(i + 1)));
				}
	    	}
		    		
		    //add exit
		    StationItem sit = mmoStations.get(list.get(list.size() - 1));
			RouteItem oExit = new RouteItem(mnArrivalPortalId, getString(R.string.sExitName), sit.GetLine(), -1, 7);
			routeList.add(oExit);
			
			int[] naBarriers = {0,0,0,0,0,0,0,0};
			for(RouteItem rit : routeList){
				List<BarrierItem> bits = rit.GetProblems();
				if(bits != null){
					for(BarrierItem bit : bits){
						if(bit.GetId() == 0){
							if(naBarriers[0] == 0 || naBarriers[0] > bit.GetValue()){
								naBarriers[0] = bit.GetValue();
							}
						}
						else if(bit.GetId() == 1){
							naBarriers[1] += bit.GetValue();							
						}
						else if(bit.GetId() == 2){
							naBarriers[2] += bit.GetValue();
						}
						else if(bit.GetId() == 3){
							naBarriers[3] += bit.GetValue();
						}
						else if(bit.GetId() == 4){
							naBarriers[4] += bit.GetValue();
						}
						else if(bit.GetId() == 5){
							if(naBarriers[5] == 0){ 
								naBarriers[5] = bit.GetValue();
							}
							else if(naBarriers[5] > bit.GetValue()){
								naBarriers[5] = bit.GetValue();
							}
						}
						else if(bit.GetId() == 6){
							if(naBarriers[6] < bit.GetValue()){
								naBarriers[6] = bit.GetValue();
							}
						}
						else if(bit.GetId() == 7){
							if(naBarriers[7] < bit.GetValue()){
								naBarriers[7] = bit.GetValue();
							}
						}
					}
				}
			}
			
			RouteItem oSumm = new RouteItem(-1, getString(R.string.sSummary), 0, 0, 0);
			FillWithData(naBarriers, oSumm, false);	    		
			routeList.add(0, oSumm);
			
			
	        // create new adapter
		    RouteExpandableListAdapter expListAdapter = new RouteExpandableListAdapter(this, routeList);
	        
		    return expListAdapter;
   		}
   		return null;
	}

	protected RouteItem FillBarriers(RouteItem it, int StationFromId, int StationToId){
		int[] naBarriers = mmoCrosses.get("" + StationFromId + "->" + StationToId);
		if(naBarriers != null && naBarriers.length == 8){
			FillWithData(naBarriers, it, false);
		}
		return it;
	}

	protected RouteItem FillBarriersForEntrance(RouteItem it, int StationId){
		StationItem sit = mmoStations.get(StationId);
		if(sit != null){
			PortalItem pit = sit.GetPortal(it.GetId());
			if(pit != null){
				FillWithData(pit.GetDetailes(), it, false);		
			}
			it.SetLine(sit.GetLine());
		}		
		return it;
	}	
	
	protected RouteItem FillBarriersForExit(RouteItem it, int PortalId){
		StationItem sit = mmoStations.get(it.GetId());
		if(sit != null){
			PortalItem pit = sit.GetPortal(PortalId);
			if(pit != null){
				FillWithData(pit.GetDetailes(), it, false);		
			}
			it.SetLine(sit.GetLine());
		}
		
		return it;
	}

	protected void FillWithData(int[] naBarriers, RouteItem it, boolean bWithZeroes){
		if(bWithZeroes || naBarriers[0] > 0){//max_width
			boolean bProblem = naBarriers[0] < mnMaxWidth;
			String sName = getString(R.string.sMaxWCWidth) + ": " + naBarriers[0] / 10 + " " + getString(R.string.sCM);
			BarrierItem bit = new BarrierItem(0, sName, bProblem, naBarriers[0]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[1] > 0){//min_step
			String sName = getString(R.string.sStairsCount) + ": " + naBarriers[1];
			BarrierItem bit = new BarrierItem(1, sName, false, naBarriers[1]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[2] > 0){//min_step_ramp
			String sName = getString(R.string.sStairsWORails) + ": " + naBarriers[2];
			BarrierItem bit = new BarrierItem(2, sName, false, naBarriers[2]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[3] > 0){//lift
			String sName = getString(R.string.sLift) + ": " + naBarriers[3];
			BarrierItem bit = new BarrierItem(3, sName, false, naBarriers[3]);
			it.AddBarrier(bit);
		}	
		else{
			String sName = getString(R.string.sLift) + ": " + getString(R.string.sNo);
			BarrierItem bit = new BarrierItem(3, sName, false, naBarriers[3]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[4] > 0){//lift_minus_step
			String sName = getString(R.string.sLiftEconomy) + ": " + naBarriers[4];
			BarrierItem bit = new BarrierItem(4, sName, false, naBarriers[4]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[5] > 0){//min_rail_width
			String sName = getString(R.string.sMinRailWidth) + ": " + naBarriers[5] / 10 + " " + getString(R.string.sCM);
			boolean bCanRoll = naBarriers[5] < mnWheelWidth && naBarriers[6] > mnWheelWidth;
			BarrierItem bit = new BarrierItem(5, sName, !bCanRoll, naBarriers[5]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[6] > 0){//max_rail_width
			String sName = getString(R.string.sMaxRailWidth) + ": " + naBarriers[6] / 10 + " " + getString(R.string.sCM);
			boolean bCanRoll = naBarriers[5] < mnWheelWidth && naBarriers[6] > mnWheelWidth;
			BarrierItem bit = new BarrierItem(6, sName, !bCanRoll, naBarriers[6]);
			it.AddBarrier(bit);
		}
		if(bWithZeroes || naBarriers[7] > 0){//max_angle
			String sName = getString(R.string.sMaxAngle) + ": " + naBarriers[7] + DEGREE_CHAR;
			BarrierItem bit = new BarrierItem(7, sName, false, naBarriers[7]);
			it.AddBarrier(bit);
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
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    	// set adapter to list view
	    mExpListView.setAdapter(moAdapters[itemPosition]);

		return true;
	}
}
