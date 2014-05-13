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

public class BarrierItem  implements Parcelable{
	private int nId;
	private String sName;
	private boolean bIsProblem;
	private int nValue;
	
	public BarrierItem(int nId, String sName, boolean bIsProblem, int nValue) {
		this.bIsProblem = bIsProblem;
		this.sName = sName;
		this.nId = nId;
		this.nValue = nValue;
	}		
	
	public String GetName(){
		return sName;
	}
	
	public int GetId(){
		return nId;
	}
	
	public int GetValue(){
		return nValue;
	}
	
	public boolean IsProblem(){
		return bIsProblem;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(sName);
		out.writeInt(nId);
		out.writeInt(nValue);
		out.writeInt(bIsProblem == true ? 1 : 0);
	}	
	
	public static final Parcelable.Creator<BarrierItem> CREATOR
    = new Parcelable.Creator<BarrierItem>() {
	    public BarrierItem createFromParcel(Parcel in) {
	        return new BarrierItem(in);
	    }
	
	    public BarrierItem[] newArray(int size) {
	        return new BarrierItem[size];
	    }
	};
	
	private BarrierItem(Parcel in) {
		sName = in.readString();
		nId = in.readInt();
		nValue = in.readInt();
		bIsProblem = in.readInt() == 1 ? true : false;
	}

	@Override
	public String toString() {
		return GetName();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
