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

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList <StationItem> mStationList;
	
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
		
		/*
		//TextView tvPhone = (TextView)v.findViewById(R.id.tvPhone);

		ImageView ivIcon = (ImageView)v.findViewById(R.id.ivIcon);
		// set data to display
		File path = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	    File imgFile = new File(path, entry.GetImage());		
		Log.d("PanicButton", imgFile.getPath());
		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ivIcon.setImageBitmap(myBitmap);
		}	
		else
		{
			ivIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_police));
		}
		tvPhone.setText(entry.GetPhone());
		
		callButton = (ImageButton) v.findViewById(R.id.phone_call);		 
		callButton.setOnClickListener(new OnClickListener() {
 
			public void onClick(View arg0) {
			    try {
			        Intent callIntent = new Intent(Intent.ACTION_CALL);
			        callIntent.setData(Uri.parse("tel:" + entry.GetPhone()));
			        mContext.startActivity(callIntent);
			    } catch (ActivityNotFoundException e) {
			        Log.e("Panic Button", "Call failed", e);
			    }
			}
 
		});		*/

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
