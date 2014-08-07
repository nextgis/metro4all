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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class StationMapActivity extends SherlockActivity {

    private Context mAppContext;

    private MapView mMapView;
    private ResourceProxy mResourceProxy;

    StationItem mStation = null;
    boolean isPortalIn;

    //overlays
    private MyLocationNewOverlay mLocationOverlay;
    private ItemizedIconOverlay<OverlayItem> mPointsOverlay;

    private ArrayList<OverlayItem> maItems;

    private final static String PREFS_TILE_SOURCE = "map_tile_source";
    private final static String PREFS_SCROLL_X = "map_scroll_x";
    private final static String PREFS_SCROLL_Y = "map_scroll_y";
    private final static String PREFS_ZOOM_LEVEL = "map_zoom_level";
//    private final static String PREFS_SHOW_LOCATION = "map_show_loc";
//    private final static String PREFS_SHOW_COMPASS = "map_show_compass";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent inIntent = getIntent();
        int stationID = inIntent.getIntExtra(MainActivity.PARAM_SEL_STATION_ID, 0);
        isPortalIn = inIntent.getBooleanExtra(MainActivity.PARAM_PORTAL_DIRECTION, true);

        mStation = MainActivity.GetGraph().GetStation(stationID);

        mAppContext = getApplicationContext();
        mResourceProxy = new ResourceProxyImpl(mAppContext);

        setTitle(String.format(
                getString(isPortalIn ? R.string.sInPortalMapTitle : R.string.sOutPortalMapTitle),
                mStation.GetName()));

        InitMap();
        PanToStation();

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

        mMapView = new MapView(mAppContext, 256, mResourceProxy);

        // Call this method to turn off hardware acceleration at the View level.
        setHardwareAccelerationOff();

        //add overlays
        mLocationOverlay = new MyLocationNewOverlay(mAppContext,
                new GpsMyLocationProvider(mAppContext), mMapView);
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
        maItems = new ArrayList<OverlayItem>();
        Drawable ivIn = getResources().getDrawable(R.drawable.portal_in);
        Drawable ivOut = getResources().getDrawable(R.drawable.portal_out);

        List<PortalItem> portalList = mStation.GetPortals(isPortalIn);

        for (PortalItem portal : portalList) {
            OverlayItem itemPortal = new OverlayItem(
                    mStation.GetId() + "", portal.GetId() + "",
                    String.format(getString(R.string.sStationPortalName), mStation.GetName(),
                            getString(isPortalIn ? R.string.sEntranceName : R.string.sExitName),
                            portal.GetName()),
                    new GeoPoint(portal.GetLatitude(), portal.GetLongitude()));

            itemPortal.setMarker(isPortalIn ? ivIn : ivOut);
            maItems.add(itemPortal);
        }

        mPointsOverlay = new ItemizedIconOverlay<OverlayItem>(maItems,
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
                        outIntent.putExtra(MainActivity.PARAM_SEL_STATION_ID,
                                selectedPortal.GetStationId());
                        outIntent.putExtra(MainActivity.PARAM_SEL_PORTAL_ID,
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
    }

    protected void PanToStation() {
        if (mStation != null) {
            GeoPoint pt = new GeoPoint(mStation.GetLatitude(), mStation.GetLongitude());
            mMapView.getController().animateTo(pt);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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

//        if (prefs.getBoolean(PREFS_SHOW_LOCATION, true)) {
            mLocationOverlay.enableMyLocation();
//        }
//        if (prefs.getBoolean(PREFS_SHOW_COMPASS, true)) {
//            mLocationOverlay.enableCompass();
//        }

        PanToStation();
    }

    @Override
    public void onPause() {
        final SharedPreferences.Editor edit =
                PreferenceManager.getDefaultSharedPreferences(mAppContext).edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
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
}
