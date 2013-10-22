package com.nextgis.metro4all.GoodGuy.utils;

import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;
import com.nextgis.metro4all.GoodGuy.utils.db.DBWrapper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SubLineAdapter extends CursorAdapter {

	public SubLineAdapter(Context context, DBWrapper db) {
		super(context, db.getAlllines(), android.support.v4.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}
	
	@Override
	public void bindView(View itemView, Context context, Cursor cursor) {
		TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
		text1.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.LINES_NAME_COLUMN)));
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		return itemView;
	}

}
