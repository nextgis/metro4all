package com.nextgis.metro4all.GoodGuy.utils.db;

import android.content.Context;
import android.database.Cursor;

public class DBWrapper extends DBHelper {

	public DBWrapper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Cursor getLineByLineId(int id_line) {
		Cursor res = mDb.query(LINES_TABLE, new String[] {
				LINES_ROW_ID,
				LINES_ID_LINE_KEY,
				LINES_NAME_KEY,
				LINES_NAME_EN_KEY,
				LINES_COLOR_KEY}, LINES_ID_LINE_KEY + " = " + id_line, null, null, null, null);
	if(res != null){
		res.moveToFirst();
	}
	return res;
	}

}
