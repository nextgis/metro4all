package com.nextgis.metro4all.GoodGuy.utils;

import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;
import com.nextgis.metro4all.GoodGuy.utils.db.DBWrapper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SubStationAdapter extends CursorAdapter implements
		SpinnerAdapter {

	public SubStationAdapter(Context context, DBWrapper db, int lineId) {
		super(context, db.getStationsyLineId(lineId), android.support.v4.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public void bindView(View itemView, Context arg1, Cursor cursor) {
		TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
		text1.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATIONS_NAME_COLUMN)));
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup parent) {
		View itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		return itemView;
	}
}
