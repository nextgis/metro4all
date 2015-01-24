/*******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway for disabled.
 * Authors:  Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2013-2015 NextGIS
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nextgis.metroaccess.data.PortalItem;
import com.nextgis.metroaccess.data.StationItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StationIndexedExpandableListAdapter extends StationExpandableListAdapter implements SectionIndexer {
	protected HashMap<String, Integer> mIndexer;
    protected ArrayList<IndexedListItem> mItems;
    protected ArrayList<String> mSections;

	public StationIndexedExpandableListAdapter(Context c, List<StationItem> stationList) {
		super(c);

        mStationList.addAll(stationList);
        mItems = new ArrayList<>();
        mSections = new ArrayList<>();
		mIndexer = new HashMap<>();
	}
	
	abstract void onInit();
	
	@Override
	public int getPositionForSection(int position) {
        if (position >= mSections.size())
            return 0;

		return mIndexer.get(mSections.get(position));
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

    private int getPositionsCountBeforeId(int id) {
        int count = 0;

        for (Map.Entry<String, Integer> entry : mIndexer.entrySet())
            if (entry.getValue() < id) count++;

        return count;
    }

    private int getStationPosition(int id) {
        return id - getPositionsCountBeforeId(id);
    }

	@Override
	public Object[] getSections() {
		return mSections.toArray();
	}

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public IndexedListItem getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        if (mItems.get(groupPosition).isSection())
            return -1;

        StationItem sit = mStationList.get(getStationPosition(groupPosition));

        if (sit != null)
            return sit.GetId();

        return -1;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        if (mItems.get(groupPosition).isSection()) {
            if (convertView == null || convertView.findViewById(R.id.tvSection) == null) {
                convertView = mInfalInflater.inflate(R.layout.select_station_section, parent, false);
            }

            SectionItem section = (SectionItem) mItems.get(groupPosition);
            TextView item = (TextView) convertView.findViewById(R.id.tvSection);
            item.setText(section.getTitle());
        } else {
            convertView = getGroupView(convertView, mStationList.get(getStationPosition(groupPosition)), parent);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        clearSections();
        mItems.clear();

        super.notifyDataSetChanged();

        onInit();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mItems.get(groupPosition).isSection())
            return 0;

        StationItem sit = mStationList.get(getStationPosition(groupPosition));

        if (sit != null) {
            List<PortalItem> ls = sit.GetPortals(m_bIn);

            if (ls == null)
                return 0;

            return ls.size();
        }

        return 0;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        StationItem sit = mStationList.get(getStationPosition(groupPosition));

        if (sit != null) {
            List<PortalItem> lpit = sit.GetPortals(m_bIn);

            if (lpit != null)
                return lpit.get(childPosition);
        }

        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return getChildView(convertView, (PortalItem) getChild(groupPosition, childPosition), parent);
    }

    public void Update(List<StationItem> stationList){
        clearSections();
        mItems.clear();
        mStationList.clear();
        mStationList.addAll(stationList);
        super.Update();
	}

    private void clearSections() {
        mIndexer.clear();
        mSections.clear();
    }

    public static abstract class IndexedListItem {
        public abstract boolean isSection();
    }

    protected class SectionItem extends IndexedListItem {
        private final String title;

        public SectionItem(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }

        @Override
        public boolean isSection() {
            return true;
        }
    }
}
