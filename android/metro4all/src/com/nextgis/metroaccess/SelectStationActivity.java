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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

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
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_ARR_STATIONS;
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_DEP_STATIONS;
import static com.nextgis.metroaccess.Constants.KEY_PREF_TOOLTIPS;
import static com.nextgis.metroaccess.Constants.MAX_RECENT_ITEMS;
import static com.nextgis.metroaccess.Constants.PREF_RESULT;
import static com.nextgis.metroaccess.Constants.SUBSCREEN_PORTAL_RESULT;
import static com.nextgis.metroaccess.Constants.TAG;
import static com.nextgis.metroaccess.PreferencesActivity.clearRecent;

public class SelectStationActivity extends ActionBarActivity {
    private static final int NUM_ITEMS = 3;

    private TextView tvNotes;

    protected static AlphabeticalStationListFragment mAlphaStListFragment;
    protected static LinesStationListFragment mLinesStListFragment;
    protected static RecentStationListFragment mRecentStListFragment;

    protected boolean m_bIn, isCityChanged = false;
    protected boolean mIsLimitations;
    private int mStationId, mPortalId;
    private Intent resultIntent;

    private SharedPreferences prefs;
    private boolean mIsKeyboardShown = false;
    private int mHeightDifference;

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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mIsLimitations = LimitationsActivity.hasLimitations(this);

        FragmentRollAdapter mAdapter = new FragmentRollAdapter(getSupportFragmentManager());
        mAdapter.setActionBar(actionBar);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
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

        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.sSelAlphabeticalTab)
                .setTabListener(new TabListener(0 + "", mPager));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.sSelLinesTab)
                .setTabListener(new TabListener(1 + "", mPager));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.sSelRecentTab)
                .setTabListener(new TabListener(2 + "", mPager));
        actionBar.addTab(tab);

        //get location from calling class
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int nType = extras.getInt(BUNDLE_EVENTSRC_KEY);
            m_bIn = extras.getBoolean(BUNDLE_ENTRANCE_KEY);
            mStationId = extras.getInt(BUNDLE_STATIONID_KEY);
            mPortalId = extras.getInt(BUNDLE_PORTALID_KEY);
            int selectedStation = -1;

            Tracker t = ((Analytics) getApplication()).getTracker();
            t.setScreenName(Analytics.SCREEN_SELECT_STATION + " " + getDirection());
            t.send(new HitBuilders.AppViewBuilder().build());

            switch (nType) {
                case DEPARTURE_RESULT:
                    setTitle(R.string.sFromStation);
                    selectedStation = prefs.getInt("dep_" + BUNDLE_STATIONID_KEY, -1);
                    break;
                case ARRIVAL_RESULT:
                    setTitle(R.string.sToStation);
                    selectedStation = prefs.getInt("arr_" + BUNDLE_STATIONID_KEY, -1);
                    break;
            }

            if (selectedStation != -1) {
                final int finalSelectedStation = selectedStation;
                mPager.post(new Runnable() {
                    @Override
                    public void run() {
                        mAlphaStListFragment.expandStation(finalSelectedStation);
                    }
                });
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
                        if (!mIsLimitations) return;

                        Rect r = new Rect();
                        //r will be populated with the coordinates of your view
                        // that area still visible.
                        activityRootView.getWindowVisibleDisplayFrame(r);
//                        int heightDiff = activityRootView.getRootView().getHeight() - r.height();
                        mHeightDifference = activityRootView.getRootView().getHeight() - r.height();

                        // if more than 1/5 of display, its probably a keyboard...
                        mIsKeyboardShown = mHeightDifference > softKeyboardHeight;

                        if (mIsKeyboardShown)
                            tvNotes.setVisibility(View.GONE);
                        else
                            tvNotes.setVisibility(View.VISIBLE);
                    }
                });
    }

    public boolean isKeyboardShown() {
        return mIsKeyboardShown;
    }

    public int getKeyboardHeight() {
//        return getResources().getDisplayMetrics().heightPixels / 5;
        return mHeightDifference;
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean was = mIsLimitations;
        mIsLimitations = LimitationsActivity.hasLimitations(this);

        if (was != mIsLimitations) {
            FragmentRollAdapter mAdapter = new FragmentRollAdapter(getSupportFragmentManager());
            mAdapter.setActionBar(getSupportActionBar());
            ViewPager mPager = (ViewPager) findViewById(R.id.pager);

            int current = mPager.getCurrentItem();
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(current);

            tvNotes.setVisibility(mIsLimitations ? View.VISIBLE : View.GONE);
        }
    }

    public boolean IsIn() {
        return m_bIn;
    }

    public boolean isHintNotShowed() {
        return !prefs.getString(KEY_PREF_TOOLTIPS, "").contains(getClass().getSimpleName());
    }

    public String getHintScreenName() {
        return getClass().getSimpleName();
    }

    public List<StationItem> GetStationList() {
        return new ArrayList<>(MainActivity.GetGraph().GetStations().values());
    }

    public static class TabListener implements ActionBar.TabListener {
        private final String m_Tag;
        private ViewPager m_Pager;

        public TabListener(String tag, ViewPager pager) {
            m_Tag = tag;
            m_Pager = pager;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            int nTag = Integer.parseInt(m_Tag);
            m_Pager.setCurrentItem(nTag);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

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
        public SelectStationListFragment getItem(int arg0) {
            switch (arg0) {
                case 0:
                    mAlphaStListFragment = new AlphabeticalStationListFragment();
                    return mAlphaStListFragment;//
                case 1:
                    mLinesStListFragment = new LinesStationListFragment();
                    return mLinesStListFragment;//
                case 2:
                    mRecentStListFragment = new RecentStationListFragment();
                    return mRecentStListFragment;//
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.btn_locate).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.BACK, getTab());
                finish();
                return true;
            case R.id.btn_settings:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.MENU_SETTINGS, getTab());
                onSettings(false);
                return true;
            case R.id.btn_limitations:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_SELECT_STATION + " " + getDirection(), Analytics.LIMITATIONS, getTab());
                onSettings(true);
                return true;
            case R.id.btn_about:
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
            case SUBSCREEN_PORTAL_RESULT:
                if (resultCode == RESULT_OK) {
                    int stationID = data.getIntExtra(BUNDLE_STATIONID_KEY, 0);
                    int portalID = data.getIntExtra(BUNDLE_PORTALID_KEY, 0);
                    Finish(stationID, portalID);
                }
                break;
            default:
                break;
        }
    }

    protected void onSettings(boolean isLimitations) {
        if (isLimitations)
            startActivity(new Intent(this, LimitationsActivity.class));
//            startActivityForResult(new Intent(this, LimitationsActivity.class), PREF_RESULT);
        else {
            Intent intentSet = new Intent(this, PreferencesActivity.class);
            startActivityForResult(intentSet, PREF_RESULT);
        }
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
