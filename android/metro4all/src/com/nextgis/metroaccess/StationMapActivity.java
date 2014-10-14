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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import static com.nextgis.metroaccess.Constants.*;

public class StationMapActivity extends SherlockActivity {

    protected int mnType;
    protected int mnMaxWidth, mnWheelWidth;
    protected boolean m_bHaveLimits;

/*  // commented because of bug https://github.com/osmdroid/osmdroid/issues/49
    private final static String PREFS_TILE_SOURCE = "map_tile_source";
*/
    private final static String PREFS_SCROLL_X = "map_scroll_x";
    private final static String PREFS_SCROLL_Y = "map_scroll_y";
    private final static String PREFS_ZOOM_LEVEL = "map_zoom_level";
    private final static String PREFS_MAP_LATITUDE = "map_latitude";
    private final static String PREFS_MAP_LONGITUDE = "map_longitude";

//    private final static String PREFS_SHOW_LOCATION = "map_show_loc";
//    private final static String PREFS_SHOW_COMPASS = "map_show_compass";

    private Context mAppContext;

    private StationMapView mMapView;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ResourceProxy mResourceProxy;
    private float scaledDensity;

    private int mStationID;
    private boolean mIsPortalIn;
    private List<StationItem> stationList;

    //overlays
    private MyLocationNewOverlay mLocationOverlay;
    private ItemizedIconOverlay<OverlayItem> mPointsOverlay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent inIntent = getIntent();
        mStationID = inIntent.getIntExtra(PARAM_SEL_STATION_ID, 0);
        mIsPortalIn = inIntent.getBooleanExtra(PARAM_PORTAL_DIRECTION, true);

        StationItem station = MainActivity.GetGraph().GetStation(mStationID);

        mAppContext = getApplicationContext();
        mResourceProxy = new ResourceProxyImpl(mAppContext);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        mnType = prefs.getInt(PreferencesActivity.KEY_PREF_USER_TYPE + "_int", 2);
        mnMaxWidth = prefs.getInt(PreferencesActivity.KEY_PREF_MAX_WIDTH + "_int", 400);
        mnWheelWidth = prefs.getInt(PreferencesActivity.KEY_PREF_WHEEL_WIDTH + "_int", 400);
        m_bHaveLimits = prefs.getBoolean(PreferencesActivity.KEY_PREF_HAVE_LIMITS, false);

        setTitle(String.format(
                getString(mIsPortalIn
                        ? R.string.sInPortalMapTitle : R.string.sOutPortalMapTitle),
                station.GetName()));

        mMapView = new StationMapView(mAppContext, 256, mResourceProxy,
                new GeoPoint(station.GetLatitude(), station.GetLongitude()));

        InitMap();

        setContentView(mMapView);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setHardwareAccelerationOff() {
        // Turn off hardware acceleration here, or in manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    protected void InitMap() {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mAppContext);

        // Call this method to turn off hardware acceleration at the View level.
        setHardwareAccelerationOff();

        gpsMyLocationProvider = new GpsMyLocationProvider(mAppContext);

        //add overlays
        mLocationOverlay =
                new MyLocationNewOverlay(mAppContext, gpsMyLocationProvider, mMapView);
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mLocationOverlay.enableMyLocation();

        mMapView.getOverlays().add(mLocationOverlay);

        LoadPortalsToOverlay();

        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getController().setZoom(prefs.getInt(PREFS_ZOOM_LEVEL, 15));
        mMapView.scrollTo(prefs.getInt(PREFS_SCROLL_X, 0),
                prefs.getInt(PREFS_SCROLL_Y, 0));
    }

    protected void LoadPortalsToOverlay() {
        scaledDensity = getBaseContext().getResources().getDisplayMetrics().scaledDensity;

        ArrayList<OverlayItem> overlayPortals = new ArrayList<OverlayItem>();
        ArrayList<OverlayItem> overlayTransparentPortals = new ArrayList<OverlayItem>();

        Bitmap original = BitmapFactory.decodeResource(mAppContext.getResources(),
                mIsPortalIn ? R.drawable.portal_in : R.drawable.portal_out);
        Drawable markerPortal = new BitmapDrawable(mAppContext.getResources(),
                Bitmap.createScaledBitmap(original,
                        (int) (scaledDensity * original.getWidth()),
                        (int) (scaledDensity * original.getHeight()), false));

        original = BitmapFactory.decodeResource(mAppContext.getResources(),
                mIsPortalIn ? R.drawable.portal_in_tr : R.drawable.portal_out_tr);
        Drawable markerTransparentPortal = new BitmapDrawable(mAppContext.getResources(),
                Bitmap.createScaledBitmap(original,
                        (int) (scaledDensity * original.getWidth()),
                        (int) (scaledDensity * original.getHeight()), false));

        original = BitmapFactory.decodeResource(mAppContext.getResources(),
                R.drawable.portal_invalid);
        Drawable markerInvalidPortal = new BitmapDrawable(mAppContext.getResources(),
                Bitmap.createScaledBitmap(original,
                        (int) (scaledDensity * original.getWidth()),
                        (int) (scaledDensity * original.getHeight()), false));

        original = BitmapFactory.decodeResource(mAppContext.getResources(),
                R.drawable.portal_invalid_tr);
        Drawable markerTransparentInvalidPortal = new BitmapDrawable(mAppContext.getResources(),
                Bitmap.createScaledBitmap(original,
                        (int) (scaledDensity * original.getWidth()),
                        (int) (scaledDensity * original.getHeight()), false));

        markerTransparentPortal.setAlpha(127);
        markerTransparentInvalidPortal.setAlpha(127);

        stationList = new ArrayList<StationItem>(MainActivity.GetGraph().GetStations().values());

        double minLat = 0, minLong = 0, maxLat = 0, maxLong = 0;

        int stationListSize = stationList.size();
        int i = 0;
        boolean isForSelectedStation = false;

        while (i < stationListSize || isForSelectedStation) {
            StationItem station;

            if (!isForSelectedStation) {
                station = stationList.get(i);
            } else {
                station = MainActivity.GetGraph().GetStation(mStationID);
            }

            boolean isSelectedStation = isForSelectedStation || (station.GetId() == mStationID);

            if (isSelectedStation && !isForSelectedStation) {
                ++i;
                isForSelectedStation = !isForSelectedStation && (i == stationListSize);
                continue;
            }

            List<PortalItem> portalList = station.GetPortals(mIsPortalIn);

            if (isSelectedStation) {
                minLat = maxLat = portalList.get(0).GetLatitude();
                minLong = maxLong = portalList.get(0).GetLongitude();
            }

            for (PortalItem portal : portalList) {
                OverlayItem itemPortal = new OverlayItem(
                        station.GetId() + "", portal.GetId() + "",
                        String.format(getString(R.string.sStationPortalName), station.GetName(),
                                getString(mIsPortalIn
                                        ? R.string.sEntranceName : R.string.sExitName),
                                portal.GetName()),
                        new GeoPoint(portal.GetLatitude(), portal.GetLongitude()));

                boolean isInvalidPortal = false;

                if (mnType > 1) {
                    boolean bSmallWidth = portal.GetDetailes()[0] < mnMaxWidth;
                    boolean bCanRoll = portal.GetDetailes()[7] == 0
                            || portal.GetDetailes()[5] <= mnWheelWidth
                            && (portal.GetDetailes()[6] == 0
                                || mnWheelWidth <= portal.GetDetailes()[6]);
                    if (m_bHaveLimits && (bSmallWidth || !bCanRoll)) {
                        isInvalidPortal = true;
                    }
                }

                if (isSelectedStation) {
                    itemPortal.setMarker(isInvalidPortal ? markerInvalidPortal : markerPortal);

                    double portalLat = portal.GetLatitude();
                    double portalLong = portal.GetLongitude();

                    if (portalLat < minLat)
                        minLat = portalLat;
                    if (portalLat > maxLat)
                        maxLat = portalLat;
                    if (portalLong < minLong)
                        minLong = portalLong;
                    if (portalLong > maxLong)
                        maxLong = portalLong;

                    overlayPortals.add(itemPortal);

                } else {
                    itemPortal.setMarker(isInvalidPortal
                            ? markerTransparentInvalidPortal : markerTransparentPortal);
                    overlayTransparentPortals.add(itemPortal);
                }
            }

            ++i;
            isForSelectedStation = !isForSelectedStation && (i == stationListSize);
        }

        mMapView.setMapCenter(new GeoPoint((maxLat - minLat) / 2 + minLat,
                (maxLong - minLong) / 2 + minLong));

        ArrayList<OverlayItem> overlayItems = overlayTransparentPortals;

        for (int j = 0; j < 2; ++j) {
            mPointsOverlay = new ItemizedIconOverlay<OverlayItem>(overlayItems,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                        public boolean onItemSingleTapUp(final int index,
                                                         final OverlayItem item) {
                            StationItem selectedStation = MainActivity.GetGraph()
                                    .GetStation(Integer.parseInt(item.getUid()));

                            if (selectedStation == null) {
                                Toast.makeText(mAppContext, R.string.sNoOrEmptyData,
                                        Toast.LENGTH_LONG).show();
                                return true;
                            }

                            PortalItem selectedPortal = selectedStation
                                    .GetPortal(Integer.parseInt(item.getTitle()));

                            if (selectedPortal == null) {
                                Toast.makeText(mAppContext, R.string.sNoOrEmptyData,
                                        Toast.LENGTH_LONG).show();
                                return true;
                            }

                            Intent outIntent = new Intent();
                            outIntent.putExtra(PARAM_SEL_STATION_ID,
                                    selectedPortal.GetStationId());
                            outIntent.putExtra(PARAM_SEL_PORTAL_ID,
                                    selectedPortal.GetId());
                            setResult(RESULT_OK, outIntent);
                            finish();

                            return true; // We 'handled' this event.
                        }

                        public boolean onItemLongPress(final int index,
                                                       final OverlayItem item) {
                            Toast.makeText(mAppContext, item.getSnippet(),
                                    Toast.LENGTH_LONG).show();
                            return true; // We 'handled' this event.
                        }
                    }
                    , mResourceProxy);

            mMapView.getOverlays().add(mPointsOverlay);

            overlayItems = overlayPortals;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

/*  // commented because of bug https://github.com/osmdroid/osmdroid/issues/49
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mAppContext);
        final String tileSourceName = prefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource =
                    TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }
*/

        // For bug https://github.com/osmdroid/osmdroid/issues/49
        // "Tiles are too small on high dpi devices"
        // It is from sources of TileSourceFactory
        final int newScale = (int) (256 * scaledDensity);
        OnlineTileSourceBase mapSource = new XYTileSource(
                "Mapnik",
                ResourceProxy.string.mapnik,
                0,
                18,
                newScale,
                ".png",
                new String[]{
                        "http://a.tile.openstreetmap.org/",
                        "http://b.tile.openstreetmap.org/",
                        "http://c.tile.openstreetmap.org/"});
        mMapView.setTileSource(mapSource);


//        if (prefs.getBoolean(PREFS_SHOW_LOCATION, true)) {
            mLocationOverlay.enableMyLocation();
//        }
//        if (prefs.getBoolean(PREFS_SHOW_COMPASS, true)) {
//            mLocationOverlay.enableCompass();
//        }
    }

    @Override
    public void onPause() {
        final SharedPreferences.Editor edit =
                PreferenceManager.getDefaultSharedPreferences(mAppContext).edit();

/*  // commented because of bug https://github.com/osmdroid/osmdroid/issues/49
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
*/
        edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
//        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
//        edit.putBoolean(PREFS_SHOW_COMPASS, mLocationOverlay.isCompassEnabled());

        edit.commit();

        mLocationOverlay.disableMyLocation();
//        mLocationOverlay.disableCompass();

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(PREFS_MAP_LATITUDE, mMapView.getMapCenter().getLatitude());
        outState.putDouble(PREFS_MAP_LONGITUDE, mMapView.getMapCenter().getLongitude());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        double nLat = savedInstanceState.getDouble(PREFS_MAP_LATITUDE, 0);
        double nLong = savedInstanceState.getDouble(PREFS_MAP_LONGITUDE, 0);
        mMapView.setRestoredMapCenter(new GeoPoint(nLat, nLong));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getSupportMenuInflater();
        infl.inflate(R.menu.menu_station_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.btn_location_found:
                onLocationFoundClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onLocationFoundClick() {
        Location myLocation = gpsMyLocationProvider.getLastKnownLocation();

        if (null == myLocation) return;

        if (mMapView.getScreenRect(null).height() > 0) {

            StationItem nearestStation = null;
            float minDistance = Float.MAX_VALUE;

            for (StationItem station : stationList) {

                List<PortalItem> portalList = station.GetPortals(mIsPortalIn);
                float[] distanceToPortal = new float[1];

                for (PortalItem portal : portalList) {

                    Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                            portal.GetLatitude(), portal.GetLongitude(), distanceToPortal);

                    if (distanceToPortal[0] < minDistance) {
                        minDistance = distanceToPortal[0];
                        nearestStation = station;
                    }
                }
            }

            if (null != nearestStation) {

                List<PortalItem> portalList = nearestStation.GetPortals(mIsPortalIn);

                float[] distanceToPortal = new float[1];
                float maxDistance = 0;
                PortalItem farPortal = null;

                for (PortalItem portal : portalList) {

                    Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                            portal.GetLatitude(), portal.GetLongitude(), distanceToPortal);

                    if (distanceToPortal[0] > maxDistance) {
                        maxDistance = distanceToPortal[0];
                        farPortal = portal;
                    }
                }

                if (null != farPortal) {

                    GeoPoint myGeoPoint = new GeoPoint(myLocation);

                    int north = myGeoPoint.destinationPoint(maxDistance, 0).getLatitudeE6();
                    int south = myGeoPoint.destinationPoint(maxDistance, 180).getLatitudeE6();
                    int east = myGeoPoint.destinationPoint(maxDistance, 90).getLongitudeE6();
                    int west = myGeoPoint.destinationPoint(maxDistance, 270).getLongitudeE6();

                    mMapView.zoomToBoundingBox(new BoundingBoxE6(north, east, south, west));

                    Toast.makeText(mAppContext,
                            String.format(getString(R.string.sNearestStation),
                                    nearestStation.GetName()),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        mMapView.getController().animateTo(new GeoPoint(myLocation));
    }
}
