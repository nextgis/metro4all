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
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteItem implements Parcelable {
	private String sName;
	private int nType;// 1 - src, 2 - dest, 3 - cross from, 4 - cross to, 5 - transit, 6 - entrance; 7 - exit
	private int nId;
	private int nLine;
	private List<BarrierItem> astBarriers;
	
	public RouteItem(int nId, String sName, int nLine, int nType) {
		this.astBarriers = new ArrayList<BarrierItem>();
		this.sName = sName;
		this.nId = nId;
		this.nType = nType;
		this.nLine = nLine;
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
		return nLine;
	}
	
	public void AddBarrier(BarrierItem bit){
		astBarriers.add(bit);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(sName);
		out.writeInt(nId);
		out.writeInt(nLine);
		out.writeInt(nType);
		//
		out.writeInt(astBarriers.size());
		for(BarrierItem it : astBarriers){
			out.writeValue(it);
		}

	}	
	
	public List<BarrierItem> GetProblems(){
		return astBarriers;
	}
	
	public static final Parcelable.Creator<RouteItem> CREATOR
    = new Parcelable.Creator<RouteItem>() {
	    public RouteItem createFromParcel(Parcel in) {
	        return new RouteItem(in);
	    }
	
	    public RouteItem[] newArray(int size) {
	        return new RouteItem[size];
	    }
	};
	
	private RouteItem(Parcel in) {
		sName = in.readString();
		nId = in.readInt();
		nLine = in.readInt();
		nType = in.readInt();

		astBarriers = new ArrayList<BarrierItem>();
		int nSize = in.readInt();
		for(int i = 0; i < nSize; i++){
			BarrierItem it = (BarrierItem) in.readValue(BarrierItem.class.getClassLoader());
			astBarriers.add(it);
		}
	}

	@Override
	public String toString() {
		return GetName();
	}

	public void SetType(int nType) {
		this.nType = nType;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void SetLine(int nLine) {
		this.nLine = nLine;		
	}
}
