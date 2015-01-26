/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Stanislav Petriakov
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

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.nextgis.metroaccess.data.MAGraph;

import org.json.JSONArray;

import java.io.File;
import java.util.Locale;

import static com.nextgis.metroaccess.Constants.APP_VERSION;
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_ARR_STATIONS;
import static com.nextgis.metroaccess.Constants.KEY_PREF_RECENT_DEP_STATIONS;
import static com.nextgis.metroaccess.PreferencesActivity.DeleteRecursive;
import static com.nextgis.metroaccess.SelectStationActivity.getRecentStations;
import static com.nextgis.metroaccess.SelectStationActivity.indexOf;

public class Analytics extends Application {
//    private static final String PROPERTY_ID = "UA-57998948-1";
    final static String PANE = "Pane";
    final static String MENU = "Menu";
    final static String PREFERENCE = "Preference";
    final static String ACTION_BAR = "ActionBar";
    final static String ACTION_ITEM = "List Item";

    final static String SCREEN_MAIN = "Main Screen";
    final static String SCREEN_PREFERENCE = "Preferences Screen";
    final static String SCREEN_MAP = "Map Screen";
    final static String SCREEN_LAYOUT = "Layout Screen";
    final static String SCREEN_SELECT_STATION = "Select Station Screen";
    final static String SCREEN_ROUTING = "Routing Screen";

    final static String FROM = "From";
    final static String TO = "To";
    final static String TAB_AZ = "Tab A...Z";
    final static String TAB_LINES = "Tab Lines";
    final static String TAB_RECENT = "Tab Recent";

    final static String BTN_MAP = "Map";
    final static String BTN_LAYOUT = "Layout";
    final static String MENU_ABOUT = "About";
    final static String MENU_SETTINGS = "Settings";
    final static String BACK = "Back";
    final static String LIMITATIONS = "Limitations";
    final static String PORTAL = "Portal selected";
    final static String HEADER = "Header clicked";
    final static String STATION_EXPAND = "Station expanded";
    final static String STATION_COLLAPSE = "Station collapsed";
    final static String LEGEND = "Legend";

    private Tracker tracker;
    private static MAGraph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        updateApplicationStructure(prefs);

        String sCurrentCity = prefs.getString(PreferencesActivity.KEY_PREF_CITY, "");
        String sCurrentCityLang = prefs.getString(PreferencesActivity.KEY_PREF_CITYLANG, Locale.getDefault().getLanguage());
        mGraph = new MAGraph(this, sCurrentCity, getExternalFilesDir(null), sCurrentCityLang);
    }

    public static MAGraph getGraph(){
		return mGraph;
	}

    synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.WARNING);
            tracker = analytics.newTracker(R.xml.app_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }

        return tracker;
    }

    public void reload(boolean disableGA) {
        disableGA = !disableGA;
        GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(disableGA);

        if (!disableGA)
            getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void addEvent(String category, String action, String label, Integer... value) {
        HitBuilders.EventBuilder event = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label);

        if (value != null && value.length > 0)
            event.setValue(value[0]);

        getTracker().send(event.build());
    }


    private void updateApplicationStructure(SharedPreferences prefs) {
        try {
            int currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int savedVersionCode = prefs.getInt(APP_VERSION, 0);

            switch (savedVersionCode) {
                case 0:
                    // ==========Improvement==========
                    File oDataFolder = new File(getExternalFilesDir(MainActivity.GetRouteDataDir()).getPath());
                    DeleteRecursive(oDataFolder);
                    // ==========End Improvement==========
                case 14:
                case 15:
                    // delete unnecessary data
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("recent_dep_counter");
                    editor.remove("recent_arr_counter");

                    JSONArray depStationsIds = getRecentStations(prefs, true);
                    JSONArray arrStationsIds = getRecentStations(prefs, false);

                    // convert recent stations to new format
                    for (int i = 0; i < 10; i++) {
                        int dep = prefs.getInt("recent_dep_stationid" + i, -1);
                        int arr = prefs.getInt("recent_arr_stationid" + i, -1);
                        editor.remove("recent_dep_stationid" + i);
                        editor.remove("recent_arr_stationid" + i);
                        editor.remove("recent_dep_portalid" + i);
                        editor.remove("recent_arr_portalid" + i);

                        if(dep != -1 && indexOf(depStationsIds, dep) == -1)
                            depStationsIds.put(dep);

                        if(arr != -1 && indexOf(arrStationsIds, arr) == -1)
                            arrStationsIds.put(arr);
                    }

                    editor.putString(KEY_PREF_RECENT_DEP_STATIONS, depStationsIds.toString());
                    editor.putString(KEY_PREF_RECENT_ARR_STATIONS, arrStationsIds.toString());
                    editor.apply();
                    break;
                default:
                    break;
            }

            if(savedVersionCode < currentVersionCode) { // update from previous version or clean install
                // save current version to preferences
                prefs.edit().putInt(APP_VERSION, currentVersionCode).apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        }
    }
}
