/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Stanislav Petriakov
 ******************************************************************************
 *   Copyright (C) 2015 NextGIS
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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import static com.nextgis.metroaccess.Constants.TAG;

public abstract class SelectStationListFragment extends Fragment {
    protected StationExpandableListView m_oExpListView;
    protected StationExpandableListAdapter m_oExpListAdapter;
    protected String mTab;
    private EditText etFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRetainInstance(true);

        View view = inflater.inflate(R.layout.stationlist_fragment, container, false);

        m_oExpListView = (StationExpandableListView) view.findViewById(R.id.lvStationList);
        m_oExpListView.setFastScrollEnabled(true);
        m_oExpListView.setGroupIndicator(null);

        m_oExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ((Analytics) getActivity().getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.PORTAL, mTab);

                final PortalItem selected = (PortalItem) m_oExpListAdapter.getChild(groupPosition, childPosition);
                SelectStationActivity parentActivity = (SelectStationActivity) getActivity();
                parentActivity.Finish(selected.GetStationId(), selected.GetId());
                return true;
            }
        });

        m_oExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (m_oExpListAdapter.getGroup(groupPosition).isSection()) {
                    ((Analytics) getActivity().getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.HEADER, mTab);

                    int scrollTo = groupPosition;

                    for (int i = 0; i < groupPosition; i++)
                        if (m_oExpListView.isGroupExpanded(i))
                            scrollTo += m_oExpListAdapter.getChildrenCount(i);

                    m_oExpListView.smoothScrollToPositionFromTop(scrollTo);
                }
                else if (((StationItem) m_oExpListAdapter.getGroup(groupPosition)).GetPortalsCount() == 0)
                    Toast.makeText(getActivity(), getString(R.string.sNoPortals), Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        m_oExpListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (!m_oExpListAdapter.getGroup(i).isSection())
                    ((Analytics) getActivity().getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.STATION_EXPAND, mTab);
            }
        });

        m_oExpListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                if (!m_oExpListAdapter.getGroup(i).isSection())
                    ((Analytics) getActivity().getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.STATION_COLLAPSE, mTab);
            }
        });

        etFilter = (EditText) view.findViewById(R.id.etStationFilterEdit);
        TextWatcher searchTextWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "*** Search value changed: " + s.toString());
                m_oExpListAdapter.getFilter().filter(s.toString());
            }
        };
        etFilter.addTextChangedListener(searchTextWatcher);

        return view;
    }

    private String getDirection() { // for GA
        return ((SelectStationActivity) getActivity()).IsIn() ? Analytics.FROM : Analytics.TO;
    }

    public void Update(){
        etFilter.setText("");
    }
}
