/*******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Author:   Dmitry Baryshnikov , polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013,2014 NextGIS
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
import java.util.List;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.nextgis.metroaccess.Constants.*;

public class StationExpandableListAdapter extends BaseExpandableListAdapter implements Filterable{

	protected Context mContext;
	protected List <StationItem> mStationList;

	protected int mnType;
	protected int mnMaxWidth, mnWheelWidth;
	protected boolean m_bHaveLimits;

	protected LayoutInflater mInfalInflater;
	protected List<StationItem> moOriginalStationList;

	protected boolean m_bIn;

	public StationExpandableListAdapter(Context c) {
		mContext = c;

		mInfalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 2);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
		m_bHaveLimits = prefs.getBoolean(PreferencesActivity.KEY_PREF_HAVE_LIMITS, false);

		SelectStationActivity act = (SelectStationActivity)mContext;
		m_bIn = act.IsIn();
    }

	protected void FillArrays(){
		mStationList.clear();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Map<Integer, StationItem> omStations = MainActivity.GetGraph().GetStations();

		if(m_bIn){
			int size = prefs.getInt("recent_dep_counter", 0);
			for(int i = 0; i < size; i++){
				int nStationId = prefs.getInt("recent_dep_" + BUNDLE_STATIONID_KEY+i, -1);
				//int nPortalId = prefs.getInt("recent_dep_"+MainActivity.BUNDLE_PORTALID_KEY+i, -1);

				StationItem sit = omStations.get(nStationId);
				if(sit != null && !mStationList.contains(sit)){
					mStationList.add(sit);
				}
			}
		}
		else{
			int size = prefs.getInt("recent_arr_counter", 0);
			for(int i = 0; i < size; i++){
				int nStationId = prefs.getInt("recent_arr_" + BUNDLE_STATIONID_KEY+i, -1);
				//int nPortalId = prefs.getInt("recent_arr_" + BUNDLE_PORTALID_KEY+i, -1);

				StationItem sit = omStations.get(nStationId);
				if(sit != null && !mStationList.contains(sit)){
					mStationList.add(sit);
				}
			}
		}
	}

	protected void onInit(){
		mStationList = new ArrayList<StationItem>();

		//load recent data
		FillArrays();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		StationItem sit = mStationList.get(groupPosition);
		if(sit != null){
			List<PortalItem> lpit = sit.GetPortals(m_bIn);
			if(lpit != null)
				return lpit.get(childPosition);
		}

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		PortalItem pit = (PortalItem)getChild(groupPosition, childPosition);
		if(pit != null)
			return pit.GetId();
		return -1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInfalInflater.inflate(R.layout.select_portal_row_layout, null);
		}
		PortalItem entry = (PortalItem) getChild(groupPosition, childPosition);
		TextView item = (TextView) convertView.findViewById(R.id.txPortalName);
		//
		if(mnType > 1){
			boolean bSmallWidth = entry.GetDetailes()[0] < mnMaxWidth;
			boolean bCanRoll = entry.GetDetailes()[5] < mnWheelWidth && entry.GetDetailes()[6] > mnWheelWidth;
			if(m_bHaveLimits && (bSmallWidth || !bCanRoll)){
				item.setTextColor(Color.RED);
			}
			else{
				TypedValue tv = new TypedValue();
				mContext.getTheme().resolveAttribute(android.R.attr.textColorSecondary, tv, true);
				item.setTextColor(mContext.getResources().getColor(tv.resourceId));
			}
		}
		//
		item.setText(entry.GetName());

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		StationItem sit = mStationList.get(groupPosition);
		if(sit != null){
			List<PortalItem> ls = sit.GetPortals(m_bIn);
			if(ls == null)
				return 0;
			return ls.size();
		}
		return 0;
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
		if(sit != null)
			return sit.GetId();
		return -1;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final StationItem entry = (StationItem) getGroup(groupPosition);
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

			String sRouteDataPath = MainActivity.GetGraph().GetCurrentRouteDataPath();
			File imgFile = new File(sRouteDataPath + "/icons", "" + entry.GetLine() + "" + entry.GetType() + ".png");
			Log.d(TAG, imgFile.getPath());
			if(imgFile.exists()){

			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    ivIcon.setImageBitmap(myBitmap);
			}

            TextView tvSchemeButton = (TextView) convertView.findViewById(R.id.tvStationSchemeButton);
            final File schemaFile =
                    new File(sRouteDataPath + "/schemes", "" + entry.GetNode() + ".png");

            if (schemaFile.exists()) {
                tvSchemeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            Log.d(TAG, schemaFile.getPath());

                            Bundle bundle = new Bundle();
                            bundle.putString("image_path", schemaFile.getPath());
                            Intent intentView = new Intent(mContext,
                                    com.nextgis.metroaccess.StationImageView.class);
                            intentView.putExtras(bundle);

                            mContext.startActivity(intentView);

                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Call failed", e);
                        }
                    }
                });

                tvSchemeButton.setVisibility(View.VISIBLE);
            } else {
                tvSchemeButton.setVisibility(View.INVISIBLE);
            }


            TextView tvMapButton = (TextView) convertView.findViewById(R.id.tvPortalMapButton);
            tvMapButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SelectStationActivity parentActivity = (SelectStationActivity) v.getContext();
                    Intent intent = new Intent(parentActivity, StationMapActivity.class);
                    intent.putExtra(PARAM_SEL_STATION_ID, entry.GetId());
                    intent.putExtra(PARAM_PORTAL_DIRECTION, parentActivity.IsIn());
                    parentActivity.startActivityForResult(intent, PORTAL_MAP_RESULT);
                }
            });
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
	public Filter getFilter() {
		return new Filter() {

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            final FilterResults oReturn = new FilterResults();
	            final ArrayList<StationItem> results = new ArrayList<StationItem>();
	            if (moOriginalStationList == null)
	            	moOriginalStationList = mStationList;
	            if (constraint != null) {
	                if (moOriginalStationList != null && moOriginalStationList.size() > 0) {
	                    for (final StationItem station : moOriginalStationList) {
	                        if (station.GetName().toLowerCase()
	                                .contains(constraint.toString()))
	                            results.add(station);
	                    }
	                }
	                oReturn.values = results;
	            }
	            return oReturn;
	        }

	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint,
	                FilterResults results) {
	        	mStationList = (ArrayList<StationItem>) results.values;
	            notifyDataSetChanged();
	        }
	    };
	}

	public void Update(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 2);
		mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
		mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
		m_bHaveLimits = prefs.getBoolean(PreferencesActivity.KEY_PREF_HAVE_LIMITS, false);

		FillArrays();
	}
}
