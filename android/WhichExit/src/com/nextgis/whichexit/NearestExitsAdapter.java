package com.nextgis.whichexit;

import java.util.ArrayList;
import java.util.List;

import com.nextgis.whichexit.NearestExitsListFragment.PoiDistance;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NearestExitsAdapter extends BaseAdapter {

	private SparseArray<SubStationExit> exits;
	ArrayList<PoiDistance> exitDistances;
	private Context mContext;
	public NearestExitsAdapter(Context context,
			SparseArray<SubStationExit> nearestExits, ArrayList<PoiDistance> distances) {
		exits = nearestExits;
		exitDistances = distances;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return exits.size();
	}

	@Override
	public SubStationExit getItem(int position) {
		// TODO Auto-generated method stub
		return exits.get((int)getItemId(position));
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return exitDistances.get(position).exitId;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup holder) {
		// TODO Auto-generated method stub
		if(contentView == null) {
			contentView = LayoutInflater.from(mContext).inflate(R.layout.exit_listview_item_layout, holder, false);
		}
		SubStationExit exit = getItem(position);
		((TextView)contentView.findViewById(R.id.stationName)).setText(exit.getStation().getName());
		((TextView)contentView.findViewById(R.id.exitName)).setText(exit.name);
		double distance = exitDistances.get(position).distance;
		if(distance > 1)
			((TextView)contentView.findViewById(R.id.exitDistance)).setText(String.format("%5.1f Км", distance));
		else 
			((TextView)contentView.findViewById(R.id.exitDistance)).setText(String.format("%3.0f М", 1000.0d*distance));
		return contentView;
	}

}
