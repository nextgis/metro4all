package com.nextgis.whichexit;

import com.google.android.gms.maps.model.LatLng;

import android.database.Cursor;

public class SubStationExit {

    int id_entrance;
    String name;
    int id_station;
    String direction;
    LatLng latlng;

    float max_width;
    int min_step;
    int min_step_ramp;
    boolean lift;
    int lift_minus_step;
    float min_rail_width;
    float max_rail_width;
    float max_angle;
 
	public SubStationExit(Cursor c) {
		// TODO Auto-generated constructor stub
		double lat = c.getDouble(c.getColumnIndexOrThrow(DBHelper.PORTALS_LAT_COLUMN));
		double lng = c.getDouble(c.getColumnIndexOrThrow(DBHelper.PORTALS_LON_COLUMN));
		latlng = new LatLng(lat, lng);
		name = c.getString(c.getColumnIndexOrThrow(DBHelper.PORTALS_NAME_COLUMN));
		direction = c.getString(c.getColumnIndexOrThrow(DBHelper.PORTALS_DIRECTION_COLUMN));
	}

}
