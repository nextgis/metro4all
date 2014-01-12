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
    
    private SubStation station;
 
	public SubStationExit(Cursor c) {
		// TODO Auto-generated constructor stub
		id_entrance = c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_ID_ENTRANCE_COLUMN));
		name = c.getString(c.getColumnIndexOrThrow(DBHelper.PORTALS_NAME_COLUMN));
		id_station = c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_ID_STATION_COLUMN));
		direction = c.getString(c.getColumnIndexOrThrow(DBHelper.PORTALS_DIRECTION_COLUMN));

		double lat = c.getDouble(c.getColumnIndexOrThrow(DBHelper.PORTALS_LAT_COLUMN));
		double lng = c.getDouble(c.getColumnIndexOrThrow(DBHelper.PORTALS_LON_COLUMN));
		latlng = new LatLng(lat, lng);
		
		max_width = c.getFloat(c.getColumnIndexOrThrow(DBHelper.PORTALS_MAX_WIDTH_COLUMN));
		min_step = c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_MIN_STEP_COLUMN));
		min_step_ramp = c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_MIN_STEP_RAMP_COLUMN));
		lift = (c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_LIFT_COLUMN)) != 0);
		lift_minus_step = c.getInt(c.getColumnIndexOrThrow(DBHelper.PORTALS_LIFT_MINUS_STEP_COLUMN));
		min_rail_width = c.getFloat(c.getColumnIndexOrThrow(DBHelper.PORTALS_MIN_RAIL_WIDTH_COLUMN));
		max_rail_width = c.getFloat(c.getColumnIndexOrThrow(DBHelper.PORTALS_MAX_RAIL_WIDTH_COLUMN));
		max_angle = c.getFloat(c.getColumnIndexOrThrow(DBHelper.PORTALS_MAX_ANGLE_COLUMN));
	}

	public SubStation getStation() {
		return station;
	}

	public void setStation(SubStation subStation) {
		station = subStation;
	}

}
