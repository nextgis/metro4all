/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Authors:  Baryshnikov Dmitriy aka Bishop (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2013-2015 NextGIS
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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nextgis.metroaccess.data.StationItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import static com.nextgis.metroaccess.SelectStationActivity.getRecentStations;

public class RecentStationListFragment extends SelectStationListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mTab = Analytics.TAB_RECENT;
     	SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
    	m_oExpListAdapter = new RecentExpandableListAdapter(parentActivity);
        m_oExpListView.setAdapter(m_oExpListAdapter);
        result.findViewById(R.id.etStationFilterEdit).setVisibility(View.GONE);
        
        return result;
    }

    public void Update(){
		if(m_oExpListAdapter != null){
			m_oExpListAdapter.Update();
			m_oExpListAdapter.notifyDataSetChanged();
		}
	}

    private class RecentExpandableListAdapter extends StationExpandableListAdapter {

        public RecentExpandableListAdapter(Context c) {
            super(c);
            onInit();
        }

        protected void onInit() {
            mStationList.clear();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Map<Integer, StationItem> omStations = MainActivity.GetGraph().GetStations();
            JSONArray stationsIds = getRecentStations(prefs, m_bIn);

            if (stationsIds != null)
                for (int i = stationsIds.length() - 1; i >= 0; i--) {
                    StationItem sit = null;

                    try {
                        sit = omStations.get(stationsIds.getInt(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (sit != null && !mStationList.contains(sit))
                        mStationList.add(sit);
                }
        }
    }
}
