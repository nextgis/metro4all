/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Author:   Baryshnikov Dmitriy (aka Bishop), polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013 NextGIS
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/
package com.nextgis.metroaccess;

import java.io.File;
import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList <StationItem> mStationList;
	private ImageButton showSchemaButton;
	
	public StationListAdapter(Context c, ArrayList <StationItem> list) {
		mContext = c;
		mStationList = list;
	}

	public int getCount() {
		return mStationList.size();
	}

	public Object getItem(int arg0) {
		return mStationList.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// get the selected entry
		final StationItem entry = mStationList.get(position);

		// reference to convertView
		View v = convertView;

		// inflate new layout if null
		if(v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = inflater.inflate(R.layout.station_row_layout, null);
		}

		// load controls from layout resources
		TextView tvName = (TextView)v.findViewById(R.id.tvStationName);
		tvName.setText(entry.GetName());
		

		//TextView tvPhone = (TextView)v.findViewById(R.id.tvPhone);

		ImageView ivIcon = (ImageView)v.findViewById(R.id.ivIcon);
		// set data to display
		//File path = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	    File imgFile = new File(MainActivity.msRDataPath + "/icons", "" + entry.GetLine() + "" + entry.GetType() + ".png");		
		Log.d(MainActivity.TAG, imgFile.getPath());
		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ivIcon.setImageBitmap(myBitmap);
		}	

		//tvPhone.setText(entry.GetPhone());
		
		showSchemaButton = (ImageButton) v.findViewById(R.id.show_sheme);
		final File schemaFile = new File(MainActivity.msRDataPath + "/schemes", "" + entry.GetId() + ".png");		
		if(schemaFile.exists()){
			showSchemaButton.setOnClickListener(new OnClickListener() {
 
				public void onClick(View arg0) {
				    try {
				    	Log.d(MainActivity.TAG, schemaFile.getPath());
				    	Intent intent = new Intent(Intent.ACTION_VIEW);
				    	intent.setDataAndType(Uri.fromFile(schemaFile),"image/png");
				    	mContext.startActivity(intent);
				    } catch (ActivityNotFoundException e) {
				        Log.e(MainActivity.TAG, "Call failed", e);
				    }
				}
	 
			});
		}
		else{
			showSchemaButton.setVisibility(View.INVISIBLE);
		}

		return v;

	}
	
	public static class StationItem implements Parcelable{
		private String sName;
		private int nType;// 1 - src, 2 - dest, 3 - cross from, 4 - cross to, 5 - transit
		private int nId;
		
		public StationItem(int nId, String sName, int nType) {
			this.sName = sName;
			this.nId = nId;
			this.nType = nType;
		}		
		
		public String GetName(){
			return sName;
		}
		
		public int GetId(){
			return nId;
		}
		
		public int GetType(){
			return nType;
		}
		
		public int GetLine(){
			return MainActivity.mmoStations.get(nId).getLine();
		}
		
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void writeToParcel(Parcel out, int flags) {
			out.writeString(sName);
			out.writeInt(nId);
			out.writeInt(nType);
		}	
		
		public static final Parcelable.Creator<StationItem> CREATOR
        = new Parcelable.Creator<StationItem>() {
		    public StationItem createFromParcel(Parcel in) {
		        return new StationItem(in);
		    }
		
		    public StationItem[] newArray(int size) {
		        return new StationItem[size];
		    }
		};
		
		private StationItem(Parcel in) {
			sName = in.readString();
			nId = in.readInt();
			nType = in.readInt();
		}

	}
}
