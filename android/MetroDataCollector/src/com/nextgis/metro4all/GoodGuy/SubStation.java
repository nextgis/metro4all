package com.nextgis.metro4all.GoodGuy;

import android.database.Cursor;

public class SubStation {
	/**
	 * @param lon the lon to set
	 */
	public void setLon(float lon) {
		this.lon = lon;
	}
	private long _id;
	private int id_station;
	private int id_line;
	private String name;
	private String name_en;
	private float lat;
	private float lon;
	public static SubStation fromCursor(Cursor c) {
		// TODO Auto-generated method stub
		SubStation station = new SubStation();
		station.setId(c.getLong(c.getColumnIndexOrThrow("_id")));
		station.setId_station(c.getInt(c.getColumnIndexOrThrow("id_station")));
		station.setId_line(c.getInt(c.getColumnIndexOrThrow("id_line")));
		station.setName(c.getString(c.getColumnIndexOrThrow("name")));
		station.setName_en(c.getString(c.getColumnIndexOrThrow("name_en")));
		station.setLat(c.getFloat(c.getColumnIndexOrThrow("lat")));
		station.setLon(c.getFloat(c.getColumnIndexOrThrow("lon")));
		
		return station;
	}
	

	/**
	 * @return the _id
	 */
	public long getId() {
		return _id;
	}


	/**
	 * @param _id the _id to set
	 */
	private void setId(long _id) {
		this._id = _id;
	}


	/**
	 * @return the id_station
	 */
	public int getId_station() {
		return id_station;
	}
	/**
	 * @param id_station the id_station to set
	 */
	public void setId_station(int id_station) {
		this.id_station = id_station;
	}
	/**
	 * @return the id_line
	 */
	public int getId_line() {
		return id_line;
	}
	/**
	 * @param id_line the id_line to set
	 */
	public void setId_line(int id_line) {
		this.id_line = id_line;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name_en
	 */
	public String getName_en() {
		return name_en;
	}
	/**
	 * @param name_en the name_en to set
	 */
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	/**
	 * @return the lat
	 */
	public float getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(float lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public float getLon() {
		return lon;
	}
	

}
