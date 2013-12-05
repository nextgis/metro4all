package com.nextgis.metro4all.GoodGuy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import com.nextgis.metro4all.GoodGuy.utils.db.DBWrapper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CellDataExportTask extends AsyncTask<Void, Integer, String> {

	private Context mContext;
	private DBWrapper mDB;
	private CellDataExportListener mListener;
	
	/**
	 * Avoid empty constructor
	 */
	private CellDataExportTask() {};
	
	public CellDataExportTask(Context pContext, CellDataExportListener pListener) {
		mContext = pContext;
		mListener = pListener;
		mDB = new DBWrapper(mContext);
		mDB.open();
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		try {
			File tempFile = File.createTempFile("celldata", ".csv", Environment.getExternalStorageDirectory());
		
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
			OutputStreamWriter writer = new OutputStreamWriter(bos);
			Cursor cellDataCursor = mDB.getAllcelldata();
			cellDataCursor.moveToFirst();
			writer.write(this.getHeaderRow(cellDataCursor));
			while(!cellDataCursor.isAfterLast()) {
				writer.write(getCursorRow(cellDataCursor));
				cellDataCursor.moveToNext();
			}
//			String result = writer.toString();
			writer.flush();
			writer.close();
			return tempFile.getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	protected void onPostExecute(String fileName) {
		mListener.onCellDataExportFinished(fileName);
	}

	private String getHeaderRow(Cursor cellDataCursor) {
		// TODO Auto-generated method stub
		String output = null;
		for(int i = 1; i < cellDataCursor.getColumnCount(); i++) {
			if(i > 1) {
				output = String.format("%s, %s", output, cellDataCursor.getColumnName(i));
			} else {
				output = cellDataCursor.getColumnName(i);
			}
		}
		output = output + "\n";
		return output;
	}
	
	private String getCursorRow(Cursor cellDataCursor) {
		// TODO Auto-generated method stub
		String output = null;
		/*
		 * i starts from 1 in order to skip _id column, which has column index equal to 0
		 */
		for(int i = 1; i < cellDataCursor.getColumnCount(); i++) {
			if(i > 1) {
				output = String.format("%s, \"%s\"", output, cellDataCursor.getString(i));
			} else {
				output = String.format("\"%s\"", cellDataCursor.getString(i));
			}
		}
		output = output + "\n";
		return output;
	}
	
	/**
	 * Used as a callback to notify export finished event
	 * @author valetin
	 *
	 */
	public interface CellDataExportListener {
		/**
		 * Notify caller with resulting file name
		 * @param full path of file with CSV content of the table
		 */
		public void onCellDataExportFinished(String contentFile);
	}

}
