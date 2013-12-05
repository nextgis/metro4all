package com.nextgis.metro4all.GoodGuy;

import android.database.Cursor;

public class SubLine {
	
	private long _id;
	private int id_line;
	private String name;
	private String name_en;
	private String color;
	
	public SubLine() {
		// dummy constructor
	}
	
	public static SubLine fromCursor(Cursor c) {
		SubLine line = new SubLine();
		line.setId(c.getLong(c.getColumnIndexOrThrow("_id")));
		line.setId_line(c.getInt(c.getColumnIndexOrThrow("id_line")));
		line.setName(c.getString(c.getColumnIndexOrThrow("name")));
		line.setName_en(c.getString(c.getColumnIndexOrThrow("name_en")));
		line.setColor(c.getString(c.getColumnIndexOrThrow("color")));
		return line;
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
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
