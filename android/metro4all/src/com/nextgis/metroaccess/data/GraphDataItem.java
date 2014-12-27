/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.nextgis.metroaccess.R;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

public class GraphDataItem implements Parcelable, Comparable<GraphDataItem> {
	
	private int m_nVersion;
	private String m_sName;
	private Map<String, String> m_sLocNames;
	private boolean m_bDirected;
	private int m_nSizeKb;
	private String m_sPath;
	private String m_sKb, m_sMb;
	
	public GraphDataItem(int nVersion, String sName, Map<String, String> sLocNames, String sPath, int nSizeKb, boolean bDirected, String sKb, String sMb) {
		this.m_sName = sName;
		this.m_sLocNames = sLocNames;
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
		out.writeInt(m_sLocNames.size());
		for(String key : m_sLocNames.keySet()){
			out.writeString(key);
			out.writeString(m_sLocNames.get(key));
		}
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
		String sLoc = Locale.getDefault().getLanguage();
		if(m_sLocNames.containsKey("name_" + sLoc)){
			return m_sLocNames.get("name_" + sLoc);
		}
		return m_sName;
	}
	
	public Map<String,String> GetLocaleNames(){
		return m_sLocNames;
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
	    String sOutput = GetLocaleName() + " (" + nSize + " ";
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
		
		m_sLocNames = new HashMap<String, String>();
		
		m_sName = in.readString();
		int size = in.readInt();
		for(int i = 0; i < size; i++){
			String key = in.readString();
			String value = in.readString();
			m_sLocNames.put(key,value);
		}		  
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

    @Override
    public int compareTo(GraphDataItem o) {
        return this.GetLocaleName().compareTo(o.GetLocaleName());
    }
}
