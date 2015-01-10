/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Stanislav Petriakov
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

import android.app.Application;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.HashMap;

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
    final static String STATION_EXPAND = "Station expanded";
    final static String STATION_COLLAPSE = "Station collapsed";
    final static String LEGEND = "Legend";

    private Tracker tracker;

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
}
