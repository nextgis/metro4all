package com.nextgis.whichexit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link PoiListFragment.OnPOISelectedListener} interface to handle interaction
 * events. Use the {@link PoiListFragment#newInstance} factory method to create
 * an instance of this fragment.
 * 
 */
public class PoiListFragment extends Fragment implements TextWatcher {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_POI_LIST = "poi_list";
	private Geocoder mGeocoder;
	private ArrayList<Address> mSearchResults;
	private OnPOISelectedListener mListener;
	private ListView mSearchResultListView;
	private SearchResultsAdapter mSearchResultsListViewAdapter;
	private String mLastSearchString;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment PoiListFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static PoiListFragment newInstance() {
		PoiListFragment fragment = new PoiListFragment();
		// Bundle args = new Bundle();
		// args.putString(ARG_POI_LIST, poiList);
		// args.putString(ARG_PARAM2, param2);
		// fragment.setArguments(args);
		return fragment;
	}

	public PoiListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_poi_list, container,
				false);
		mSearchResultListView = (ListView) rootView
				.findViewById(R.id.poiResult);
		mSearchResultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> sender, View item,
					int position, long id) {

				if (mListener != null) {
					mListener.onPOISelected(mSearchResults.get(position));
				}
			}
		});

		mSearchResultListView
				.setAdapter(mSearchResultsListViewAdapter = new SearchResultsAdapter(
						getActivity()));
		mSearchResults = new ArrayList<Address>();
		mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPOISelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(String.format(
					"Activity %s must implement %s.", activity.getClass()
							.getSimpleName(), OnPOISelectedListener.class
							.getSimpleName()));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnPOISelectedListener {
		// TODO: Update argument type and name
		public void onPOISelected(Address address);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		mLastSearchString = s.toString();
		if (s.length() > 3) {
			(new GeoSearchAsyncTask())
					.execute(new String[] { mLastSearchString });
		} else {
			mSearchResultsListViewAdapter.clearResults();
		}

	}

	public void startPOISearch() {
		// TODO Auto-generated method stub
		(new GeoSearchAsyncTask())
		.execute(new String[] { mLastSearchString });
	}

	private Object mSearchResultsLock = new Object();
	private class GeoSearchAsyncTask extends
			AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... args) {
			List<Address> results = null;
			try {
				results = mGeocoder.getFromLocationName(args[0], 10);
				List<Address> intersection = new ArrayList<Address>();
				if (results != null && results.size() > 0) {
//					for (Address found : results) {
//						for (Address address : mSearchResults) {
//							if (found.equals(address)) {
//								intersection.add(address);
//							}
//						}
//					}
					synchronized (mSearchResultsLock) {
//						mSearchResults.removeAll(intersection);
						mSearchResults.clear();
						mSearchResults.addAll(results);
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return results;
		}

		@Override
		protected void onPostExecute(List<Address> results) {
			if (results != null && results.size() > 0) {
				mSearchResultsListViewAdapter
						.updateSearchResults(mSearchResults);
			}
		}
	}

}
