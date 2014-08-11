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

import android.content.Context;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class StationMapView extends MapView {

    private double mStationLatitude;
    private double mStationLongitude;
    private GeoPoint mRestoredLocation = null;


    public StationMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy,
                          double latitude, double longitude) {
        super(context, tileSizePixels, resourceProxy);
        mStationLatitude = latitude;
        mStationLongitude = longitude;
    }

    public void setRestoredLocation(GeoPoint point) {
        mRestoredLocation = point;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        PanToStation();
    }

    protected void PanToStation() {
        GeoPoint pt = (mRestoredLocation == null)
                ? new GeoPoint(mStationLatitude, mStationLongitude)
                : mRestoredLocation;
        getController().animateTo(pt);
    }
}
