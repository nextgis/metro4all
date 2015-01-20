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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nextgis.metroaccess.data.StationItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.nextgis.metroaccess.Constants.ARRIVAL_RESULT;
import static com.nextgis.metroaccess.Constants.BUNDLE_CITY_CHANGED;
import static com.nextgis.metroaccess.Constants.BUNDLE_ENTRANCE_KEY;
import static com.nextgis.metroaccess.Constants.BUNDLE_EVENTSRC_KEY;
import static com.nextgis.metroaccess.Constants.BUNDLE_PORTALID_KEY;
import static com.nextgis.metroaccess.Constants.BUNDLE_STATIONID_KEY;
import static com.nextgis.metroaccess.Constants.DEPARTURE_RESULT;
import static com.nextgis.metroaccess.Constants.MAX_RECENT_ITEMS;
import static com.nextgis.metroaccess.Constants.MENU_ABOUT;
import static com.nextgis.metroaccess.Constants.MENU_SETTINGS;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_RESULT;
import static com.nextgis.metroaccess.Constants.PREF_RESULT;
import static com.nextgis.metroaccess.Constants.TAG;
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_ARR_STATIONS;
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_DEP_STATIONS;
import static com.nextgis.metroaccess.PreferencesActivity.clearRecent;

public class SelectStationActivity extends SherlockFragmentActivity {
    private static final int NUM_ITEMS = 3;

    private FragmentRollAdapter mAdapter;
    private ViewPager mPager;
    private TextView tvNotes;

    protected static AlphabeticalStationListFragment mAlphaStListFragment;
    protected static LinesStationListFragment mLinesStListFragment;
    protected static RecentStationListFragment mRecentStListFragment;

    protected boolean m_bIn, isCityChanged = false;
    private int mStationId, mPortalId;
    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_station);
        resultIntent = new Intent();

        // setup action bar for tabs
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAdapter = new FragmentRollAdapter(getSupportFragmentManager());
        mAdapter.setActionBar(actionBar);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int arg0) {
                Log.d(TAG, "onPageSelected: " + arg0);
                actionBar.getTabAt(arg0).select();
            }
        });

        Tab tab = actionBar.newTab()
                .setText(R.string.sSelAlphabeticalTab)
                .setTabListener(new TabListener<SherlockFragment>(0 + "", mPager));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.sSelLinesTab)
                .setTabListener(new TabListener<SherlockFragment>(1 + "", mPager));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.sSelRecentTab)
                .setTabListener(new TabListener<SherlockFragment>(2 + "", mPager));
        actionBar.addTab(tab);

        //get location from calling class
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int nType = extras.getInt(BUNDLE_EVENTSRC_KEY);
            m_bIn = extras.getBoolean(BUNDLE_ENTRANCE_KEY);
            mStationId = extras.getInt(BUNDLE_STATIONID_KEY);
            mPortalId = extras.getInt(BUNDLE_PORTALID_KEY);

            Tracker t = ((Analytics) getApplication()).getTracker();
            t.setScreenName(Analytics.SCREEN_SELECT_STATION + " " + getDirection());
            t.send(new HitBuilders.AppViewBuilder().build());

            switch (nType) {
                case DEPARTURE_RESULT:
                    setTitle(R.string.sFromStation);
                    break;
                case ARRIVAL_RESULT:
                    setTitle(R.string.sToStation);
                    break;
            }
        }

        tvNotes = (TextView) findViewById(R.id.tvNotes);

        // http://stackoverflow.com/a/9108219
        final int softKeyboardHeight = getResources().getDisplayMetrics().heightPixels / 5;
        final View activityRootView = findViewById(R.id.select_station_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!HasLimits()) return;

                        Rect r = new Rect();
                        //r will be populated with the coordinates of your view
                        // that area still visible.
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = activityRootView.getRootView().getHeight() - r.height();

                        // if more than 1/5 of display, its probably a keyboard...
                        if (heightDiff > softKeyboardHeight)
                            tvNotes.setVisibility(View.GONE);
                        else
                            tvNotes.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNotes.setVisibility(HasLimits() ? View.VISIBLE : View.GONE);
    }

    public boolean IsIn() {
        return m_bIn;
    }

    public List<StationItem> GetStationList() {
        return new ArrayList<StationItem>(MainActivity.GetGraph().GetStations().values());
    }

    public static class TabListener<T extends SherlockFragment> implements ActionBar.TabListener {
        private final String m_Tag;
        private ViewPager m_Pager;

        public TabListener(String tag, ViewPager pager) {
            m_Tag = tag;
            m_Pager = pager;
        }


        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            int nTag = Integer.parseInt(m_Tag);
            m_Pager.setCurrentItem(nTag);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }
    }

    public static class FragmentRollAdapter extends FragmentPagerAdapter {
        ActionBar m_ActionBar;

        public FragmentRollAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        public void setActionBar(ActionBar bar) {
            m_ActionBar = bar;
        }

        @Override
        public SherlockFragment getItem(int arg0) {
            switch (arg0) {
                case 0:
                    mAlphaStListFragment = new AlphabeticalStationListFragment();
                    return (SherlockFragment) mAlphaStListFragment;//
                case 1:
                    mLinesStListFragment = new LinesStationListFragment();
                    return (SherlockFragment) mLinesStListFragment;//
                case 2:
                    mRecentStListFragment = new RecentStationListFragment();
                    return (SherlockFragment) mRecentStListFragment;//
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getSupportMenuInflater().inflate(R.menu.main, menu);
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.sSettings)
                .setIcon(R.drawable.ic_action_settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, R.string.sAbout)
                .setIcon(R.drawable.ic_action_about)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.BACK, getTab());

                finish();
                return true;
            case MENU_SETTINGS:
                // app icon in action bar clicked; go home
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.MENU_SETTINGS, getTab());
                onSettings();
                return true;
            case MENU_ABOUT:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.MENU_ABOUT, getTab());
                Intent intentAbout = new Intent(this, AboutActivity.class);
                intentAbout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAbout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.BACK, getTab());

        super.onBackPressed();
    }

    private String getDirection() {
        return m_bIn ? Analytics.FROM : Analytics.TO;
    }

    private String getTab() {
        int i = getSupportActionBar().getSelectedTab().getPosition();
        return i == 0 ? Analytics.TAB_AZ : i == 1 ? Analytics.TAB_LINES : Analytics.TAB_RECENT;
    }

    public void Finish(int nStationId, int nPortalId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        JSONArray stationsIds = getRecentStations(prefs, IsIn());
        String direction = IsIn() ? KEY_PREF_RECENT_DEP_STATIONS : KEY_PREF_RECENT_ARR_STATIONS;
        int index = indexOf(stationsIds, nStationId);

        if (index == -1 && stationsIds.length() == MAX_RECENT_ITEMS) {
            stationsIds = remove(stationsIds, 0);
            stationsIds.put(nStationId);
        }

        if (index == -1 && stationsIds.length() < MAX_RECENT_ITEMS)
            stationsIds.put(nStationId);

        if (index != -1) {
            stationsIds = remove(stationsIds, index);
            stationsIds.put(nStationId);
        }

        prefs.edit().putString(direction, stationsIds.toString()).apply();

        mStationId = nStationId;
        mPortalId = nPortalId;
        finish();
    }

    @Override
    public void finish() {
        resultIntent.putExtra(BUNDLE_STATIONID_KEY, mStationId);
        resultIntent.putExtra(BUNDLE_PORTALID_KEY, mPortalId);
        resultIntent.putExtra(BUNDLE_CITY_CHANGED, isCityChanged);
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //update fragments to new data
        switch (requestCode) {
            case PREF_RESULT:
                if (data != null) {
                    isCityChanged = isCityChanged ? isCityChanged : data.getBooleanExtra(BUNDLE_CITY_CHANGED, false);

                    if (isCityChanged) {
                        clearRecent(PreferenceManager.getDefaultSharedPreferences(this));
                        mStationId = -1;
                        mPortalId = -1;
                    }
                }

                if (mAlphaStListFragment != null)
                    mAlphaStListFragment.Update();
                if (mLinesStListFragment != null)
                    mLinesStListFragment.Update();
                if (mRecentStListFragment != null)
                    mRecentStListFragment.Update();
                break;
            case PORTAL_MAP_RESULT:
                if (resultCode == RESULT_OK) {
                    ((Analytics) getApplication()).addEvent(Analytics.SCREEN_MAP + " " + getDirection(), Analytics.PORTAL, Analytics.SCREEN_MAP);

                    int stationID = data.getIntExtra(BUNDLE_STATIONID_KEY, 0);
                    int portalID = data.getIntExtra(BUNDLE_PORTALID_KEY, 0);
                    Finish(stationID, portalID);
                }
                break;
            default:
                break;
        }
    }

    protected void onSettings() {
        Intent intentSet = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intentSet, PREF_RESULT);
    }

    public boolean HasLimits() {
        return LimitationsActivity.hasLimitations(this);
    }

    public static JSONArray getRecentStations(SharedPreferences prefs, boolean isDeparture) {
        String direction = isDeparture ? KEY_PREF_RECENT_DEP_STATIONS : KEY_PREF_RECENT_ARR_STATIONS;
        JSONArray stationIds = new JSONArray();

        try {
            stationIds = new JSONArray(prefs.getString(direction, "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stationIds;
    }

    public static int indexOf(JSONArray array, int item) {
        for (int i = 0; i < array.length(); i++) {
            try {
                if (item == array.get(i))
                    return i;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public static JSONArray remove(JSONArray array, int index) {
        JSONArray replacedIds = new JSONArray();

        for (int i = 0; i < array.length(); i++)
            if (i != index)
                try {
                    replacedIds.put(array.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        return replacedIds;
    }
}
