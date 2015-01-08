/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
 *   Copyright (C) 2014,2015 NextGIS
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

import static com.nextgis.metroaccess.Constants.PARAM_PORTAL_DIRECTION;
import static com.nextgis.metroaccess.Constants.PARAM_ROOT_ACTIVITY;
import static com.nextgis.metroaccess.Constants.PARAM_SCHEME_PATH;
import static com.nextgis.metroaccess.Constants.PARAM_SEL_PORTAL_ID;
import static com.nextgis.metroaccess.Constants.PARAM_SEL_STATION_ID;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_MAIN_FROM_RESULT;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_MAIN_TO_RESULT;
import static com.nextgis.metroaccess.MainActivity.getBitmapFromSVG;

public class ButtonListAdapter extends BaseAdapter {

    protected Context m_oContext;
    protected LayoutInflater m_oInfalInflater;
    protected StationItem nullStation, fromStation, toStation;
    protected PortalItem fromPortal, toPortal;

    protected ImageButton ibtnLocateFrom;
    protected View.OnClickListener ibtnLocateFromListener;

    public ButtonListAdapter(Context c) {
        this.m_oContext = c;
        this.m_oInfalInflater = (LayoutInflater) m_oContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fromStation = toStation = nullStation = new StationItem(-1, m_oContext.getString(R.string.sStationName) + ": " + m_oContext.getString(R.string.sNotSet), -1, -1, -1, -1, -1, -1);
        nullPortals(true, true);
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
        switch (position) {
            case 0://create from pane
                return CreateFromPane(convertView);
            case 1://create to pane
                return CreateToPane(convertView);
            case 2://create from map pane
                return CreateAdds(convertView, (String) m_oContext.getResources().getText(R.string.sLimits));
            case 3://create conditions pane
                break;
        }
        return null;
    }

    protected View CreateFromPane(View convertView) {
        convertView = CreatePane(convertView, true, fromStation, fromPortal);

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

        return convertView;
    }

    protected View CreateToPane(View convertView) {
        return CreatePane(convertView, false, toStation, toPortal);
    }

    private View CreatePane(View convertView, final boolean isFromPane, final StationItem station, final PortalItem portal) {
        if (convertView == null) {
            convertView = m_oInfalInflater.inflate(R.layout.fromto_layout, null);
        }

        final int paneTitle, requestCode;
        final String gaPane;

        if (isFromPane) {
            paneTitle = R.string.sFromStation;
            gaPane = Analytics.FROM;
            requestCode = PORTAL_MAP_MAIN_FROM_RESULT;
        } else {
            paneTitle = R.string.sToStation;
            gaPane = Analytics.TO;
            requestCode = PORTAL_MAP_MAIN_TO_RESULT;
        }

        // set map button
        ImageView ibtnMap = (ImageView) convertView.findViewById(R.id.ibtnMap);
        ImageView ivMetroIconLeft = (ImageView) convertView.findViewById(R.id.ivMetroIconLeft);
        ImageView ivMetroIconRight = (ImageView) convertView.findViewById(R.id.ivMetroIconRight);
        ImageView ivSmallIcon = (ImageView) convertView.findViewById(R.id.ivSmallIcon);

        if (station != nullStation) {
            ibtnMap.setVisibility(View.VISIBLE);
            ibtnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Analytics) ((Activity) m_oContext).getApplication()).addEvent(Analytics.SCREEN_MAIN, Analytics.BTN_MAP, gaPane + " " + Analytics.PANE);

                    File schemaFile = new File(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/schemes", "" + station.GetNode() + ".png");
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM_SEL_STATION_ID, station.GetId());
                    bundle.putInt(PARAM_SEL_PORTAL_ID, portal.GetId());
                    bundle.putBoolean(PARAM_PORTAL_DIRECTION, isFromPane);
                    bundle.putBoolean(PARAM_ROOT_ACTIVITY, true);
                    bundle.putString(PARAM_SCHEME_PATH, schemaFile.getPath());
                    Intent intent = new Intent(m_oContext, StationMapActivity.class);
                    intent.putExtras(bundle);

                    Activity parent = (Activity) m_oContext;
                    parent.startActivityForResult(intent, requestCode);
                }
            });

            // set selected line icon, entrance metro icon and arrow icon
            Bitmap metroIcon = getBitmapFromSVG(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/icons/metro.svg");
            Bitmap arrowIcon = getBitmapFromSVG(m_oContext, R.raw.arrow);
            Bitmap lineIcon = getBitmapFromSVG(m_oContext, station);

            if (isFromPane) {   // from pane > rotate arrow 180 degree
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                arrowIcon = Bitmap.createBitmap(arrowIcon, 0, 0, arrowIcon.getWidth(), arrowIcon.getHeight(), matrix, true);
            }

            setImageToImageView(ivMetroIconLeft, metroIcon);
            setImageToImageView(ivMetroIconRight, arrowIcon);
            setImageToImageView(ivSmallIcon, lineIcon);
        } else {    // hide all icons if statiton is not selected
            ibtnMap.setVisibility(View.GONE);
            ivMetroIconLeft.setVisibility(View.GONE);
            ivMetroIconRight.setVisibility(View.GONE);
            ivSmallIcon.setVisibility(View.GONE);
        }

//        ImageView ivMarkIcon = (ImageView)convertView.findViewById(R.id.ivMarkIcon);
//        ivMarkIcon.setImageResource(R.drawable.ic_geomarker_a);   // _b

        // hide big icon
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
        ivIcon.setVisibility(View.GONE);

        // set texts
        TextView tvPaneName = (TextView) convertView.findViewById(R.id.tvPaneName);
        tvPaneName.setText(paneTitle);

        String sStationName = station.GetName();
        TextView tvStationName = (TextView) convertView.findViewById(R.id.tvStationName);
        tvStationName.setText(sStationName);

        String sEntranceName = portal.GetName();
        TextView tvEntranceName = (TextView) convertView.findViewById(R.id.tvEntranceName);
        tvEntranceName.setText(sEntranceName);

        return convertView;
    }

    private void setImageToImageView(ImageView view, Bitmap bitmap) {
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            view.setVisibility(View.VISIBLE);
        } else
            view.setVisibility(View.GONE);
    }

    protected View CreateAdds(View convertView, String sText) {
        if (convertView == null) {
            convertView = m_oInfalInflater.inflate(R.layout.additional_layout, null);
        }

        TextView tvPaneName = (TextView) convertView.findViewById(R.id.tvPaneName);
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

    public void clear() {
        fromStation = toStation = nullStation;
        nullPortals(true, true);

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
