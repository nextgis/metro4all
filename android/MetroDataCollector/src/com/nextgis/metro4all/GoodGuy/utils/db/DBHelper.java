/**********************************************************************************************************************************************************************
****** AUTO GENERATED FILE BY ANDROID SQLITE HELPER SCRIPT BY FEDERICO PAOLINELLI. ANY CHANGE WILL BE WIPED OUT IF THE SCRIPT IS PROCESSED AGAIN. *******
**********************************************************************************************************************************************************************/
package com.nextgis.metro4all.GoodGuy.utils.db;

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

    private static final String DATABASE_NAME = "metroaccess.sqlite3";
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

    public static final String ROW_ID = "_id";

    // -------------- LINES DEFINITIONS ------------

    public static final String LINES_TABLE = "lines";
    public static final String LINES_ID_LINE_COLUMN = "id_line";
    public static final int LINES_ID_LINE_COLUMN_POSITION = 1;
    public static final String LINES_NAME_COLUMN = "name";
    public static final int LINES_NAME_COLUMN_POSITION = 2;
    public static final String LINES_NAME_EN_COLUMN = "name_en";
    public static final int LINES_NAME_EN_COLUMN_POSITION = 3;
    public static final String LINES_COLOR_COLUMN = "color";
    public static final int LINES_COLOR_COLUMN_POSITION = 4;




    // -------------- STATIONS DEFINITIONS ------------

    public static final String STATIONS_TABLE = "stations";
    public static final String STATIONS_ID_STATION_COLUMN = "id_station";
    public static final int STATIONS_ID_STATION_COLUMN_POSITION = 1;
    public static final String STATIONS_ID_LINE_COLUMN = "id_line";
    public static final int STATIONS_ID_LINE_COLUMN_POSITION = 2;
    public static final String STATIONS_NAME_COLUMN = "name";
    public static final int STATIONS_NAME_COLUMN_POSITION = 3;
    public static final String STATIONS_NAME_EN_COLUMN = "name_en";
    public static final int STATIONS_NAME_EN_COLUMN_POSITION = 4;
    public static final String STATIONS_LAT_COLUMN = "lat";
    public static final int STATIONS_LAT_COLUMN_POSITION = 5;
    public static final String STATIONS_LON_COLUMN = "lon";
    public static final int STATIONS_LON_COLUMN_POSITION = 6;




    // -------------- PORTALS DEFINITIONS ------------

    public static final String PORTALS_TABLE = "portals";
    public static final String PORTALS_ID_ENTRANCE_COLUMN = "id_entrance";
    public static final int PORTALS_ID_ENTRANCE_COLUMN_POSITION = 1;
    public static final String PORTALS_NAME_COLUMN = "name";
    public static final int PORTALS_NAME_COLUMN_POSITION = 2;
    public static final String PORTALS_ID_STATION_COLUMN = "id_station";
    public static final int PORTALS_ID_STATION_COLUMN_POSITION = 3;
    public static final String PORTALS_DIRECTION_COLUMN = "direction";
    public static final int PORTALS_DIRECTION_COLUMN_POSITION = 4;
    public static final String PORTALS_LAT_COLUMN = "lat";
    public static final int PORTALS_LAT_COLUMN_POSITION = 5;
    public static final String PORTALS_LON_COLUMN = "lon";
    public static final int PORTALS_LON_COLUMN_POSITION = 6;
    public static final String PORTALS_MAX_WIDTH_COLUMN = "max_width";
    public static final int PORTALS_MAX_WIDTH_COLUMN_POSITION = 7;
    public static final String PORTALS_MIN_STEP_COLUMN = "min_step";
    public static final int PORTALS_MIN_STEP_COLUMN_POSITION = 8;
    public static final String PORTALS_MIN_STEP_RAMP_COLUMN = "min_step_ramp";
    public static final int PORTALS_MIN_STEP_RAMP_COLUMN_POSITION = 9;
    public static final String PORTALS_LIFT_COLUMN = "lift";
    public static final int PORTALS_LIFT_COLUMN_POSITION = 10;
    public static final String PORTALS_LIFT_MINUS_STEP_COLUMN = "lift_minus_step";
    public static final int PORTALS_LIFT_MINUS_STEP_COLUMN_POSITION = 11;
    public static final String PORTALS_MIN_RAIL_WIDTH_COLUMN = "min_rail_width";
    public static final int PORTALS_MIN_RAIL_WIDTH_COLUMN_POSITION = 12;
    public static final String PORTALS_MAX_RAIL_WIDTH_COLUMN = "max_rail_width";
    public static final int PORTALS_MAX_RAIL_WIDTH_COLUMN_POSITION = 13;
    public static final String PORTALS_MAX_ANGLE_COLUMN = "max_angle";
    public static final int PORTALS_MAX_ANGLE_COLUMN_POSITION = 14;




    // -------------- GRAPH DEFINITIONS ------------

    public static final String GRAPH_TABLE = "graph";
    public static final String GRAPH_ID_FROM_COLUMN = "id_from";
    public static final int GRAPH_ID_FROM_COLUMN_POSITION = 1;
    public static final String GRAPH_ID_TO_COLUMN = "id_to";
    public static final int GRAPH_ID_TO_COLUMN_POSITION = 2;
    public static final String GRAPH_NAME_FROM_COLUMN = "name_from";
    public static final int GRAPH_NAME_FROM_COLUMN_POSITION = 3;
    public static final String GRAPH_NAME_TO_COLUMN = "name_to";
    public static final int GRAPH_NAME_TO_COLUMN_POSITION = 4;
    public static final String GRAPH_COST_COLUMN = "cost";
    public static final int GRAPH_COST_COLUMN_POSITION = 5;




    // -------------- INTERCHANGES DEFINITIONS ------------

    public static final String INTERCHANGES_TABLE = "interchanges";
    public static final String INTERCHANGES_STATION_FROM_COLUMN = "station_from";
    public static final int INTERCHANGES_STATION_FROM_COLUMN_POSITION = 1;
    public static final String INTERCHANGES_STATION_TO_COLUMN = "station_to";
    public static final int INTERCHANGES_STATION_TO_COLUMN_POSITION = 2;
    public static final String INTERCHANGES_MAX_WIDTH_COLUMN = "max_width";
    public static final int INTERCHANGES_MAX_WIDTH_COLUMN_POSITION = 3;
    public static final String INTERCHANGES_MIN_STEP_COLUMN = "min_step";
    public static final int INTERCHANGES_MIN_STEP_COLUMN_POSITION = 4;
    public static final String INTERCHANGES_MIN_STEP_RAMP_COLUMN = "min_step_ramp";
    public static final int INTERCHANGES_MIN_STEP_RAMP_COLUMN_POSITION = 5;
    public static final String INTERCHANGES_LIFT_COLUMN = "lift";
    public static final int INTERCHANGES_LIFT_COLUMN_POSITION = 6;
    public static final String INTERCHANGES_LIFT_MINUS_STEP_COLUMN = "lift_minus_step";
    public static final int INTERCHANGES_LIFT_MINUS_STEP_COLUMN_POSITION = 7;
    public static final String INTERCHANGES_MIN_RAIL_WIDTH_COLUMN = "min_rail_width";
    public static final int INTERCHANGES_MIN_RAIL_WIDTH_COLUMN_POSITION = 8;
    public static final String INTERCHANGES_MAX_RAIL_WIDTH_COLUMN = "max_rail_width";
    public static final int INTERCHANGES_MAX_RAIL_WIDTH_COLUMN_POSITION = 9;
    public static final String INTERCHANGES_MAX_ANGLE_COLUMN = "max_angle";
    public static final int INTERCHANGES_MAX_ANGLE_COLUMN_POSITION = 10;




    // -------------- CELLDATA DEFINITIONS ------------

    public static final String CELLDATA_TABLE = "celldata";
    public static final String CELLDATA_DATE_COLUMN = "date";
    public static final int CELLDATA_DATE_COLUMN_POSITION = 1;
    public static final String CELLDATA_ID_STATION_COLUMN = "id_station";
    public static final int CELLDATA_ID_STATION_COLUMN_POSITION = 2;
    public static final String CELLDATA_ID_LINE_COLUMN = "id_line";
    public static final int CELLDATA_ID_LINE_COLUMN_POSITION = 3;
    public static final String CELLDATA_CELL_CID_COLUMN = "cell_cid";
    public static final int CELLDATA_CELL_CID_COLUMN_POSITION = 4;
    public static final String CELLDATA_CELL_LAC_COLUMN = "cell_lac";
    public static final int CELLDATA_CELL_LAC_COLUMN_POSITION = 5;
    public static final String CELLDATA_CELL_HASH_COLUMN = "cell_hash";
    public static final int CELLDATA_CELL_HASH_COLUMN_POSITION = 6;
    public static final String CELLDATA_CELL_NAME_COLUMN = "cell_name";
    public static final int CELLDATA_CELL_NAME_COLUMN_POSITION = 7;
    public static final String CELLDATA_CELL_SIGNAL_COLUMN = "cell_signal";
    public static final int CELLDATA_CELL_SIGNAL_COLUMN_POSITION = 8;
    public static final String CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN = "cell_neighbors_count";
    public static final int CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN_POSITION = 9;
    public static final String CELLDATA_CELL_NEIGHBORS_DATA_COLUMN = "cell_neighbors_data";
    public static final int CELLDATA_CELL_NEIGHBORS_DATA_COLUMN_POSITION = 10;
    public static final String CELLDATA_CELL_GEO_LOCATION_COLUMN = "cell_geo_location";
    public static final int CELLDATA_CELL_GEO_LOCATION_COLUMN_POSITION = 11;






    // -------- TABLES CREATION ----------

    // lines CREATION 
    private static final String DATABASE_LINES_CREATE = "create table " + LINES_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 LINES_ID_LINE_COLUMN + " integer  " + ", " + 
				 LINES_NAME_COLUMN + " text  " + ", " + 
				 LINES_NAME_EN_COLUMN + " text  " + ", " + 
				 LINES_COLOR_COLUMN + " text  " + ");";


    // stations CREATION 
    private static final String DATABASE_STATIONS_CREATE = "create table " + STATIONS_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 STATIONS_ID_STATION_COLUMN + " integer  " + ", " + 
				 STATIONS_ID_LINE_COLUMN + " integer  " + ", " + 
				 STATIONS_NAME_COLUMN + " text  " + ", " + 
				 STATIONS_NAME_EN_COLUMN + " text  " + ", " + 
				 STATIONS_LAT_COLUMN + " float  " + ", " + 
				 STATIONS_LON_COLUMN + " float  " + ");";


    // portals CREATION 
    private static final String DATABASE_PORTALS_CREATE = "create table " + PORTALS_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 PORTALS_ID_ENTRANCE_COLUMN + " integer  " + ", " + 
				 PORTALS_NAME_COLUMN + " text  " + ", " + 
				 PORTALS_ID_STATION_COLUMN + " integer  " + ", " + 
				 PORTALS_DIRECTION_COLUMN + " text  " + ", " + 
				 PORTALS_LAT_COLUMN + " float  " + ", " + 
				 PORTALS_LON_COLUMN + " float  " + ", " + 
				 PORTALS_MAX_WIDTH_COLUMN + " integer  " + ", " + 
				 PORTALS_MIN_STEP_COLUMN + " integer  " + ", " + 
				 PORTALS_MIN_STEP_RAMP_COLUMN + " integer  " + ", " + 
				 PORTALS_LIFT_COLUMN + " integer  " + ", " + 
				 PORTALS_LIFT_MINUS_STEP_COLUMN + " integer  " + ", " + 
				 PORTALS_MIN_RAIL_WIDTH_COLUMN + " integer  " + ", " + 
				 PORTALS_MAX_RAIL_WIDTH_COLUMN + " integer  " + ", " + 
				 PORTALS_MAX_ANGLE_COLUMN + " integer  " + ");";


    // graph CREATION 
    private static final String DATABASE_GRAPH_CREATE = "create table " + GRAPH_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 GRAPH_ID_FROM_COLUMN + " integer  " + ", " + 
				 GRAPH_ID_TO_COLUMN + " integer  " + ", " + 
				 GRAPH_NAME_FROM_COLUMN + " text  " + ", " + 
				 GRAPH_NAME_TO_COLUMN + " text  " + ", " + 
				 GRAPH_COST_COLUMN + " integer  " + ");";


    // interchanges CREATION 
    private static final String DATABASE_INTERCHANGES_CREATE = "create table " + INTERCHANGES_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 INTERCHANGES_STATION_FROM_COLUMN + " text  " + ", " + 
				 INTERCHANGES_STATION_TO_COLUMN + " text  " + ", " + 
				 INTERCHANGES_MAX_WIDTH_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_MIN_STEP_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_MIN_STEP_RAMP_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_LIFT_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_LIFT_MINUS_STEP_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_MIN_RAIL_WIDTH_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_MAX_RAIL_WIDTH_COLUMN + " integer  " + ", " + 
				 INTERCHANGES_MAX_ANGLE_COLUMN + " integer  " + ");";


    // celldata CREATION 
    private static final String DATABASE_CELLDATA_CREATE = "create table " + CELLDATA_TABLE + " (" + 
				 ROW_ID + " integer primary key autoincrement" + ", " + 
				 CELLDATA_DATE_COLUMN + " integer  " + ", " + 
				 CELLDATA_ID_STATION_COLUMN + " integer  " + ", " + 
				 CELLDATA_ID_LINE_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_CID_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_LAC_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_HASH_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_NAME_COLUMN + " text  " + ", " + 
				 CELLDATA_CELL_SIGNAL_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN + " integer  " + ", " + 
				 CELLDATA_CELL_NEIGHBORS_DATA_COLUMN + " text  " + ", " + 
				 CELLDATA_CELL_GEO_LOCATION_COLUMN + " text  " + ");";




	// -------------- LINES HELPERS ------------------
    public long addlines(Integer id_line, String name, String name_en, String color){
     ContentValues contentValues = new ContentValues();
       contentValues.put(LINES_ID_LINE_COLUMN, id_line);
       contentValues.put(LINES_NAME_COLUMN, name);
       contentValues.put(LINES_NAME_EN_COLUMN, name_en);
       contentValues.put(LINES_COLOR_COLUMN, color);
       return mDb.insert(LINES_TABLE, null, contentValues);
    
    }

    public long updatelines(long rowIndex, Integer id_line, String name, String name_en, String color){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(LINES_ID_LINE_COLUMN, id_line);
       contentValues.put(LINES_NAME_COLUMN, name);
       contentValues.put(LINES_NAME_EN_COLUMN, name_en);
       contentValues.put(LINES_COLOR_COLUMN, color);
       return mDb.update(LINES_TABLE, contentValues, where, null);
    
    }

    public boolean removelines(long rowIndex){
       return mDb.delete(LINES_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAlllines(){
       return mDb.delete(LINES_TABLE, null, null) > 0;
    }

    public Cursor getAlllines(){
    	return mDb.query(LINES_TABLE, new String[] {
                  ROW_ID,
                    LINES_ID_LINE_COLUMN,
                    LINES_NAME_COLUMN,
                    LINES_NAME_EN_COLUMN,
                    LINES_COLOR_COLUMN}, null, null, null, null, null);
    }

    public Cursor getlines(long rowIndex){
    	Cursor res = mDb.query(LINES_TABLE, new String[] {
                  ROW_ID,
                    LINES_ID_LINE_COLUMN,
                    LINES_NAME_COLUMN,
                    LINES_NAME_EN_COLUMN,
                    LINES_COLOR_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
    	if(res != null){
    		res.moveToFirst();
    	}
    	return res;
    }

	// -------------- STATIONS HELPERS ------------------
    public long addstations(Integer id_station, Integer id_line, String name, String name_en, Float lat, Float lon){
     ContentValues contentValues = new ContentValues();
       contentValues.put(STATIONS_ID_STATION_COLUMN, id_station);
       contentValues.put(STATIONS_ID_LINE_COLUMN, id_line);
       contentValues.put(STATIONS_NAME_COLUMN, name);
       contentValues.put(STATIONS_NAME_EN_COLUMN, name_en);
       contentValues.put(STATIONS_LAT_COLUMN, lat);
       contentValues.put(STATIONS_LON_COLUMN, lon);
       return mDb.insert(STATIONS_TABLE, null, contentValues);
    
    }

    public long updatestations(long rowIndex, Integer id_station, Integer id_line, String name, String name_en, Float lat, Float lon){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(STATIONS_ID_STATION_COLUMN, id_station);
       contentValues.put(STATIONS_ID_LINE_COLUMN, id_line);
       contentValues.put(STATIONS_NAME_COLUMN, name);
       contentValues.put(STATIONS_NAME_EN_COLUMN, name_en);
       contentValues.put(STATIONS_LAT_COLUMN, lat);
       contentValues.put(STATIONS_LON_COLUMN, lon);
       return mDb.update(STATIONS_TABLE, contentValues, where, null);
    
    }

    public boolean removestations(long rowIndex){
       return mDb.delete(STATIONS_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllstations(){
       return mDb.delete(STATIONS_TABLE, null, null) > 0;
    }

    public Cursor getAllstations(){
    	return mDb.query(STATIONS_TABLE, new String[] {
                  ROW_ID,
                    STATIONS_ID_STATION_COLUMN,
                    STATIONS_ID_LINE_COLUMN,
                    STATIONS_NAME_COLUMN,
                    STATIONS_NAME_EN_COLUMN,
                    STATIONS_LAT_COLUMN,
                    STATIONS_LON_COLUMN}, null, null, null, null, null);
    }

    public Cursor getstations(long rowIndex){
    	Cursor res = mDb.query(STATIONS_TABLE, new String[] {
                  ROW_ID,
                    STATIONS_ID_STATION_COLUMN,
                    STATIONS_ID_LINE_COLUMN,
                    STATIONS_NAME_COLUMN,
                    STATIONS_NAME_EN_COLUMN,
                    STATIONS_LAT_COLUMN,
                    STATIONS_LON_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
    	if(res != null){
    		res.moveToFirst();
    	}
    	return res;
    }

	// -------------- PORTALS HELPERS ------------------
    public long addportals(Integer id_entrance, String name, Integer id_station, String direction, Float lat, Float lon, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle){
     ContentValues contentValues = new ContentValues();
       contentValues.put(PORTALS_ID_ENTRANCE_COLUMN, id_entrance);
       contentValues.put(PORTALS_NAME_COLUMN, name);
       contentValues.put(PORTALS_ID_STATION_COLUMN, id_station);
       contentValues.put(PORTALS_DIRECTION_COLUMN, direction);
       contentValues.put(PORTALS_LAT_COLUMN, lat);
       contentValues.put(PORTALS_LON_COLUMN, lon);
       contentValues.put(PORTALS_MAX_WIDTH_COLUMN, max_width);
       contentValues.put(PORTALS_MIN_STEP_COLUMN, min_step);
       contentValues.put(PORTALS_MIN_STEP_RAMP_COLUMN, min_step_ramp);
       contentValues.put(PORTALS_LIFT_COLUMN, lift);
       contentValues.put(PORTALS_LIFT_MINUS_STEP_COLUMN, lift_minus_step);
       contentValues.put(PORTALS_MIN_RAIL_WIDTH_COLUMN, min_rail_width);
       contentValues.put(PORTALS_MAX_RAIL_WIDTH_COLUMN, max_rail_width);
       contentValues.put(PORTALS_MAX_ANGLE_COLUMN, max_angle);
       return mDb.insert(PORTALS_TABLE, null, contentValues);
    
    }

    public long updateportals(long rowIndex, Integer id_entrance, String name, Integer id_station, String direction, Float lat, Float lon, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(PORTALS_ID_ENTRANCE_COLUMN, id_entrance);
       contentValues.put(PORTALS_NAME_COLUMN, name);
       contentValues.put(PORTALS_ID_STATION_COLUMN, id_station);
       contentValues.put(PORTALS_DIRECTION_COLUMN, direction);
       contentValues.put(PORTALS_LAT_COLUMN, lat);
       contentValues.put(PORTALS_LON_COLUMN, lon);
       contentValues.put(PORTALS_MAX_WIDTH_COLUMN, max_width);
       contentValues.put(PORTALS_MIN_STEP_COLUMN, min_step);
       contentValues.put(PORTALS_MIN_STEP_RAMP_COLUMN, min_step_ramp);
       contentValues.put(PORTALS_LIFT_COLUMN, lift);
       contentValues.put(PORTALS_LIFT_MINUS_STEP_COLUMN, lift_minus_step);
       contentValues.put(PORTALS_MIN_RAIL_WIDTH_COLUMN, min_rail_width);
       contentValues.put(PORTALS_MAX_RAIL_WIDTH_COLUMN, max_rail_width);
       contentValues.put(PORTALS_MAX_ANGLE_COLUMN, max_angle);
       return mDb.update(PORTALS_TABLE, contentValues, where, null);
    
    }

    public boolean removeportals(long rowIndex){
       return mDb.delete(PORTALS_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllportals(){
       return mDb.delete(PORTALS_TABLE, null, null) > 0;
    }

    public Cursor getAllportals(){
    	return mDb.query(PORTALS_TABLE, new String[] {
                  ROW_ID,
                    PORTALS_ID_ENTRANCE_COLUMN,
                    PORTALS_NAME_COLUMN,
                    PORTALS_ID_STATION_COLUMN,
                    PORTALS_DIRECTION_COLUMN,
                    PORTALS_LAT_COLUMN,
                    PORTALS_LON_COLUMN,
                    PORTALS_MAX_WIDTH_COLUMN,
                    PORTALS_MIN_STEP_COLUMN,
                    PORTALS_MIN_STEP_RAMP_COLUMN,
                    PORTALS_LIFT_COLUMN,
                    PORTALS_LIFT_MINUS_STEP_COLUMN,
                    PORTALS_MIN_RAIL_WIDTH_COLUMN,
                    PORTALS_MAX_RAIL_WIDTH_COLUMN,
                    PORTALS_MAX_ANGLE_COLUMN}, null, null, null, null, null);
    }

    public Cursor getportals(long rowIndex){
    	Cursor res = mDb.query(PORTALS_TABLE, new String[] {
                  ROW_ID,
                    PORTALS_ID_ENTRANCE_COLUMN,
                    PORTALS_NAME_COLUMN,
                    PORTALS_ID_STATION_COLUMN,
                    PORTALS_DIRECTION_COLUMN,
                    PORTALS_LAT_COLUMN,
                    PORTALS_LON_COLUMN,
                    PORTALS_MAX_WIDTH_COLUMN,
                    PORTALS_MIN_STEP_COLUMN,
                    PORTALS_MIN_STEP_RAMP_COLUMN,
                    PORTALS_LIFT_COLUMN,
                    PORTALS_LIFT_MINUS_STEP_COLUMN,
                    PORTALS_MIN_RAIL_WIDTH_COLUMN,
                    PORTALS_MAX_RAIL_WIDTH_COLUMN,
                    PORTALS_MAX_ANGLE_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
    	if(res != null){
    		res.moveToFirst();
    	}
    	return res;
    }

	// -------------- GRAPH HELPERS ------------------
    public long addgraph(Integer id_from, Integer id_to, String name_from, String name_to, Integer cost){
     ContentValues contentValues = new ContentValues();
       contentValues.put(GRAPH_ID_FROM_COLUMN, id_from);
       contentValues.put(GRAPH_ID_TO_COLUMN, id_to);
       contentValues.put(GRAPH_NAME_FROM_COLUMN, name_from);
       contentValues.put(GRAPH_NAME_TO_COLUMN, name_to);
       contentValues.put(GRAPH_COST_COLUMN, cost);
       return mDb.insert(GRAPH_TABLE, null, contentValues);
    
    }

    public long updategraph(long rowIndex, Integer id_from, Integer id_to, String name_from, String name_to, Integer cost){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(GRAPH_ID_FROM_COLUMN, id_from);
       contentValues.put(GRAPH_ID_TO_COLUMN, id_to);
       contentValues.put(GRAPH_NAME_FROM_COLUMN, name_from);
       contentValues.put(GRAPH_NAME_TO_COLUMN, name_to);
       contentValues.put(GRAPH_COST_COLUMN, cost);
       return mDb.update(GRAPH_TABLE, contentValues, where, null);
    
    }

    public boolean removegraph(long rowIndex){
       return mDb.delete(GRAPH_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllgraph(){
       return mDb.delete(GRAPH_TABLE, null, null) > 0;
    }

    public Cursor getAllgraph(){
    	return mDb.query(GRAPH_TABLE, new String[] {
                  ROW_ID,
                    GRAPH_ID_FROM_COLUMN,
                    GRAPH_ID_TO_COLUMN,
                    GRAPH_NAME_FROM_COLUMN,
                    GRAPH_NAME_TO_COLUMN,
                    GRAPH_COST_COLUMN}, null, null, null, null, null);
    }

    public Cursor getgraph(long rowIndex){
    	Cursor res = mDb.query(GRAPH_TABLE, new String[] {
                  ROW_ID,
                    GRAPH_ID_FROM_COLUMN,
                    GRAPH_ID_TO_COLUMN,
                    GRAPH_NAME_FROM_COLUMN,
                    GRAPH_NAME_TO_COLUMN,
                    GRAPH_COST_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
    	if(res != null){
    		res.moveToFirst();
    	}
    	return res;
    }

	// -------------- INTERCHANGES HELPERS ------------------
    public long addinterchanges(String station_from, String station_to, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle){
     ContentValues contentValues = new ContentValues();
       contentValues.put(INTERCHANGES_STATION_FROM_COLUMN, station_from);
       contentValues.put(INTERCHANGES_STATION_TO_COLUMN, station_to);
       contentValues.put(INTERCHANGES_MAX_WIDTH_COLUMN, max_width);
       contentValues.put(INTERCHANGES_MIN_STEP_COLUMN, min_step);
       contentValues.put(INTERCHANGES_MIN_STEP_RAMP_COLUMN, min_step_ramp);
       contentValues.put(INTERCHANGES_LIFT_COLUMN, lift);
       contentValues.put(INTERCHANGES_LIFT_MINUS_STEP_COLUMN, lift_minus_step);
       contentValues.put(INTERCHANGES_MIN_RAIL_WIDTH_COLUMN, min_rail_width);
       contentValues.put(INTERCHANGES_MAX_RAIL_WIDTH_COLUMN, max_rail_width);
       contentValues.put(INTERCHANGES_MAX_ANGLE_COLUMN, max_angle);
       return mDb.insert(INTERCHANGES_TABLE, null, contentValues);
    
    }

    public long updateinterchanges(long rowIndex, String station_from, String station_to, Integer max_width, Integer min_step, Integer min_step_ramp, Integer lift, Integer lift_minus_step, Integer min_rail_width, Integer max_rail_width, Integer max_angle){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(INTERCHANGES_STATION_FROM_COLUMN, station_from);
       contentValues.put(INTERCHANGES_STATION_TO_COLUMN, station_to);
       contentValues.put(INTERCHANGES_MAX_WIDTH_COLUMN, max_width);
       contentValues.put(INTERCHANGES_MIN_STEP_COLUMN, min_step);
       contentValues.put(INTERCHANGES_MIN_STEP_RAMP_COLUMN, min_step_ramp);
       contentValues.put(INTERCHANGES_LIFT_COLUMN, lift);
       contentValues.put(INTERCHANGES_LIFT_MINUS_STEP_COLUMN, lift_minus_step);
       contentValues.put(INTERCHANGES_MIN_RAIL_WIDTH_COLUMN, min_rail_width);
       contentValues.put(INTERCHANGES_MAX_RAIL_WIDTH_COLUMN, max_rail_width);
       contentValues.put(INTERCHANGES_MAX_ANGLE_COLUMN, max_angle);
       return mDb.update(INTERCHANGES_TABLE, contentValues, where, null);
    
    }

    public boolean removeinterchanges(long rowIndex){
       return mDb.delete(INTERCHANGES_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllinterchanges(){
       return mDb.delete(INTERCHANGES_TABLE, null, null) > 0;
    }

    public Cursor getAllinterchanges(){
    	return mDb.query(INTERCHANGES_TABLE, new String[] {
                  ROW_ID,
                    INTERCHANGES_STATION_FROM_COLUMN,
                    INTERCHANGES_STATION_TO_COLUMN,
                    INTERCHANGES_MAX_WIDTH_COLUMN,
                    INTERCHANGES_MIN_STEP_COLUMN,
                    INTERCHANGES_MIN_STEP_RAMP_COLUMN,
                    INTERCHANGES_LIFT_COLUMN,
                    INTERCHANGES_LIFT_MINUS_STEP_COLUMN,
                    INTERCHANGES_MIN_RAIL_WIDTH_COLUMN,
                    INTERCHANGES_MAX_RAIL_WIDTH_COLUMN,
                    INTERCHANGES_MAX_ANGLE_COLUMN}, null, null, null, null, null);
    }

    public Cursor getinterchanges(long rowIndex){
    	Cursor res = mDb.query(INTERCHANGES_TABLE, new String[] {
                  ROW_ID,
                    INTERCHANGES_STATION_FROM_COLUMN,
                    INTERCHANGES_STATION_TO_COLUMN,
                    INTERCHANGES_MAX_WIDTH_COLUMN,
                    INTERCHANGES_MIN_STEP_COLUMN,
                    INTERCHANGES_MIN_STEP_RAMP_COLUMN,
                    INTERCHANGES_LIFT_COLUMN,
                    INTERCHANGES_LIFT_MINUS_STEP_COLUMN,
                    INTERCHANGES_MIN_RAIL_WIDTH_COLUMN,
                    INTERCHANGES_MAX_RAIL_WIDTH_COLUMN,
                    INTERCHANGES_MAX_ANGLE_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
    	if(res != null){
    		res.moveToFirst();
    	}
    	return res;
    }

	// -------------- CELLDATA HELPERS ------------------
    public long addcelldata(Date date, Integer id_station, Integer id_line, Integer cell_cid, Integer cell_lac, Integer cell_hash, String cell_name, Integer cell_signal, Integer cell_neighbors_count, String cell_neighbors_data, String cell_geo_location){
     ContentValues contentValues = new ContentValues();
       contentValues.put(CELLDATA_DATE_COLUMN, date.getTime());
       contentValues.put(CELLDATA_ID_STATION_COLUMN, id_station);
       contentValues.put(CELLDATA_ID_LINE_COLUMN, id_line);
       contentValues.put(CELLDATA_CELL_CID_COLUMN, cell_cid);
       contentValues.put(CELLDATA_CELL_LAC_COLUMN, cell_lac);
       contentValues.put(CELLDATA_CELL_HASH_COLUMN, cell_hash);
       contentValues.put(CELLDATA_CELL_NAME_COLUMN, cell_name);
       contentValues.put(CELLDATA_CELL_SIGNAL_COLUMN, cell_signal);
       contentValues.put(CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN, cell_neighbors_count);
       contentValues.put(CELLDATA_CELL_NEIGHBORS_DATA_COLUMN, cell_neighbors_data);
       contentValues.put(CELLDATA_CELL_GEO_LOCATION_COLUMN, cell_geo_location);
       return mDb.insert(CELLDATA_TABLE, null, contentValues);
    
    }

    public long updatecelldata(long rowIndex, Date date, Integer id_station, Integer id_line, Integer cell_cid, Integer cell_lac, Integer cell_hash, String cell_name, Integer cell_signal, Integer cell_neighbors_count, String cell_neighbors_data, String cell_geo_location){
       String where = ROW_ID + " = " + rowIndex;
     ContentValues contentValues = new ContentValues();
       contentValues.put(CELLDATA_DATE_COLUMN, date.getTime());
       contentValues.put(CELLDATA_ID_STATION_COLUMN, id_station);
       contentValues.put(CELLDATA_ID_LINE_COLUMN, id_line);
       contentValues.put(CELLDATA_CELL_CID_COLUMN, cell_cid);
       contentValues.put(CELLDATA_CELL_LAC_COLUMN, cell_lac);
       contentValues.put(CELLDATA_CELL_HASH_COLUMN, cell_hash);
       contentValues.put(CELLDATA_CELL_NAME_COLUMN, cell_name);
       contentValues.put(CELLDATA_CELL_SIGNAL_COLUMN, cell_signal);
       contentValues.put(CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN, cell_neighbors_count);
       contentValues.put(CELLDATA_CELL_NEIGHBORS_DATA_COLUMN, cell_neighbors_data);
       contentValues.put(CELLDATA_CELL_GEO_LOCATION_COLUMN, cell_geo_location);
       return mDb.update(CELLDATA_TABLE, contentValues, where, null);
    
    }

    public boolean removecelldata(long rowIndex){
       return mDb.delete(CELLDATA_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllcelldata(){
       return mDb.delete(CELLDATA_TABLE, null, null) > 0;
    }

    public Cursor getAllcelldata(){
    	return mDb.query(CELLDATA_TABLE, new String[] {
                  ROW_ID,
                    CELLDATA_DATE_COLUMN,
                    CELLDATA_ID_STATION_COLUMN,
                    CELLDATA_ID_LINE_COLUMN,
                    CELLDATA_CELL_CID_COLUMN,
                    CELLDATA_CELL_LAC_COLUMN,
                    CELLDATA_CELL_HASH_COLUMN,
                    CELLDATA_CELL_NAME_COLUMN,
                    CELLDATA_CELL_SIGNAL_COLUMN,
                    CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN,
                    CELLDATA_CELL_NEIGHBORS_DATA_COLUMN,
                    CELLDATA_CELL_GEO_LOCATION_COLUMN}, null, null, null, null, null);
    }

    public Cursor getcelldata(long rowIndex){
    	Cursor res = mDb.query(CELLDATA_TABLE, new String[] {
                  ROW_ID,
                    CELLDATA_DATE_COLUMN,
                    CELLDATA_ID_STATION_COLUMN,
                    CELLDATA_ID_LINE_COLUMN,
                    CELLDATA_CELL_CID_COLUMN,
                    CELLDATA_CELL_LAC_COLUMN,
                    CELLDATA_CELL_HASH_COLUMN,
                    CELLDATA_CELL_NAME_COLUMN,
                    CELLDATA_CELL_SIGNAL_COLUMN,
                    CELLDATA_CELL_NEIGHBORS_COUNT_COLUMN,
                    CELLDATA_CELL_NEIGHBORS_DATA_COLUMN,
                    CELLDATA_CELL_GEO_LOCATION_COLUMN}, ROW_ID + " = " + rowIndex, null, null, null, null);
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
            //db.execSQL(DATABASE_LINES_CREATE);
			//db.execSQL(DATABASE_STATIONS_CREATE);
			//db.execSQL(DATABASE_PORTALS_CREATE);
			//db.execSQL(DATABASE_GRAPH_CREATE);
			//db.execSQL(DATABASE_INTERCHANGES_CREATE);
			db.execSQL(DATABASE_CELLDATA_CREATE);
			
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
//            db.execSQL("DROP TABLE IF EXISTS " + LINES_TABLE + ";");
			//db.execSQL("DROP TABLE IF EXISTS " + STATIONS_TABLE + ";");
			//db.execSQL("DROP TABLE IF EXISTS " + PORTALS_TABLE + ";");
			//db.execSQL("DROP TABLE IF EXISTS " + GRAPH_TABLE + ";");
			//db.execSQL("DROP TABLE IF EXISTS " + INTERCHANGES_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + CELLDATA_TABLE + ";");
			
            // Create a new one.
            onCreate(db);
        }
    }
     
    /** Dummy object to allow class to compile */
}
