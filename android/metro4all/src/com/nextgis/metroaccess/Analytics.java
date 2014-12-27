/**
 * Created by 4eRT on 27.12.2014.
 */
package com.nextgis.metroaccess;

import android.app.Application;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.HashMap;

public class Analytics extends Application {
//    private static final String PROPERTY_ID = "UA-57998948-1";

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
        GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(disableGA);

        if (!disableGA)
            try { getTracker().send(new HitBuilders.ScreenViewBuilder().build()); }
            catch(Exception e) { }
    }
}
