/*******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway for disabled.
 * Author:   Dmitry Baryshnikov, polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013,2014 NextGIS
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
*******************************************************************************/

package com.nextgis.metroaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.nextgis.metroaccess.data.StationItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SectionIndexer;

public class StationIndexedExpandableListAdapter extends StationExpandableListAdapter implements SectionIndexer {

	protected HashMap<String, Integer> mAlphaIndexer; 
	
	protected String[] msaSections;

	public StationIndexedExpandableListAdapter(Context c, List<StationItem> stationList) {
		super(c);

		mStationList = new ArrayList <StationItem>();
		mStationList.addAll(stationList);
		//mStationList = stationList;
		mAlphaIndexer = new HashMap<String, Integer>();
	}
	
	@Override
	protected void onInit(){
    	Collections.sort(mStationList, new StationItemComparator()); 

		for (int x = 0; x < mStationList.size(); x++) {  
			String s = mStationList.get(x).GetName();  
			String ch = s.substring(0, 1);  
			ch = ch.toUpperCase();  
			if (!mAlphaIndexer.containsKey(ch)){
				mAlphaIndexer.put(ch, x);

				StationItem sit =
                        new StationItem(-1, ch, -1, -1, -1, -1, -1, -1);
				mStationList.add(x, sit);

			}     
		}  	
        
        List<String> sectionList = new ArrayList<String>( mAlphaIndexer.keySet() );  

        Collections.sort(sectionList);  
        
        msaSections = new String[sectionList.size()];  

        sectionList.toArray(msaSections);
	}
	
	@Override
	public int getPositionForSection(int arg0) {
		return mAlphaIndexer.get(msaSections[arg0]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return msaSections;
	}

	protected class StationItemComparator implements Comparator<StationItem>
	{
	    public int compare(StationItem left, StationItem right) {
	    	return left.GetName().compareTo( right.GetName() );
	    }
	}
	
	public void Update(List<StationItem> stationList){
		super.Update();

		mStationList.clear();
		mStationList.addAll(stationList);
		//mStationList = stationList;
		mAlphaIndexer.clear();
		
		onInit();
	}

}
