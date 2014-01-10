package com.nextgis.whichexit;

import java.util.ArrayList;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseAdapter {

	private ArrayList<Address> mResultsArrayList;
	private Context mContext;

	public SearchResultsAdapter(Context pContext) {
		mContext = pContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (mResultsArrayList == null) ? 0 : mResultsArrayList.size();
	}

	@Override
	public Address getItem(int position) {
		// TODO Auto-generated method stub
		return mResultsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_2, parent, false);
		}
		Address item = getItem(position);
		String title = (item.getLocality() == null) ? "" : item
				.getFeatureName();
		((TextView) convertView.findViewById(android.R.id.text1))
				.setText(title);
		((TextView) convertView.findViewById(android.R.id.text2))
				.setText(getAddressString(item));
		return convertView;
	}

	public void updateSearchResults(ArrayList<Address> mSearchResults) {
		mResultsArrayList = mSearchResults;
		this.notifyDataSetInvalidated();
	}

	private String getAddressString(Address address) {
		String line = address.getAddressLine(0);
		String result = line;
		for (int i = 1; (line = address.getAddressLine(i)) != null; i++) {
			result = String.format("%s, %s", result, line);
		}
		return result;
	}

	public void clearResults() {
		if (mResultsArrayList != null) {
			mResultsArrayList.clear();
			notifyDataSetInvalidated();
		}
	}
}
