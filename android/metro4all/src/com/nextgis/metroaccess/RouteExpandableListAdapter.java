/*******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Authors:  Baryshnikov Dmitriy aka Bishop (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2013,2015 NextGIS
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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextgis.metroaccess.data.BarrierItem;
import com.nextgis.metroaccess.data.RouteItem;

import java.io.File;
import java.util.List;

import static com.nextgis.metroaccess.Constants.BUNDLE_PORTALID_KEY;
import static com.nextgis.metroaccess.Constants.BUNDLE_STATIONID_KEY;
import static com.nextgis.metroaccess.Constants.PARAM_ACTIVITY_FOR_RESULT;
import static com.nextgis.metroaccess.Constants.PARAM_PORTAL_DIRECTION;
import static com.nextgis.metroaccess.Constants.PARAM_ROOT_ACTIVITY;
import static com.nextgis.metroaccess.Constants.PARAM_SCHEME_PATH;
import static com.nextgis.metroaccess.Constants.TAG;

public class RouteExpandableListAdapter extends BaseExpandableListAdapter {
	protected Context mContext;
	protected List <RouteItem> maRouteList;
    protected int mDeparturePortalId, mArrivalPortalId, mDepartureStationId, mArrivalStationId;

	protected LayoutInflater mInfalInflater;
	
	public RouteExpandableListAdapter(Context c, List<RouteItem> RouteList) {
		mContext = c;
		maRouteList = RouteList;
		
		mInfalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		RouteItem rit = maRouteList.get(groupPosition);
		if(rit != null)
			return rit.GetProblems().get(childPosition);
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		BarrierItem bit = (BarrierItem)getChild(groupPosition, childPosition);
		if(bit != null)
			return bit.GetId();
		return -1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInfalInflater.inflate(R.layout.barrier_row_layout, null);
		}

		RouteItem rit = (RouteItem)getGroup(groupPosition);
		BarrierItem bit = (BarrierItem)getChild(groupPosition, childPosition);
		TextView item = (TextView) convertView.findViewById(R.id.txBarrierName);
		//
		if(bit.IsProblem())
			item.setTextColor(Color.RED);
		else
			item.setTextColor(mContext.getResources().getColor(R.color.bkColorStrongDark));
		//
		item.setText(bit.GetName());
		
		ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
		// set data to display
//		String sRouteDataPath = MainActivity.GetGraph().GetCurrentRouteDataPath();
//	    File imgFile = new File(sRouteDataPath + "/icons", "" + rit.GetLine() + "8.png");
//		Log.d(TAG, imgFile.getPath());

        Bitmap myBitmap = MainActivity.getBitmapFromSVG(mContext, rit, true);

//		if(rit.GetType() != 7 && imgFile.exists()){
		if(rit.GetType() != 7 && myBitmap != null){
//		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ivIcon.setImageBitmap(myBitmap);
		    ivIcon.setVisibility(View.VISIBLE);
		}	
		else{
			ivIcon.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		RouteItem rit = maRouteList.get(groupPosition);
		if(rit != null)
			return rit.GetProblems().size();
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return maRouteList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return maRouteList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		RouteItem rit = (RouteItem)maRouteList.get(groupPosition);
		if(rit != null)
			return rit.GetId();
		return -1;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final RouteItem entry = (RouteItem) getGroup(groupPosition);
		if (convertView == null) {
			convertView = mInfalInflater.inflate(R.layout.station_row_layout, null);
		}

		TextView item = (TextView) convertView.findViewById(R.id.tvStationName);
		item.setText(entry.GetName());
//        item.setTextAppearance(mContext, entry.GetType() != 5 ? android.R.style.TextAppearance_Medium : android.R.style.TextAppearance_Small);
//        item.setTextColor(mContext.getResources().getColor(entry.GetType() != 5 ? android.R.color.black : R.color.darkGray));
		
		TextView subitem = (TextView) convertView.findViewById(R.id.tvBarriersExist);
		if(entry.GetProblems().size() > 0){
			subitem.setVisibility(View.VISIBLE);
			boolean bConflict = false;
			for(BarrierItem bit : entry.GetProblems()){
				if(bit.IsProblem()){
					bConflict = true;
					break;
				}
			}
			
			if(entry.GetId() == -1){
				subitem.setText(mContext.getString(R.string.sSummaryClick));
			}
			else{
				subitem.setText(mContext.getString(R.string.sBarriersExist));
			}			
			
			if(bConflict){
				// + " " + mContext.getString(R.string.sBarriersConflict));
				subitem.setTextColor(Color.RED);
			}
			else
				subitem.setTextColor(mContext.getResources().getColor(R.color.bkColorStrongDark));
		}
		else{
			subitem.setVisibility(View.GONE);
		}
		
		ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
		// set data to display
		String sRouteDataPath = MainActivity.GetGraph().GetCurrentRouteDataPath();

//	    File imgFile = new File(sRouteDataPath + "/icons", "" + entry.GetLine() + "" + entry.GetType() + ".png");
//		Log.d(TAG, imgFile.getPath());

        Bitmap myBitmap = MainActivity.getBitmapFromSVG(mContext, entry, false);

        int padding = 0;

        if (entry.GetType() == 6 || entry.GetType() == 7) {
            float value = 10 * mContext.getResources().getDisplayMetrics().density;
            padding = (int) value;
        }

        ivIcon.setPadding(padding, padding, padding, padding);

//        if(imgFile.exists()){
        if(myBitmap != null){
//		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ivIcon.setImageBitmap(myBitmap);
		    ivIcon.setVisibility(View.VISIBLE);
		}
		else{
			ivIcon.setVisibility(View.INVISIBLE);
		}
		
		ImageButton showSchemaButton = (ImageButton) convertView.findViewById(R.id.show_sheme);
		showSchemaButton.setFocusable(false);
        ImageButton showMapButton = (ImageButton) convertView.findViewById(R.id.show_map);
        showMapButton.setFocusable(false);

        final File schemaFile = new File(sRouteDataPath + "/schemes", "" + entry.GetNode() + ".png");
        int portalId = 0;
        boolean crossButton = false;

        if (mDeparturePortalId != 0 && entry.GetId() == mDepartureStationId)
            portalId = mDeparturePortalId;

        if (mArrivalPortalId != 0 && entry.GetId() == mArrivalStationId)
            portalId = mArrivalPortalId;

        final boolean IsIn = portalId == mDeparturePortalId;
        final int pid = portalId;

        final Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_STATIONID_KEY, entry.GetId());
        bundle.putInt(BUNDLE_PORTALID_KEY, pid);
        bundle.putString(PARAM_SCHEME_PATH, schemaFile.getPath());
        bundle.putBoolean(PARAM_PORTAL_DIRECTION, IsIn);
        bundle.putBoolean(PARAM_ACTIVITY_FOR_RESULT, false);

        if (portalId != 0){
            crossButton = true;

            showMapButton.setOnClickListener(new OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        ((Analytics) ((Activity) mContext).getApplication()).addEvent(Analytics.SCREEN_ROUTING, Analytics.BTN_MAP, Analytics.ACTION_ITEM);

                        Intent intent = new Intent(mContext, StationMapActivity.class);
                        intent.putExtras(bundle);
                        intent.putExtra(PARAM_ROOT_ACTIVITY, true);

                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Call failed", e);
                    }
                }

            });
            showMapButton.setVisibility(View.VISIBLE);
        } else
            showMapButton.setVisibility(View.GONE);

        final boolean root = crossButton;

        if (entry.GetType() == 6 || entry.GetType() == 7 || entry.GetType() == 0)
            showSchemaButton.setVisibility(View.GONE);
        else {
            showSchemaButton.setVisibility(View.VISIBLE);
            showSchemaButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    try {
                        Log.d(TAG, schemaFile.getPath());
                        ((Analytics) ((Activity) mContext).getApplication()).addEvent(Analytics.SCREEN_ROUTING, Analytics.BTN_LAYOUT, Analytics.ACTION_ITEM);

                        Intent intent = new Intent(mContext, com.nextgis.metroaccess.StationImageView.class);
                        intent.putExtras(bundle);

                        if (root)
                            intent.putExtra(PARAM_ROOT_ACTIVITY, true);

                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Call failed", e);
                    }
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
		return false;
	}

    public void setDepartureArrivalPortals(int dep, int arr) {
        mDeparturePortalId = dep;
        mArrivalPortalId = arr;
    }

    public void setDepartureArrivalStations(int dep, int arr) {
        mDepartureStationId = dep;
        mArrivalStationId = arr;
    }

}
