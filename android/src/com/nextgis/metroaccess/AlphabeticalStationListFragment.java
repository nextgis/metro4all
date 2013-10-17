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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.content.Context;
import android.os.Bundle;

public class AlphabeticalStationListFragment extends SherlockFragment {
	protected static final String TAG = AlphabeticalStationListFragment.class
			.getSimpleName();
	protected ExpandableListView mExpListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.setRetainInstance(true);

		SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
		View view = inflater.inflate(
				R.layout.alphabetical_stationlist_fragment, container, false);

		mExpListView = (ExpandableListView) view
				.findViewById(R.id.lvStationList);
		final StationIndexedExpandableListAdapter expListAdapter = new StationIndexedExpandableListAdapter(
				parentActivity, parentActivity.GetStationList(),
				parentActivity.GetPortalCollection());
		expListAdapter.onInit();
		mExpListView.setAdapter(expListAdapter);
		mExpListView.setFastScrollEnabled(true);

		mExpListView.setGroupIndicator(null);

		mExpListView.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				final PortalItem selected = (PortalItem) expListAdapter
						.getChild(groupPosition, childPosition);
				SelectStationActivity parentActivity = (SelectStationActivity) getSherlockActivity();
				parentActivity.Finish(selected.GetStationId(), selected.GetId());
				return true;
			}
		});
		
		mExpListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				
				return false;
			}			
		});

		EditText stationFilterEdit = (EditText) view
				.findViewById(R.id.etStationFilterEdit);
		TextWatcher searchTextWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ignore
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// ignore
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d(TAG, "*** Search value changed: " + s.toString());
				expListAdapter.getFilter().filter(s.toString());
			}
		};
		stationFilterEdit.addTextChangedListener(searchTextWatcher);
		
		return view;
	}
}
