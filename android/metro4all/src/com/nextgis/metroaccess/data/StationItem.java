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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class StationItem implements Parcelable {
	private String sName;
	private int nType;// 1 - src, 2 - dest, 3 - cross from, 4 - cross to, 5 - transit
	private int nId;
	private int nLine;
	private int nNode;
	private Map<Integer, PortalItem> maoPortals;
	
	public StationItem(int nId, String sName, int nLine, int nNode, int nType) {
		this.maoPortals = new HashMap<Integer, PortalItem>();
		this.sName = sName;
		this.nId = nId;
		this.nType = nType;
		this.nLine = nLine;
		this.nNode = nNode;
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
	
	public int GetNode(){
		return nNode;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(sName);
		out.writeInt(nId);
		out.writeInt(nLine);
		out.writeInt(nNode);
		out.writeInt(nType);
		//
		out.writeInt(maoPortals.size());
		for(PortalItem it : maoPortals.values()){
			out.writeValue(it);
		}
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
		nLine = in.readInt();
		nNode = in.readInt();
		nType = in.readInt();
		
		maoPortals = new HashMap<Integer, PortalItem>();
		int nSize = in.readInt();
		for(int i = 0; i < nSize; i++){
			PortalItem it = (PortalItem) in.readValue(PortalItem.class.getClassLoader());
			maoPortals.put(it.GetId(), it);
		}
	}

	@Override
	public String toString() {
		return GetName();
	}

	public void SetType(int nType) {
		this.nType = nType;
	}
	
	public List<PortalItem> GetPortals(boolean bIn){
		List<PortalItem> ret = new ArrayList<PortalItem>();
		for(PortalItem pit : maoPortals.values()){
			if(bIn && (pit.GetDirection() == 1 || pit.GetDirection() == 3)){
				ret.add(pit);
			}
			else if(!bIn && (pit.GetDirection() == 2 || pit.GetDirection() == 3)){
				ret.add(pit);
			}
		}
		return ret;
	}
	
	public void AddPortal(PortalItem it){
		maoPortals.put(it.GetId(), it);
	}

	public PortalItem GetPortal(int nPortalId) {
		return maoPortals.get(nPortalId);
	}
}
