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
import android.widget.Toast;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class StationMapView extends MapView {

    private GeoPoint mMapCenter = null;
    private GeoPoint mRestoredMapCenter = null;


    public StationMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy,
                          GeoPoint center) {
        super(context, tileSizePixels, resourceProxy);
        mMapCenter = center;
    }

    public void setMapCenter(GeoPoint point) {
        mMapCenter = point;
    }

    public void setRestoredMapCenter(GeoPoint point) {
        mRestoredMapCenter = point;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        PanToStation();

        Context mContext = getContext();
        boolean isNotNetwork = !MetaDownloader.IsNetworkAvailible(mContext);

        if (isNotNetwork)
            Toast.makeText(mContext, mContext.getString(R.string.sNetworkUnreachErr),
                    Toast.LENGTH_LONG).show();
    }

    protected void PanToStation() {
        GeoPoint pt = (mRestoredMapCenter == null) ? mMapCenter : mRestoredMapCenter;
        getController().animateTo(pt);
    }
}
