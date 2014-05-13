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

package com.nextgis.metroaccess.data;

import android.os.Parcel;
import android.os.Parcelable;

public class PortalItem implements Parcelable {
	private String sName;
	private int nDirection;// 1 - in, 2 - out, 3 - both
	private int nId;
	private int nStationId;
	private int[] anDetails;
	
	public PortalItem(int nId, String sName, int nStationId, int nDirection, int[] anDetails) {
		this.sName = sName;
		this.nId = nId;
		this.nDirection = nDirection;
		this.nStationId = nStationId;
		this.nStationId = nStationId;
		this.anDetails = anDetails;
	}	
	
	public String GetName(){
		if(sName.length() == 0){
			return "#" + nId;
		}
		return sName;
	}
	
	public int GetId(){
		return nId;
	}
	
	public int GetDirection(){
		return nDirection;
	}
	
	public int GetStationId(){
		return nStationId;
	}
	
	public int[] GetDetailes(){
		return anDetails;
	}
	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(sName);
		out.writeInt(nId);
		out.writeInt(nStationId);
		out.writeInt(nDirection);
		out.writeInt(anDetails.length);
		for(int i = 0; i < anDetails.length; i++){
			out.writeInt(anDetails[i]);
		}
	}

	public static final Parcelable.Creator<PortalItem> CREATOR
    = new Parcelable.Creator<PortalItem>() {
	    public PortalItem createFromParcel(Parcel in) {
	        return new PortalItem(in);
	    }
	
	    public PortalItem[] newArray(int size) {
	        return new PortalItem[size];
	    }
	};
	
	private PortalItem(Parcel in) {
		sName = in.readString();
		nId = in.readInt();
		nStationId = in.readInt();
		nDirection = in.readInt();
		int size = in.readInt();
		anDetails = new int[size];
		for(int i = 0; i < size; i++){
			anDetails[i] = in.readInt();
		}
	}

	@Override
	public String toString() {
		return GetName();
	}
	
}
