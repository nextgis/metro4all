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

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List <StationItem> mStationList;
	private Map<StationItem, List<PortalItem>> mPortalCollection;

	private int mnType;
	private int mnMaxWidth, mnWheelWidth;
	
	public StationExpandableListAdapter(Context c, List<StationItem> stationList, Map<StationItem, List<PortalItem>> portalCollection) {
		mContext = c;
		mStationList = stationList;
		mPortalCollection = portalCollection;
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 1);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
		
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mPortalCollection.get(mStationList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		PortalItem pit = (PortalItem)getChild(groupPosition, childPosition);
		return pit.GetId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.select_portal_row_layout, null);
		}
		PortalItem entry = (PortalItem) getChild(groupPosition, childPosition);
		TextView item = (TextView) convertView.findViewById(R.id.txPortalName);
		//
		if(mnType > 1){
			boolean bSmallWidth = entry.GetDetailes()[0] < mnMaxWidth;
			boolean bCanRoll = entry.GetDetailes()[5] < mnWheelWidth && entry.GetDetailes()[6] > mnWheelWidth;
			if(bSmallWidth || !bCanRoll){
				item.setTextColor(Color.RED);
			}
		}
		//
		item.setText(entry.GetName());

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mPortalCollection.get(mStationList.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mStationList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mStationList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		StationItem sit = (StationItem)mStationList.get(groupPosition);
		return sit.GetId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.select_station_row_layout, null);
		}
		StationItem entry = (StationItem) getGroup(groupPosition);
		TextView item = (TextView) convertView.findViewById(R.id.tvStationName);
		item.setText(entry.GetName());

		ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);

		File imgFile = new File(MainActivity.msRDataPath + "/icons", "" + entry.GetLine() + "" + entry.GetType() + ".png");		
		Log.d(MainActivity.TAG, imgFile.getPath());
		if(imgFile.exists()){
		
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ivIcon.setImageBitmap(myBitmap);
		}	

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
