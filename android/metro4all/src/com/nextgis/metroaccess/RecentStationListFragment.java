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
import com.nextgis.metroaccess.data.PortalItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.os.Bundle;

public class RecentStationListFragment extends SherlockFragment {
	protected ExpandableListView m_oExpListView;
	protected StationExpandableListAdapter m_oExpListAdapter;
	
	protected TextView m_tvNotes;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {   
    	
    	
    	this.setRetainInstance(true);
    	
     	SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
    	
    	View view = inflater.inflate(R.layout.recent_stationlist_fragment, container, false);
		
        m_tvNotes = (TextView)view.findViewById(R.id.tvNotes);   
		if( m_tvNotes != null){
			if(!parentActivity.HasLimits()){
				m_tvNotes.setVisibility(View.INVISIBLE);
			}
		}
		
    	m_oExpListView = (ExpandableListView) view.findViewById(R.id.lvStationList);
    	m_oExpListAdapter = new StationExpandableListAdapter(parentActivity);
    	m_oExpListAdapter.onInit();
        m_oExpListView.setAdapter(m_oExpListAdapter);
        m_oExpListView.setFastScrollEnabled(true); 
        m_oExpListView.setGroupIndicator(null);

        m_oExpListView.setOnChildClickListener(new OnChildClickListener() {
 
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	final PortalItem selected = (PortalItem) m_oExpListAdapter.getChild(groupPosition, childPosition);
            	SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
            	parentActivity.Finish(selected.GetStationId(), selected.GetId());
                return true;
            }
        });
        
        return view;

    }
    
	public void Update(){
		if( m_tvNotes != null){
			SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
			if(parentActivity.HasLimits()){
				m_tvNotes.setVisibility(View.VISIBLE);
			}
			else{
				m_tvNotes.setVisibility(View.INVISIBLE);
			}
		}
		
		if(m_oExpListAdapter != null){
			m_oExpListAdapter.Update();
			m_oExpListAdapter.notifyDataSetChanged();
		}
	}
}
