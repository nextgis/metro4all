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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import android.widget.SectionIndexer;
import android.widget.TextView;

public class StationExpandableListAdapter extends BaseExpandableListAdapter implements SectionIndexer {
	private Context mContext;
	private List <StationItem> mStationList;
	private Map<StationItem, List<PortalItem>> mPortalCollection;
	
	private HashMap<String, Integer> mAlphaIndexer; 

	private int mnType;
	private int mnMaxWidth, mnWheelWidth;
	
	private String[] msaSections;
	
	private LayoutInflater mInfalInflater;
	
	public StationExpandableListAdapter(Context c, List<StationItem> stationList, Map<StationItem, List<PortalItem>> portalCollection) {
		mContext = c;

		mInfalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mStationList = new ArrayList <StationItem>();
		mStationList.addAll(stationList);
		//mStationList = stationList;
		mPortalCollection = new HashMap<StationItem, List<PortalItem>>();
		mPortalCollection.putAll(portalCollection);
		//mPortalCollection = portalCollection;
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 1);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
		
		mAlphaIndexer = new HashMap<String, Integer>();
        for (int x = 0; x < mStationList.size(); x++) {  
             String s = mStationList.get(x).GetName();  
             String ch = s.substring(0, 1);  
             ch = ch.toUpperCase();  
             if (!mAlphaIndexer.containsKey(ch)){
            	 mAlphaIndexer.put(ch, x);
            	 
            	 StationItem sit = new StationItem(-1, ch, -1, -1);
            	 mStationList.add(x, sit);
            	 mPortalCollection.put(sit, null);
            	 
             }     
        }  	
        
        List<String> sectionList = new ArrayList<String>( mAlphaIndexer.keySet() );  

        Collections.sort(sectionList);  
        
        SelectStationActivity parentActivity = (SelectStationActivity)mContext;
        
        //Collections.sort(mStationList, parentActivity.new StationItemComparator());

        msaSections = new String[sectionList.size()];  

        sectionList.toArray(msaSections);
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
		List<PortalItem> ls = mPortalCollection.get(mStationList.get(groupPosition));
		if(ls == null)
			return 0;
		return ls.size();
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
		StationItem entry = (StationItem) getGroup(groupPosition);
		if(entry.GetId() == -1){
			if (convertView == null || convertView.findViewById(R.id.tvCategoryName) == null) {
				convertView = mInfalInflater.inflate(R.layout.select_category_row_layout, null);
			}
			TextView item = (TextView) convertView.findViewById(R.id.tvCategoryName);
			item.setText(entry.GetName());
		}
		else{
			if (convertView == null || convertView.findViewById(R.id.tvStationName) == null) {
				convertView = mInfalInflater.inflate(R.layout.select_station_row_layout, null);
			}
			TextView item = (TextView) convertView.findViewById(R.id.tvStationName);
			item.setText(entry.GetName());

			ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);

			File imgFile = new File(MainActivity.msRDataPath + "/icons", "" + entry.GetLine() + "" + entry.GetType() + ".png");		
			Log.d(MainActivity.TAG, imgFile.getPath());
			if(imgFile.exists()){
			
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    ivIcon.setImageBitmap(myBitmap);
			}	
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

	@Override
	public int getPositionForSection(int arg0) {
		return mAlphaIndexer.get(msaSections[arg0]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return msaSections;
	}

}
