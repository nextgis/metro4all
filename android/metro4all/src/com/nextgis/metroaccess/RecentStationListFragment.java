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

import com.actionbarsherlock.app.SherlockFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.os.Bundle;

public class RecentStationListFragment extends SherlockFragment {
	protected ExpandableListView mExpListView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {   
    	
    	
    	this.setRetainInstance(true);
    	
     	SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
    	
    	View view = inflater.inflate(R.layout.recent_stationlist_fragment, container, false);

     	mExpListView = (ExpandableListView) view.findViewById(R.id.lvStationList);
        final StationExpandableListAdapter expListAdapter = new StationExpandableListAdapter(parentActivity);
        expListAdapter.onInit();
        mExpListView.setAdapter(expListAdapter);
        mExpListView.setFastScrollEnabled(true);
 
        mExpListView.setGroupIndicator(null);

        mExpListView.setOnChildClickListener(new OnChildClickListener() {
 
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	final PortalItem selected = (PortalItem) expListAdapter.getChild(groupPosition, childPosition);
            	SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
            	parentActivity.Finish(selected.GetStationId(), selected.GetId());
                return true;
            }
        });
        
        return view;

    }
}
