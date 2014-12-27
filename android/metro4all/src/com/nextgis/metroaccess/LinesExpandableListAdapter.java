/*******************************************************************************
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
*******************************************************************************/
package com.nextgis.metroaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import android.content.Context;

public class LinesExpandableListAdapter extends StationIndexedExpandableListAdapter {

	protected Map<Integer, String> momLines;
	protected HashMap<Integer, Integer> mDigitIndexer; 
	protected Integer[] mnaSections;
	
	public LinesExpandableListAdapter(Context c, List<StationItem> stationList, Map<Integer, String> omLines) {
		super(c, stationList);
		
		momLines = omLines;
	}

	@Override
	protected void onInit() {
    	Collections.sort(mStationList, new StationItemComparator()); 

    	mDigitIndexer = new HashMap<Integer, Integer>();

		for (int x = 0; x < mStationList.size(); x++) {  
			int ch = mStationList.get(x).GetLine();  
			if (!mDigitIndexer.containsKey(ch)){
				mDigitIndexer.put(ch, x);

				String sName = ""+ ch + ". " + momLines.get(ch);
				StationItem sit =
                        new StationItem(-1, sName, -1, -1, -1, -1, -1, -1);
				mStationList.add(x, sit);
			}     
		}  
		
        List<Integer> sectionList = new ArrayList<Integer>( mDigitIndexer.keySet() );  

        Collections.sort(sectionList);  
        
        mnaSections = new Integer[sectionList.size()];  

        sectionList.toArray(mnaSections);
		
	}
	
	@Override
	public int getPositionForSection(int arg0) {
		return mDigitIndexer.get(mnaSections[arg0]);
	}

	@Override
	public Object[] getSections() {
		return mnaSections;
	}
	
	
	protected class StationItemComparator implements Comparator<StationItem>
	{
	    public int compare(StationItem left, StationItem right) {
	    	if(left.GetLine() == right.GetLine())
	    		return left.GetOrder()  - right.GetOrder();//left.GetName().compareTo( right.GetName() );
	    	else {
	    		return left.GetLine() - right.GetLine();
	    	}
	    }
	}

}
