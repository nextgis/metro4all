package com.nextgis.metro4all.GoodGuy.utils.db;

import android.content.Context;
import android.database.Cursor;

/**
 * Так как класс DBHelper генерируется автоматически из скрипта в нем как
 * правило нехватает нужных функций, таких, как например поиск строки в таблице
 * по полю. Данный класс как раз предназначен для того, чтобы во-первых добавить
 * нужные функции, а во вторых - связать объекты с хранилищем.
 * 
 * @author valetin
 * 
 */
public class DBWrapper extends DBHelper {

	public DBWrapper(Context context) {
		super(context);
	}

	public Cursor getLineByLineId(int id_line) {
		Cursor res = mDb.query(LINES_TABLE, new String[] { ROW_ID,
				LINES_ID_LINE_COLUMN, LINES_NAME_COLUMN, LINES_NAME_EN_COLUMN,
				LINES_COLOR_COLUMN }, LINES_ID_LINE_COLUMN + " = " + id_line,
				null, null, null, null);
		if (res != null) {
			res.moveToFirst();
		}
		return res;
	}

	public Cursor getStationsyLineId(int id_line) {
		Cursor res = mDb.query(STATIONS_TABLE, new String[] { ROW_ID,
				STATIONS_ID_STATION_COLUMN, STATIONS_ID_LINE_COLUMN,
				STATIONS_NAME_COLUMN, STATIONS_NAME_EN_COLUMN,
				STATIONS_LAT_COLUMN, STATIONS_LON_COLUMN },
				STATIONS_ID_LINE_COLUMN + " = " + id_line, null, null, null,
				null);
		if (res != null) {
			res.moveToFirst();
		}
		return res;
	}

}
