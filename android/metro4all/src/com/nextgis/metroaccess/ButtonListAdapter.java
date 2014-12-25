/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Author:   Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2014 NextGIS
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import java.io.File;

import static com.nextgis.metroaccess.Constants.BUNDLE_ENTRANCE_KEY;
import static com.nextgis.metroaccess.Constants.PARAM_PORTAL_DIRECTION;
import static com.nextgis.metroaccess.Constants.PARAM_ROOT_ACTIVITY;
import static com.nextgis.metroaccess.Constants.PARAM_SCHEME_PATH;
import static com.nextgis.metroaccess.Constants.PARAM_SEL_PORTAL_ID;
import static com.nextgis.metroaccess.Constants.PARAM_SEL_STATION_ID;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_MAIN_FROM_RESULT;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_MAIN_TO_RESULT;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_RESULT;

public class ButtonListAdapter extends BaseAdapter {

	protected Context m_oContext;
	protected LayoutInflater m_oInfalInflater;
    protected StationItem nullStation, fromStation, toStation;
    protected PortalItem fromPortal, toPortal;
//	protected int m_sFromStationLine;
//    protected int m_sToStationLine;
//	protected String m_sFromStationName;
//	protected String m_sFromEntranceName;
//	protected String m_sToStationName;
//	protected String m_sToEntranceName;
//    protected String sNotSet;
    protected ImageButton ibtnLocateFrom;
	protected View.OnClickListener ibtnLocateFromListener;

	public ButtonListAdapter(Context c) {
		this.m_oContext = c;
		this.m_oInfalInflater = (LayoutInflater) m_oContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		sNotSet = (String) m_oContext.getResources().getText(R.string.sNotSet);
        fromStation = toStation = nullStation = new StationItem(-1, m_oContext.getString(R.string.sStationName) + ": " + m_oContext.getString(R.string.sNotSet), -1, -1, -1, -1, -1, -1);
        nullPortals(true, true);
//		this.m_sFromStationName = sNotSet;
//		this.m_sToStationName = sNotSet;
//		this.m_sFromEntranceName = sNotSet;
//		this.m_sToEntranceName = sNotSet;
	}
	
	@Override
	public int getCount() {
		return 3;//TODO: add "set my conditions" and "set from map"
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch(position){
		case 0://create from pane
			return CreateFromPane(convertView);
		case 1://create to pane
			return CreateToPane(convertView);
		case 2://create from map pane
			return CreateAdds(convertView, (String)m_oContext.getResources().getText(R.string.sLimits));
		case 3://create conditions pane
			break;
		}
		return null;
	}
	
	protected View CreateFromPane(View convertView){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.fromto_layout, null);
		}

        ImageView ibtnMap = (ImageView) convertView.findViewById(R.id.ibtnMap);

        if (ibtnLocateFrom == null) {   // add "locate me" button
            ibtnLocateFrom = new ImageButton(convertView.getContext());
            ibtnLocateFrom.setImageResource(R.drawable.ic_action_location_found);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            ibtnLocateFrom.setLayoutParams(lp);
            ibtnLocateFrom.setPadding(ibtnMap.getPaddingLeft(), ibtnMap.getPaddingTop(), ibtnMap.getPaddingRight(), ibtnMap.getPaddingBottom());
            ibtnLocateFrom.setBackgroundResource(0);
            ibtnLocateFrom.setFocusable(false);
            ibtnLocateFrom.setOnClickListener(ibtnLocateFromListener);
            ((LinearLayout) convertView.findViewById(R.id.llPaneButtons)).addView(ibtnLocateFrom, 0);
        }

        if (fromStation != nullStation) {
            ibtnMap.setVisibility(View.VISIBLE);
            ibtnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM_SEL_STATION_ID, fromStation.GetId());
                    bundle.putInt(PARAM_SEL_PORTAL_ID, fromPortal.GetId());
                    bundle.putBoolean(PARAM_PORTAL_DIRECTION, true);
                    Intent intent = new Intent(m_oContext, StationMapActivity.class);
                    intent.putExtras(bundle);

                    Activity parent = (Activity) m_oContext;
                    parent.startActivityForResult(intent, PORTAL_MAP_MAIN_FROM_RESULT);
                }
            });
        } else
            ibtnMap.setVisibility(View.GONE);

		ImageView ivMarkIcon = (ImageView)convertView.findViewById(R.id.ivMarkIcon);
		ivMarkIcon.setImageResource(R.drawable.ic_geomarker_a);

        ImageView ivSmallIcon = (ImageView)convertView.findViewById(R.id.ivSmallIcon);

        File imgFile = new File(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/icons", fromStation.GetLine() + "5.png");
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            myBitmap = Bitmap.createBitmap(myBitmap, myBitmap.getWidth()/4, 0, myBitmap.getWidth()/2, myBitmap.getHeight(), matrix, true);

            ivSmallIcon.setImageBitmap(myBitmap);
            ivSmallIcon.setVisibility(View.VISIBLE);
        } else
            ivSmallIcon.setVisibility(View.GONE);

		ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
//		ivIcon.setImageResource(R.drawable.ic_geomarker_a);
        ivIcon.setVisibility(View.GONE);
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(R.string.sFromStation);

		String sStationName = fromStation.GetName();
		TextView tvStationName = (TextView)convertView.findViewById(R.id.tvStationName);
		tvStationName.setText(sStationName);
		
		String sEntranceName = fromPortal.GetName();
		TextView tvEntranceName = (TextView)convertView.findViewById(R.id.tvEntranceName);
		tvEntranceName.setText(sEntranceName);
		
		return convertView;
	}

    protected View CreateToPane(View convertView){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.fromto_layout, null);
		}

        ImageView ibtnMap = (ImageView) convertView.findViewById(R.id.ibtnMap);

        if (toStation != nullStation) {
            ibtnMap.setVisibility(View.VISIBLE);
            ibtnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM_SEL_STATION_ID, toStation.GetId());
                    bundle.putInt(PARAM_SEL_PORTAL_ID, toPortal.GetId());
                    bundle.putBoolean(PARAM_PORTAL_DIRECTION, false);
                    Intent intent = new Intent(m_oContext, StationMapActivity.class);
                    intent.putExtras(bundle);

                    Activity parent = (Activity) m_oContext;
                    parent.startActivityForResult(intent, PORTAL_MAP_MAIN_TO_RESULT);
                }
            });
        } else
            ibtnMap.setVisibility(View.GONE);

        ImageView ivMarkIcon = (ImageView)convertView.findViewById(R.id.ivMarkIcon);
        ivMarkIcon.setImageResource(R.drawable.ic_geomarker_b);

        ImageView ivSmallIcon = (ImageView)convertView.findViewById(R.id.ivSmallIcon);

        File imgFile = new File(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/icons", toStation.GetLine() + "5.png");
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            myBitmap = Bitmap.createBitmap(myBitmap, myBitmap.getWidth()/4, 0, myBitmap.getWidth()/2, myBitmap.getHeight(), matrix, true);

            ivSmallIcon.setImageBitmap(myBitmap);
            ivSmallIcon.setVisibility(View.VISIBLE);
        } else
            ivSmallIcon.setVisibility(View.GONE);

        ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
//		ivIcon.setImageResource(R.drawable.ic_geomarker_b);
        ivIcon.setVisibility(View.GONE);
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(R.string.sToStation);

        String sStationName = toStation.GetName();
		TextView tvStationName = (TextView)convertView.findViewById(R.id.tvStationName);
		tvStationName.setText(sStationName);
		
		String sExitName = toPortal.GetName();
		TextView tvExitName = (TextView)convertView.findViewById(R.id.tvEntranceName);
		tvExitName.setText(sExitName);

		return convertView;
	}	
	
	protected View CreateAdds(View convertView, String sText){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.additional_layout, null);
		}
		
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(sText);		

		return convertView;
	}

    public void setFromStation(StationItem fromStation) {
        if (fromStation != null)
            this.fromStation = fromStation;
        else
            this.fromStation = nullStation;
    }

    public void setToStation(StationItem toStation) {
        if (toStation != null)
            this.toStation = toStation;
        else
            this.toStation = nullStation;
    }

    public void setFromPortal(int portalId) {
        if (fromStation != nullStation)
            fromPortal = fromStation.GetPortal(portalId);
        else
            nullPortals(true, false);
    }

    public void setToPortal(int portalId) {
        if (toStation != nullStation)
            toPortal = toStation.GetPortal(portalId);
        else
            nullPortals(false, true);
    }

//    public void setFromStationLine(int sFromStationLine) {
//        this.m_sFromStationLine = sFromStationLine;
//    }

//	public String getFromStationName() {
//		return m_sFromStationName;
//	}

//	public void setFromStationName(String sFromStationName) {
//		this.m_sFromStationName = sFromStationName;
//	}

//	public String getFromEntranceName() {
//		return m_sFromEntranceName;
//	}

//	public void setFromEntranceName(String sFromEntranceName) {
//		this.m_sFromEntranceName = sFromEntranceName;
//	}

//    public void setToStationLine(int sToStationLine) {
//        this.m_sToStationLine = sToStationLine;
//    }

//	public String getToStationName() {
//		return m_sToStationName;
//	}

//	public void setToStationName(String sToStationName) {
//		this.m_sToStationName = sToStationName;
//	}

//	public String getToEntranceName() {
//		return m_sToEntranceName;
//	}

//	public void setToEntranceName(String sToEntranceName) {
//		this.m_sToEntranceName = sToEntranceName;
//	}

    public void clear(){
        fromStation = toStation = nullStation;
        nullPortals(true, true);
//        this.m_sFromStationName = sNotSet;
//        this.m_sToStationName = sNotSet;
//        this.m_sFromEntranceName = sNotSet;
//        this.m_sToEntranceName = sNotSet;
//        this.m_sFromStationLine = -1;
//        this.m_sToStationLine = -1;

        notifyDataSetChanged();
    }

    private void nullPortals(boolean from, boolean to) {
        if (from)
            fromPortal = new PortalItem(-1, m_oContext.getString(R.string.sEntranceName) + ": " + m_oContext.getString(R.string.sNotSet), -1, -1, null, -1, -1);

        if (to)
            toPortal = new PortalItem(-1, m_oContext.getString(R.string.sExitName) + ": " + m_oContext.getString(R.string.sNotSet), -1, -1, null, -1, -1);
    }

    public void setOnLocateFromListener(View.OnClickListener listener) {
        ibtnLocateFromListener = listener;
    }
}
