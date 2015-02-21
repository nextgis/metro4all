/*******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
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
 *******************************************************************************/

package com.nextgis.metroaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.nextgis.metroaccess.Constants.BUNDLE_STATIONID_KEY;
import static com.nextgis.metroaccess.Constants.PARAM_PORTAL_DIRECTION;
import static com.nextgis.metroaccess.Constants.PARAM_ROOT_ACTIVITY;
import static com.nextgis.metroaccess.Constants.PARAM_SCHEME_PATH;
import static com.nextgis.metroaccess.Constants.SUBSCREEN_PORTAL_RESULT;
import static com.nextgis.metroaccess.StationImageView.hideHint;

public abstract class StationExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {
    protected Context mContext;
    protected List<StationItem> mStationList;

    protected int mnType;
    protected int mnMaxWidth, mnWheelWidth;
    protected boolean m_bHaveLimits;

    protected LayoutInflater mInfalInflater;
    protected List<StationItem> moOriginalStationList;

    protected boolean m_bIn;
    private boolean mIsHintNotShowed;
    private String mHintScreenName;

    public StationExpandableListAdapter(Context c) {
        mContext = c;
        mStationList = new ArrayList<>();
        mInfalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        loadPreferences();

        SelectStationActivity act = (SelectStationActivity) mContext;
        m_bIn = act.IsIn();
        mIsHintNotShowed = act.isHintNotShowed();
        mHintScreenName = act.getHintScreenName();
    }

    abstract void onInit();

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        StationItem sit = mStationList.get(groupPosition);

        if (sit != null) {
            List<PortalItem> lpit = sit.GetPortals(m_bIn);

            if (lpit != null)
                return lpit.get(childPosition);
        }

        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        PortalItem pit = (PortalItem) getChild(groupPosition, childPosition);

        if (pit != null)
            return pit.GetId();

        return -1;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        PortalItem entry = (PortalItem) getChild(groupPosition, childPosition);
        return getChildView(convertView, entry, parent);
    }

    protected View getChildView(View convertView, PortalItem entry, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInfalInflater.inflate(R.layout.select_portal_row_layout, parent, false);
        }

        TextView item = (TextView) convertView.findViewById(R.id.txPortalName);
        TextView meetcode = (TextView) convertView.findViewById(R.id.tvPortalMeetCode);

        if (mnType > 1) {
            boolean bSmallWidth = entry.GetDetailes()[0] < mnMaxWidth;
            boolean bCanRoll = entry.GetDetailes()[7] == 0 || entry.GetDetailes()[5] <= mnWheelWidth
                    && (entry.GetDetailes()[6] == 0 || mnWheelWidth <= entry.GetDetailes()[6]);

            if (m_bHaveLimits && (bSmallWidth || !bCanRoll)) {
                item.setTextColor(Color.RED);
            } else {
                TypedValue tv = new TypedValue();
                mContext.getTheme().resolveAttribute(android.R.attr.textColorSecondary, tv, true);
                item.setTextColor(mContext.getResources().getColor(tv.resourceId));
            }
        }

        item.setText(entry.GetName());
        meetcode.setText(entry.GetReadableMeetCode());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        StationItem sit = mStationList.get(groupPosition);

        if (sit != null) {
            List<PortalItem> ls = sit.GetPortals(m_bIn);

            if (ls == null)
                return 0;

            return ls.size();
        }

        return 0;
    }

    @Override
    public StationIndexedExpandableListAdapter.IndexedListItem getGroup(int groupPosition) {
        return mStationList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mStationList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        StationItem sit = mStationList.get(groupPosition);

        if (sit != null)
            return sit.GetId();

        return -1;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        final StationItem entry = (StationItem) getGroup(groupPosition);
        return getGroupView(convertView, entry, parent);
    }

    protected View getGroupView(View convertView, StationItem entry, final ViewGroup parent) {
        if (convertView == null || convertView.findViewById(R.id.tvStationName) == null) {
            convertView = mInfalInflater.inflate(R.layout.select_station_row_layout, parent, false);
        }

        final TextView item = (TextView) convertView.findViewById(R.id.tvStationName);
        item.setText(entry.GetName());

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);

        String sRouteDataPath = MainActivity.GetGraph().GetCurrentRouteDataPath();

        String color = MainActivity.GetGraph().GetLineColor(entry.GetLine());
        Bitmap myBitmap = MainActivity.getBitmapFromSVG(mContext, R.raw._0, color);
        ivIcon.setImageBitmap(myBitmap);

        final ImageButton ibtnMenu = (ImageButton) convertView.findViewById(R.id.ibtnMenu);
        final File schemaFile = new File(sRouteDataPath + "/schemes", "" + entry.GetNode() + ".png");
        final SelectStationActivity parentActivity = (SelectStationActivity) ibtnMenu.getContext();

        final Bundle bundle = new Bundle();
        bundle.putString(PARAM_SCHEME_PATH, schemaFile.getPath());
        bundle.putBoolean(PARAM_ROOT_ACTIVITY, true);
        bundle.putBoolean(PARAM_PORTAL_DIRECTION, parentActivity.IsIn());
        bundle.putInt(BUNDLE_STATIONID_KEY, entry.GetId());

        int i = parentActivity.getSupportActionBar().getSelectedTab().getPosition();
        final String gaParent = i == 0 ? Analytics.TAB_AZ : i == 1 ? Analytics.TAB_LINES : Analytics.TAB_RECENT;
        final String direction = parentActivity.IsIn() ? Analytics.FROM : Analytics.TO;

        ibtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    if (mIsHintNotShowed) {
                        hideHint(mContext, mHintScreenName);
                        view.getRootView().getRootView().findViewById(R.id.ttSelectStation).setVisibility(View.GONE);
                    }

//                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = mInflater.inflate(R.layout.stationlist_popup_menu, parent, false);

                    final TextView itemMap = (TextView) layout.findViewById(R.id.tvMap);
                    final TextView itemLayout = (TextView) layout.findViewById(R.id.tvLayout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    // fix for items full-width background fill
                    if (itemMap.getLayoutParams().width > itemLayout.getLayoutParams().width)
                        itemLayout.setLayoutParams(lp);
                    else
                        itemMap.setLayoutParams(lp);

                    layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                    final PopupWindow mDropdown = new PopupWindow(layout, itemLayout.getMeasuredWidth(),
                            itemMap.getMeasuredHeight() + itemLayout.getMeasuredHeight(), true);
                    mDropdown.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    Drawable background = mContext.getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult);
                    mDropdown.setBackgroundDrawable(background);
                    mDropdown.setClippingEnabled(false);

                    itemMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Analytics) ((Activity) mContext).getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + direction, Analytics.BTN_MAP, gaParent);

                            mDropdown.dismiss();
                            Intent intent = new Intent(parentActivity, StationMapActivity.class);
                            intent.putExtras(bundle);
                            parentActivity.startActivityForResult(intent, SUBSCREEN_PORTAL_RESULT);
                        }
                    });

                    itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Analytics) ((Activity) mContext).getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + direction, Analytics.BTN_LAYOUT, gaParent);

                            mDropdown.dismiss();
                            Intent intentView = new Intent(parentActivity, StationImageView.class);
                            intentView.putExtras(bundle);
                            parentActivity.startActivityForResult(intentView, SUBSCREEN_PORTAL_RESULT);
                        }
                    });

//                    mDropdown.showAsDropDown(view, -itemLayout.getMeasuredWidth() / 2, -10);
                    SelectStationActivity parentActivity = (SelectStationActivity) mContext;

                    int[] loc = new int[2];
                    view.getLocationOnScreen(loc);
                    int x = loc[0] - mDropdown.getWidth() / 2 - mDropdown.getWidth() / 4;
                    int y = loc[1] + ibtnMenu.getMeasuredHeight() / 2;

                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;

                    if(parentActivity.isKeyboardShown())
                        height -= parentActivity.getKeyboardHeight();

                    if (y + mDropdown.getHeight() > height)
                        y = y - mDropdown.getHeight() - ibtnMenu.getMeasuredHeight() / 2;

                    mDropdown.showAtLocation(view.getRootView(), Gravity.NO_GRAVITY, x, y);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
                final ArrayList<StationItem> results = new ArrayList<>();

                if (constraint != null) {
                    if (moOriginalStationList == null)
                        moOriginalStationList = mStationList;

                    if (moOriginalStationList != null && moOriginalStationList.size() > 0) {
                        for (final StationItem station : moOriginalStationList) {
                            String stationName = station.GetName().toLowerCase().replace("ё", "е");
                            String constr = constraint.toString().toLowerCase().replace("ё", "е");

                            if (stationName.contains(constr))
                                results.add(station);
                        }
                    }

                    oReturn.values = results;
                }

                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.values != null) {
                    mStationList = (ArrayList<StationItem>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    public void Update() {
        loadPreferences();
        moOriginalStationList = null;
        onInit();
    }

    private void loadPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 2);
        mnMaxWidth = LimitationsActivity.getMaxWidth(mContext);
        mnWheelWidth = LimitationsActivity.getWheelWidth(mContext);
        m_bHaveLimits = LimitationsActivity.hasLimitations(mContext);
    }
}
