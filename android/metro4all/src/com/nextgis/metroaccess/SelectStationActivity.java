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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nextgis.metroaccess.data.StationItem;

public class SelectStationActivity extends SherlockFragmentActivity {
    private static final int NUM_ITEMS = 4;

    private FragmentRollAdapter mAdapter;
    private ViewPager mPager;

    protected static AlphabeticalStationListFragment mAlphaStListFragment;
    protected static LinesStationListFragment mLinesStListFragment;
    protected static RecentStationListFragment mRecentStListFragment;
    protected static StationMapFragment mStationMapFragment;

    protected boolean m_bIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_station);

        // setup action bar for tabs
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);

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
                Log.d(MainActivity.TAG, "onPageSelected: " + arg0);
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

        tab = actionBar.newTab()
                .setText(R.string.sSelMapTab)
                .setTabListener(new TabListener<SherlockFragment>(3 + "", mPager));
        actionBar.addTab(tab);

        //get location from calling class
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int nType = extras.getInt(MainActivity.BUNDLE_EVENTSRC_KEY);
            m_bIn = extras.getBoolean(MainActivity.BUNDLE_ENTRANCE_KEY);
            switch (nType) {
                case MainActivity.DEPARTURE_RESULT:
                    setTitle(R.string.sFromStation);
                    break;
                case MainActivity.ARRIVAL_RESULT:
                    setTitle(R.string.sToStation);
                    break;
            }
        }
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
                case 3:
                    mStationMapFragment = new StationMapFragment();
                    return (SherlockFragment) mStationMapFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getSupportMenuInflater().inflate(R.menu.main, menu);
        menu.add(Menu.NONE, MainActivity.MENU_SETTINGS, Menu.NONE, R.string.sSettings)
                .setIcon(R.drawable.ic_action_settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE, MainActivity.MENU_ABOUT, Menu.NONE, R.string.sAbout)
                .setIcon(R.drawable.ic_action_about)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            case MainActivity.MENU_SETTINGS:
                // app icon in action bar clicked; go home
                onSettings();
                return true;
            case MainActivity.MENU_ABOUT:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                intentAbout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAbout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Finish(int nStationId, int nPortalId) {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.BUNDLE_STATIONID_KEY, nStationId);
        intent.putExtra(MainActivity.BUNDLE_PORTALID_KEY, nPortalId);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //update fragments to new data
        switch (requestCode) {
            case MainActivity.PREF_RESULT:
                if (mAlphaStListFragment != null)
                    mAlphaStListFragment.Update();
                if (mLinesStListFragment != null)
                    mLinesStListFragment.Update();
                if (mRecentStListFragment != null)
                    mRecentStListFragment.Update();
                break;
            default:
                break;
        }
    }

    protected void onSettings() {
        Intent intentSet = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intentSet, MainActivity.PREF_RESULT);
    }

    public boolean HasLimits() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(PreferencesActivity.KEY_PREF_HAVE_LIMITS, false);

    }
}
