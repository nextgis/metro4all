/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Author:   Dmitry Baryshnikov, polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2014 NextGIS
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
import com.nextgis.metroaccess.R;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

public class GraphDataItem implements Parcelable {
	
	private int m_nVersion;
	private String m_sName;
	private String m_sLocName;
	private boolean m_bDirected;
	private int m_nSizeKb;
	private String m_sPath;
	private String m_sKb, m_sMb;
	
	public GraphDataItem(int nVersion, String sName, String sLocName, String sPath, int nSizeKb, boolean bDirected, String sKb, String sMb) {
		this.m_sName = sName;
		this.m_sLocName = sLocName;
		this.m_nVersion = nVersion;
		this.m_bDirected = bDirected;
		this.m_nSizeKb = nSizeKb;
		this.m_sKb = sKb;
		this.m_sMb = sMb;
		this.m_sPath = sPath;
	}	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(m_sName);
		out.writeString(m_sLocName);
		out.writeInt(m_nVersion);
		out.writeInt(m_nSizeKb);
		out.writeInt(m_bDirected == true ? 1 : 0);
		out.writeString(m_sPath);
		out.writeString(m_sKb);
		out.writeString(m_sMb);
	}
	
	public String GetName(){
		return m_sName;
	}
	
	public String GetLocaleName(){
		return m_sLocName;
	}
	
	public int GetVersion(){
		return m_nVersion;
	}
	
	public boolean GetDirected(){
		return m_bDirected;
	}
	
	public String GetFullName(){
		String sValSize;
		int nSize;
		if(m_nSizeKb > 1000){
			nSize = m_nSizeKb / 1000;
			sValSize = m_sMb;
		}
		else{
			nSize = m_nSizeKb;
			sValSize = m_sKb;
		}
	    String sOutput = m_sLocName + " (" + nSize + " ";
	    sOutput += sValSize;
	    sOutput += ")";
	    
	    return sOutput;
	}

	public static final Parcelable.Creator<GraphDataItem> CREATOR
    = new Parcelable.Creator<GraphDataItem>() {
	    public GraphDataItem createFromParcel(Parcel in) {
	        return new GraphDataItem(in);
	    }
	
	    public GraphDataItem[] newArray(int size) {
	        return new GraphDataItem[size];
	    }
	};
	
	private GraphDataItem(Parcel in) {
		m_sName = in.readString();
		m_sLocName = in.readString();
		m_nVersion = in.readInt();
		m_nSizeKb = in.readInt();
		m_bDirected = in.readInt() == 1 ? true : false;
		m_sKb = in.readString();
		m_sMb = in.readString();
		m_sPath = in.readString();
	}

	@Override
	public String toString() {
		return GetFullName();
	}
	
	public void SetPath(String sPath){
		m_sPath = sPath;
	}
	
	public String GetPath(){
		return m_sPath;
	}
	
	public boolean IsNewer(GraphDataItem oItem){
		if(m_sName.equals(oItem.GetName())){
			if(m_nVersion > oItem.GetVersion()) {
				return true;
			}
		}
		return false;
	}
}
