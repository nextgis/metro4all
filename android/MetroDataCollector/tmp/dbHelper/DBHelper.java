/**********************************************************************************************************************************************************************
****** AUTO GENERATED FILE BY ANDROID SQLITE HELPER SCRIPT BY FEDERICO PAOLINELLI. ANY CHANGE WILL BE WIPED OUT IF THE SCRIPT IS PROCESSED AGAIN. *******
**********************************************************************************************************************************************************************/
package com.nextgis.metro4all.GoodGuy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.util.Date;

public class DBHelper{
    
    private static final String TAG = "DBHelper";

    private static final String DATABASE_NAME = "metroaccessDb.db";
    private static final int DATABASE_VERSION = 1;


    // Variable to hold the database instance
    protected SQLiteDatabase mDb;
    // Context of the application using the database.
    private final Context mContext;
    // Database open/upgrade helper
    private MyDbHelper mDbHelper;
    
    public DBHelper(Context context) {
        mContext = context;
        mDbHelper = new MyDbHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public DBHelper open() throws SQLException { 
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
                                                     
    public void close() {
        mDb.close();
    }

	// -------------- LINES DEFINITIONS ------------

	public static final String LINES_TABLE = "lines";
	public static final String LINES_ID_LINE_KEY = "id_line";
	protected static final int LINES_ID_LINE_COLUMN = 1;
	public static final String LINES_NAME_KEY = "name";
	protected static final int LINES_NAME_COLUMN = 2;
	public static final String LINES_NAME_EN_KEY = "name_en";
	protected static final int LINES_NAME_EN_COLUMN = 3;
	public static final String LINES_COLOR_KEY = "color";
	protected static final int LINES_COLOR_COLUMN = 4;
	public static final String LINES_ROW_ID = "_id";

	// -------------- STATIONS DEFINITIONS ------------

	public static final String STATIONS_TABLE = "stations";
	public static final String STATIONS_ID_STATION_KEY = "id_station";
	protected static final int STATIONS_ID_STATION_COLUMN = 1;
	public static final String STATIONS_ID_LINE_KEY = "id_line";
	protected static final int STATIONS_ID_LINE_COLUMN = 2;
	public static final String STATIONS_NAME_KEY = "name";
	protected static final int STATIONS_NAME_COLUMN = 3;
	public static final String STATIONS_NAME_EN_KEY = "name_en";
	protected static final int STATIONS_NAME_EN_COLUMN = 4;
	public static final String STATIONS_LAT_KEY = "lat";
	protected static final int STATIONS_LAT_COLUMN = 5;
	public static final String STATIONS_LON_KEY = "lon";
	protected static final int STATIONS_LON_COLUMN = 6;
	public static final String STATIONS_ROW_ID = "_id";

	// -------------- PORTALS DEFINITIONS ------------

	public static final String PORTALS_TABLE = "portals";
	public static final String PORTALS_ID_ENTRANCE_KEY = "id_entrance";
	protected static final int PORTALS_ID_ENTRANCE_COLUMN = 1;
	public static final String PORTALS_NAME_KEY = "name";
	protected static final int PORTALS_NAME_COLUMN = 2;
	public static final String PORTALS_ID_STATION_KEY = "id_station";
	protected static final int PORTALS_ID_STATION_COLUMN = 3;
	public static final String PORTALS_DIRECTION_KEY = "direction";
	protected static final int PORTALS_DIRECTION_COLUMN = 4;
	public static final String PORTALS_LAT_KEY = "lat";
	protected static final int PORTALS_LAT_COLUMN = 5;
	public static final String PORTALS_LON_KEY = "lon";
	protected static final int PORTALS_LON_COLUMN = 6;
	public static final String PORTALS_MAX_WIDTH_KEY = "max_width";
	protected static final int PORTALS_MAX_WIDTH_COLUMN = 7;
	public static final String PORTALS_MIN_STEP_KEY = "min_step";
	protected static final int PORTALS_MIN_STEP_COLUMN = 8;
	public static final String PORTALS_MIN_STEP_RAMP_KEY = "min_step_ramp";
	protected static final int PORTALS_MIN_STEP_RAMP_COLUMN = 9;
	public static final String PORTALS_LIFT_KEY = "lift";
	protected static final int PORTALS_LIFT_COLUMN = 10;
	public static final String PORTALS_LIFT_MINUS_STEP_KEY = "lift_minus_step";
	protected static final int PORTALS_LIFT_MINUS_STEP_COLUMN = 11;
	public static final String PORTALS_MIN_RAIL_WIDTH_KEY = "min_rail_width";
	protected static final int PORTALS_MIN_RAIL_WIDTH_COLUMN = 12;
	public static final String PORTALS_MAX_RAIL_WIDTH_KEY = "max_rail_width";
	protected static final int PORTALS_MAX_RAIL_WIDTH_COLUMN = 13;
	public static final String PORTALS_MAX_ANGLE_KEY = "max_angle";
	protected static final int PORTALS_MAX_ANGLE_COLUMN = 14;
	public static final String PORTALS_ROW_ID = "_id";

	// -------------- GRAPH DEFINITIONS ------------

	public static final String GRAPH_TABLE = "graph";
	public static final String GRAPH_ID_FROM_KEY = "id_from";
	protected static final int GRAPH_ID_FROM_COLUMN = 1;
	public static final String GRAPH_ID_TO_KEY = "id_to";
	protected static final int GRAPH_ID_TO_COLUMN = 2;
	public static final String GRAPH_NAME_FROM_KEY = "name_from";
	protected static final int GRAPH_NAME_FROM_COLUMN = 3;
	public static final String GRAPH_NAME_TO_KEY = "name_to";
	protected static final int GRAPH_NAME_TO_COLUMN = 4;
	public static final String GRAPH_COST_KEY = "cost";
	protected static final int GRAPH_COST_COLUMN = 5;
	public static final String GRAPH_ROW_ID = "_id";

	// -------------- INTERCHANGES DEFINITIONS ------------

	public static final String INTERCHANGES_TABLE = "interchanges";
	public static final String INTERCHANGES_STATION_FROM_KEY = "station_from";
	protected static final int INTERCHANGES_STATION_FROM_COLUMN = 1;
	public static final String INTERCHANGES_STATION_TO_KEY = "station_to";
	protected static final int INTERCHANGES_STATION_TO_COLUMN = 2;
	public static final String INTERCHANGES_MAX_WIDTH_KEY = "max_width";
	protected static final int INTERCHANGES_MAX_WIDTH_COLUMN = 3;
	public static final String INTERCHANGES_MIN_STEP_KEY = "min_step";
	protected static final int INTERCHANGES_MIN_STEP_COLUMN = 4;
	public static final String INTERCHANGES_MIN_STEP_RAMP_KEY = "min_step_ramp";
	protected static final int INTERCHANGES_MIN_STEP_RAMP_COLUMN = 5;
	public static final String INTERCHANGES_LIFT_KEY = "lift";
	protected static final int INTERCHANGES_LIFT_COLUMN = 6;
	public static final String INTERCHANGES_LIFT_MINUS_STEP_KEY = "lift_minus_step";
	protected static final int INTERCHANGES_LIFT_MINUS_STEP_COLUMN = 7;
	public static final String INTERCHANGES_MIN_RAIL_WIDTH_KEY = "min_rail_width";
	protected static final int INTERCHANGES_MIN_RAIL_WIDTH_COLUMN = 8;
	public static final String INTERCHANGES_MAX_RAIL_WIDTH_KEY = "max_rail_width";
	protected static final int INTERCHANGES_MAX_RAIL_WIDTH_COLUMN = 9;
	public static final String INTERCHANGES_MAX_ANGLE_KEY = "max_angle";
	protected static final int INTERCHANGES_MAX_ANGLE_COLUMN = 10;
	public static final String INTERCHANGES_ROW_ID = "_id";



	// -------- TABLES CREATION ----------

	// lines CREATION 
	private static final String DATABASE_LINES_CREATE = "create table " + LINES_TABLE + " (" + 
				 LINES_ROW_ID + " integer primary key autoincrement" + ", " + 
				 LINES_ID_LINE_KEY + " integer  " + ", " + 
				 LINES_NAME_KEY + " text  " + ", " + 
				 LINES_NAME_EN_KEY + " text  " + ", " + 
				 LINES_COLOR_KEY + " text  " + ");";


	// stations CREATION 
	private static final String DATABASE_STATIONS_CREATE = "create table " + STATIONS_TABLE + " (" + 
				 STATIONS_ROW_ID + " integer primary key autoincrement" + ", " + 
				 STATIONS_ID_STATION_KEY + " integer  " + ", " + 
				 STATIONS_ID_LINE_KEY + " integer  " + ", " + 
				 STATIONS_NAME_KEY + " text  " + ", " + 
				 STATIONS_NAME_EN_KEY + " text  " + ", " + 
				 STATIONS_LAT_KEY + " float  " + ", " + 
				 STATIONS_LON_KEY + " float  " + ");";


	// portals CREATION 
	private static final String DATABASE_PORTALS_CREATE = "create table " + PORTALS_TABLE + " (" + 
				 PORTALS_ROW_ID + " integer primary key autoincrement" + ", " + 
				 PORTALS_ID_ENTRANCE_KEY + " integer  " + ", " + 
				 PORTALS_NAME_KEY + " text  " + ", " + 
				 PORTALS_ID_STATION_KEY + " integer  " + ", " + 
				 PORTALS_DIRECTION_KEY + " text  " + ", " + 
				 PORTALS_LAT_KEY + " float  " + ", " + 
				 PORTALS_LON_KEY + " float  " + ", " + 
				 PORTALS_MAX_WIDTH_KEY + " integer  " + ", " + 
				 PORTALS_MIN_STEP_KEY + " integer  " + ", " + 
				 PORTALS_MIN_STEP_RAMP_KEY + " integer  " + ", " + 
				 PORTALS_LIFT_KEY + " integer  " + ", " + 
				 PORTALS_LIFT_MINUS_STEP_KEY + " integer  " + ", " + 
				 PORTALS_MIN_RAIL_WIDTH_KEY + " integer  " + ", " + 
				 PORTALS_MAX_RAIL_WIDTH_KEY + " integer  " + ", " + 
				 PORTALS_MAX_ANGLE_KEY + " integer  " + ");";


	// graph CREATION 
	private static final String DATABASE_GRAPH_CREATE = "create table " + GRAPH_TABLE + " (" + 
				 GRAPH_ROW_ID + " integer primary key autoincrement" + ", " + 
				 GRAPH_ID_FROM_KEY + " integer  " + ", " + 
				 GRAPH_ID_TO_KEY + " integer  " + ", " + 
				 GRAPH_NAME_FROM_KEY + " text  " + ", " + 
				 GRAPH_NAME_TO_KEY + " text  " + ", " + 
				 GRAPH_COST_KEY + " integer  " + ");";


	// interchanges CREATION 
	private static final String DATABASE_INTERCHANGES_CREATE = "create table " + INTERCHANGES_TABLE + " (" + 
				 INTERCHANGES_ROW_ID + " integer primary key autoincrement" + ", " + 
				 INTERCHANGES_STATION_FROM_KEY + " text  " + ", " + 
				 INTERCHANGES_STATION_TO_KEY + " text  " + ", " + 
				 INTERCHANGES_MAX_WIDTH_KEY + " integer  " + ", " + 
				 INTERCHANGES_MIN_STEP_KEY + " integer  " + ", " + 
				 INTERCHANGES_MIN_STEP_RAMP_KEY + " integer  " + ", " + 
				 INTERCHANGES_LIFT_KEY + " integer  " + ", " + 
				 INTERCHANGES_LIFT_MINUS_STEP_KEY + " integer  " + ", " + 
				 INTERCHANGES_MIN_RAIL_WIDTH_KEY + " integer  " + ", " + 
				 INTERCHANGES_MAX_RAIL_WIDTH_KEY + " integer  " + ", " + 
				 INTERCHANGES_MAX_ANGLE_KEY + " integer  " + ");";




	// -------------- LINES HELPERS ------------------
	public long addlines(Integer id_line, String name, String name_en, String color)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(LINES_ID_LINE_KEY, id_line);
		contentValues.put(LINES_NAME_KEY, name);
		contentValues.put(LINES_NAME_EN_KEY, name_en);
		contentValues.put(LINES_COLOR_KEY, color);
		return mDb.insert(LINES_TABLE, null, contentValues);
	
	}

	public long updatelines(long rowIndex, Integer id_line, String name, String name_en, String color)
	{
		String where = LINES_ROW_ID + " = " + rowIndex;
		ContentValues contentValues = new ContentValues();
		contentValues.put(LINES_ID_LINE_KEY, id_line);
		contentValues.put(LINES_NAME_KEY, name);
		contentValues.put(LINES_NAME_EN_KEY, name_en);
		contentValues.put(LINES_COLOR_KEY, color);
		return mDb.update(LINES_TABLE, contentValues, where, null);
	
	}

	public boolean removelines(Long rowIndex)
	{
		return mDb.delete(LINES_TABLE, LINES_ROW_ID + " = " + rowIndex, null) > 0;
	}

	public boolean removeAlllines()
	{
		return mDb.delete(LINES_TABLE, null, null) > 0;
	}

	public Cursor getAlllines()
	{
		return mDb.query(LINES_TABLE, new String[] {
					LINES_ROW_ID,
					LINES_ID_LINE_KEY,
					LINES_NAME_KEY,
					LINES_NAME_EN_KEY,
					LINES_COLOR_KEY}, null, null, null, null, null);
	}

	public Cursor getlines(long rowIndex)
	{
		Cursor res = mDb.query(LINES_TABLE, new String[] {
					LINES_ROW_ID,
					LINES_ID_LINE_KEY,
					LINES_NAME_KEY,
					LINES_NAME_EN_KEY,
					LINES_COLOR_KEY}, LINES_ROW_ID + " = " + rowIndex, null, null, null, null);
		if(res != null){
			res.moveToFirst();
		}
		return res;
	}

	// -------------- STATIONS HELPERS ------------------
	public long addstations(Integer id_station, Integer id_line, String name, String name_en, Float lat, Float lon)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(STATIONS_ID_STATION_KEY, id_station);
		contentValues.put(STATIONS_ID_LINE_KEY, id_line);
		contentValues.put(STATIONS_NAME_KEY, name);
		contentValues.put(STATIONS_NAME_EN_KEY, name_en);
		contentValues.put(STATIONS_LAT_KEY, lat);
		contentValues.put(STATIONS_LON_KEY, lon);
		return mDb.insert(STATIONS_TABLE, null, contentValues);
	
	}

	public long updatestations(long rowIndex, Integer id_station, Integer id_line, String name, String name_en, Float lat, Float lon)
	{
		String where = STATIONS_ROW_ID + " = " + rowIndex;
		ContentValues contentValues = new ContentValues();
		contentValues.put(STATIONS_ID_STATION_KEY, id_station);
		contentValues.put(STATIONS_ID_LINE_KEY, id_line);
		contentValues.put(STATIONS_NAME_KEY, name);
		contentValues.put(STATIONS_NAME_EN_KEY, name_en);
		contentValues.put(STATIONS_LAT_KEY, lat);
		contentValues.put(STATIONS_LON_KEY, lon);
		return mDb.update(STATIONS_TABLE, contentValues, where, null);
	
	}

	public boolean removestations(Long rowIndex)
	{
		return mDb.delete(STATIONS_TABLE, STATIONS_ROW_ID + " = " + rowIndex, null) > 0;
	}

	public boolean removeAllstations()
	{
		return mDb.delete(STATIONS_TABLE, null, null) > 0;
	}

	public Cursor getAllstations()
	{
		return mDb.query(STATIONS_TABLE, new String[] {
					STATIONS_ROW_ID,
					STATIONS_ID_STATION_KEY,
					STATIONS_ID_LINE_KEY,
					STATIONS_NAME_KEY,
					STATIONS_NAME_EN_KEY,
					STATIONS_LAT_KEY,
					STATIONS_LON_KEY}, null, null, null, null, null);
	}

	public Cursor getstations(long rowIndex)
	{
		Cursor res = mDb.query(STATIONS_TABLE, new String[] {
					STATIONS_ROW_ID,
					STATIONS_ID_STATION_KEY,
					STATIONS_ID_LINE_KEY,
					STATIONS_NAME_KEY,
					STATIONS_NAME_EN_KEY,
					STATIONS_LAT_KEY,
					STATIONS_LON_KEY}, STATIONS_ROW_ID + " = " + rowIndex, null, null, null, null);
		if(res != null){
			res.moveToFirst();
		}
		return res;
	}

	// -------------- PORTALS HELPERS ------------------
	public long addportals(Integer id_entrance, String name, Integer id_station, String direction, Float lat, Float lon, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(PORTALS_ID_ENTRANCE_KEY, id_entrance);
		contentValues.put(PORTALS_NAME_KEY, name);
		contentValues.put(PORTALS_ID_STATION_KEY, id_station);
		contentValues.put(PORTALS_DIRECTION_KEY, direction);
		contentValues.put(PORTALS_LAT_KEY, lat);
		contentValues.put(PORTALS_LON_KEY, lon);
		contentValues.put(PORTALS_MAX_WIDTH_KEY, max_width);
		contentValues.put(PORTALS_MIN_STEP_KEY, min_step);
		contentValues.put(PORTALS_MIN_STEP_RAMP_KEY, min_step_ramp);
		contentValues.put(PORTALS_LIFT_KEY, lift);
		contentValues.put(PORTALS_LIFT_MINUS_STEP_KEY, lift_minus_step);
		contentValues.put(PORTALS_MIN_RAIL_WIDTH_KEY, min_rail_width);
		contentValues.put(PORTALS_MAX_RAIL_WIDTH_KEY, max_rail_width);
		contentValues.put(PORTALS_MAX_ANGLE_KEY, max_angle);
		return mDb.insert(PORTALS_TABLE, null, contentValues);
	
	}

	public long updateportals(long rowIndex, Integer id_entrance, String name, Integer id_station, String direction, Float lat, Float lon, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle)
	{
		String where = PORTALS_ROW_ID + " = " + rowIndex;
		ContentValues contentValues = new ContentValues();
		contentValues.put(PORTALS_ID_ENTRANCE_KEY, id_entrance);
		contentValues.put(PORTALS_NAME_KEY, name);
		contentValues.put(PORTALS_ID_STATION_KEY, id_station);
		contentValues.put(PORTALS_DIRECTION_KEY, direction);
		contentValues.put(PORTALS_LAT_KEY, lat);
		contentValues.put(PORTALS_LON_KEY, lon);
		contentValues.put(PORTALS_MAX_WIDTH_KEY, max_width);
		contentValues.put(PORTALS_MIN_STEP_KEY, min_step);
		contentValues.put(PORTALS_MIN_STEP_RAMP_KEY, min_step_ramp);
		contentValues.put(PORTALS_LIFT_KEY, lift);
		contentValues.put(PORTALS_LIFT_MINUS_STEP_KEY, lift_minus_step);
		contentValues.put(PORTALS_MIN_RAIL_WIDTH_KEY, min_rail_width);
		contentValues.put(PORTALS_MAX_RAIL_WIDTH_KEY, max_rail_width);
		contentValues.put(PORTALS_MAX_ANGLE_KEY, max_angle);
		return mDb.update(PORTALS_TABLE, contentValues, where, null);
	
	}

	public boolean removeportals(Long rowIndex)
	{
		return mDb.delete(PORTALS_TABLE, PORTALS_ROW_ID + " = " + rowIndex, null) > 0;
	}

	public boolean removeAllportals()
	{
		return mDb.delete(PORTALS_TABLE, null, null) > 0;
	}

	public Cursor getAllportals()
	{
		return mDb.query(PORTALS_TABLE, new String[] {
					PORTALS_ROW_ID,
					PORTALS_ID_ENTRANCE_KEY,
					PORTALS_NAME_KEY,
					PORTALS_ID_STATION_KEY,
					PORTALS_DIRECTION_KEY,
					PORTALS_LAT_KEY,
					PORTALS_LON_KEY,
					PORTALS_MAX_WIDTH_KEY,
					PORTALS_MIN_STEP_KEY,
					PORTALS_MIN_STEP_RAMP_KEY,
					PORTALS_LIFT_KEY,
					PORTALS_LIFT_MINUS_STEP_KEY,
					PORTALS_MIN_RAIL_WIDTH_KEY,
					PORTALS_MAX_RAIL_WIDTH_KEY,
					PORTALS_MAX_ANGLE_KEY}, null, null, null, null, null);
	}

	public Cursor getportals(long rowIndex)
	{
		Cursor res = mDb.query(PORTALS_TABLE, new String[] {
					PORTALS_ROW_ID,
					PORTALS_ID_ENTRANCE_KEY,
					PORTALS_NAME_KEY,
					PORTALS_ID_STATION_KEY,
					PORTALS_DIRECTION_KEY,
					PORTALS_LAT_KEY,
					PORTALS_LON_KEY,
					PORTALS_MAX_WIDTH_KEY,
					PORTALS_MIN_STEP_KEY,
					PORTALS_MIN_STEP_RAMP_KEY,
					PORTALS_LIFT_KEY,
					PORTALS_LIFT_MINUS_STEP_KEY,
					PORTALS_MIN_RAIL_WIDTH_KEY,
					PORTALS_MAX_RAIL_WIDTH_KEY,
					PORTALS_MAX_ANGLE_KEY}, PORTALS_ROW_ID + " = " + rowIndex, null, null, null, null);
		if(res != null){
			res.moveToFirst();
		}
		return res;
	}

	// -------------- GRAPH HELPERS ------------------
	public long addgraph(Integer id_from, Integer id_to, String name_from, String name_to, Integer cost)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(GRAPH_ID_FROM_KEY, id_from);
		contentValues.put(GRAPH_ID_TO_KEY, id_to);
		contentValues.put(GRAPH_NAME_FROM_KEY, name_from);
		contentValues.put(GRAPH_NAME_TO_KEY, name_to);
		contentValues.put(GRAPH_COST_KEY, cost);
		return mDb.insert(GRAPH_TABLE, null, contentValues);
	
	}

	public long updategraph(long rowIndex, Integer id_from, Integer id_to, String name_from, String name_to, Integer cost)
	{
		String where = GRAPH_ROW_ID + " = " + rowIndex;
		ContentValues contentValues = new ContentValues();
		contentValues.put(GRAPH_ID_FROM_KEY, id_from);
		contentValues.put(GRAPH_ID_TO_KEY, id_to);
		contentValues.put(GRAPH_NAME_FROM_KEY, name_from);
		contentValues.put(GRAPH_NAME_TO_KEY, name_to);
		contentValues.put(GRAPH_COST_KEY, cost);
		return mDb.update(GRAPH_TABLE, contentValues, where, null);
	
	}

	public boolean removegraph(Long rowIndex)
	{
		return mDb.delete(GRAPH_TABLE, GRAPH_ROW_ID + " = " + rowIndex, null) > 0;
	}

	public boolean removeAllgraph()
	{
		return mDb.delete(GRAPH_TABLE, null, null) > 0;
	}

	public Cursor getAllgraph()
	{
		return mDb.query(GRAPH_TABLE, new String[] {
					GRAPH_ROW_ID,
					GRAPH_ID_FROM_KEY,
					GRAPH_ID_TO_KEY,
					GRAPH_NAME_FROM_KEY,
					GRAPH_NAME_TO_KEY,
					GRAPH_COST_KEY}, null, null, null, null, null);
	}

	public Cursor getgraph(long rowIndex)
	{
		Cursor res = mDb.query(GRAPH_TABLE, new String[] {
					GRAPH_ROW_ID,
					GRAPH_ID_FROM_KEY,
					GRAPH_ID_TO_KEY,
					GRAPH_NAME_FROM_KEY,
					GRAPH_NAME_TO_KEY,
					GRAPH_COST_KEY}, GRAPH_ROW_ID + " = " + rowIndex, null, null, null, null);
		if(res != null){
			res.moveToFirst();
		}
		return res;
	}

	// -------------- INTERCHANGES HELPERS ------------------
	public long addinterchanges(String station_from, String station_to, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(INTERCHANGES_STATION_FROM_KEY, station_from);
		contentValues.put(INTERCHANGES_STATION_TO_KEY, station_to);
		contentValues.put(INTERCHANGES_MAX_WIDTH_KEY, max_width);
		contentValues.put(INTERCHANGES_MIN_STEP_KEY, min_step);
		contentValues.put(INTERCHANGES_MIN_STEP_RAMP_KEY, min_step_ramp);
		contentValues.put(INTERCHANGES_LIFT_KEY, lift);
		contentValues.put(INTERCHANGES_LIFT_MINUS_STEP_KEY, lift_minus_step);
		contentValues.put(INTERCHANGES_MIN_RAIL_WIDTH_KEY, min_rail_width);
		contentValues.put(INTERCHANGES_MAX_RAIL_WIDTH_KEY, max_rail_width);
		contentValues.put(INTERCHANGES_MAX_ANGLE_KEY, max_angle);
		return mDb.insert(INTERCHANGES_TABLE, null, contentValues);
	
	}

	public long updateinterchanges(long rowIndex, String station_from, String station_to, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle)
	{
		String where = INTERCHANGES_ROW_ID + " = " + rowIndex;
		ContentValues contentValues = new ContentValues();
		contentValues.put(INTERCHANGES_STATION_FROM_KEY, station_from);
		contentValues.put(INTERCHANGES_STATION_TO_KEY, station_to);
		contentValues.put(INTERCHANGES_MAX_WIDTH_KEY, max_width);
		contentValues.put(INTERCHANGES_MIN_STEP_KEY, min_step);
		contentValues.put(INTERCHANGES_MIN_STEP_RAMP_KEY, min_step_ramp);
		contentValues.put(INTERCHANGES_LIFT_KEY, lift);
		contentValues.put(INTERCHANGES_LIFT_MINUS_STEP_KEY, lift_minus_step);
		contentValues.put(INTERCHANGES_MIN_RAIL_WIDTH_KEY, min_rail_width);
		contentValues.put(INTERCHANGES_MAX_RAIL_WIDTH_KEY, max_rail_width);
		contentValues.put(INTERCHANGES_MAX_ANGLE_KEY, max_angle);
		return mDb.update(INTERCHANGES_TABLE, contentValues, where, null);
	
	}

	public boolean removeinterchanges(Long rowIndex)
	{
		return mDb.delete(INTERCHANGES_TABLE, INTERCHANGES_ROW_ID + " = " + rowIndex, null) > 0;
	}

	public boolean removeAllinterchanges()
	{
		return mDb.delete(INTERCHANGES_TABLE, null, null) > 0;
	}

	public Cursor getAllinterchanges()
	{
		return mDb.query(INTERCHANGES_TABLE, new String[] {
					INTERCHANGES_ROW_ID,
					INTERCHANGES_STATION_FROM_KEY,
					INTERCHANGES_STATION_TO_KEY,
					INTERCHANGES_MAX_WIDTH_KEY,
					INTERCHANGES_MIN_STEP_KEY,
					INTERCHANGES_MIN_STEP_RAMP_KEY,
					INTERCHANGES_LIFT_KEY,
					INTERCHANGES_LIFT_MINUS_STEP_KEY,
					INTERCHANGES_MIN_RAIL_WIDTH_KEY,
					INTERCHANGES_MAX_RAIL_WIDTH_KEY,
					INTERCHANGES_MAX_ANGLE_KEY}, null, null, null, null, null);
	}

	public Cursor getinterchanges(long rowIndex)
	{
		Cursor res = mDb.query(INTERCHANGES_TABLE, new String[] {
					INTERCHANGES_ROW_ID,
					INTERCHANGES_STATION_FROM_KEY,
					INTERCHANGES_STATION_TO_KEY,
					INTERCHANGES_MAX_WIDTH_KEY,
					INTERCHANGES_MIN_STEP_KEY,
					INTERCHANGES_MIN_STEP_RAMP_KEY,
					INTERCHANGES_LIFT_KEY,
					INTERCHANGES_LIFT_MINUS_STEP_KEY,
					INTERCHANGES_MIN_RAIL_WIDTH_KEY,
					INTERCHANGES_MAX_RAIL_WIDTH_KEY,
					INTERCHANGES_MAX_ANGLE_KEY}, INTERCHANGES_ROW_ID + " = " + rowIndex, null, null, null, null);
		if(res != null){
			res.moveToFirst();
		}
		return res;
	}




    private static class MyDbHelper extends SQLiteOpenHelper {
    
        public MyDbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // Called when no database exists in disk and the helper class needs
        // to create a new one. 
        @Override
        public void onCreate(SQLiteDatabase db) {      
            db.execSQL(DATABASE_LINES_CREATE);
			db.execSQL(DATABASE_STATIONS_CREATE);
			db.execSQL(DATABASE_PORTALS_CREATE);
			db.execSQL(DATABASE_GRAPH_CREATE);
			db.execSQL(DATABASE_INTERCHANGES_CREATE);
			
        }

        // Called when there is a database version mismatch meaning that the version
        // of the database on disk needs to be upgraded to the current version.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Log the version upgrade.
            Log.w(TAG, "Upgrading from version " + 
                        oldVersion + " to " +
                        newVersion + ", which will destroy all old data");
            
            // Upgrade the existing database to conform to the new version. Multiple 
            // previous versions can be handled by comparing _oldVersion and _newVersion
            // values.

            // The simplest case is to drop the old table and create a new one.
            db.execSQL("DROP TABLE IF EXISTS " + LINES_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + STATIONS_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + PORTALS_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + GRAPH_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + INTERCHANGES_TABLE + ";");
			
            // Create a new one.
            onCreate(db);
        }
    }
     
    /** Dummy object to allow class to compile */
}