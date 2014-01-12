package com.nextgis.whichexit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link NearestExitsListFragment.OnExitSelectedListener} interface to handle
 * interaction events. Use the {@link NearestExitsListFragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public class NearestExitsListFragment extends Fragment {

	SparseArray<SubStation> mSubStations;
	// ArrayList<SubStation> mSubStations;
	SparseArray<SubStationExit> mExits;

	private OnExitSelectedListener mListener;
	private ListView mListView;
	private Address mCurrentPoi;

	public NearestExitsListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.fragment_nearest_exits_list,
				container, false);

		mListView = (ListView) result.findViewById(R.id.stationList);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View senderView,
					int position, long id) {
				// TODO Auto-generated method stub
				mListener.onExitSelected(mExits.get((int) id), mCurrentPoi);
			}
		});
		init(getActivity());
		return result;
	}

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		try {
			mListener = (OnExitSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		if (mExits != null)
			return;
		mSubStations = new SparseArray<SubStation>();
		DBWrapper mDB = new DBWrapper(context);
		mDB.open();
		Cursor c = mDB.getAllstations();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			SubStation station = SubStation.fromCursor(c);
			mSubStations.append(station.getId_station(), station);
			c.moveToNext();
		}

		c = mDB.getAllportals();
		mExits = new SparseArray<SubStationExit>();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			SubStationExit exit = new SubStationExit(c);
			exit.setStation(mSubStations.get(exit.id_station));
			mExits.append(exit.id_entrance, exit);
			c.moveToNext();
		}
		if (mCurrentPoi != null) {
			SortPoiTask task = new SortPoiTask();
			task.execute(mCurrentPoi);
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
	public interface OnExitSelectedListener {
		// TODO: Update argument type and name
		public void onExitSelected(SubStationExit exit, Address mCurrentPoi);

		public void onNearestExitsListFragmentCreated(NearestExitsListFragment f);
	}

	private double distance(LatLng point1, LatLng point2) {
		return distance(point1.latitude, point1.longitude, point2.latitude,
				point2.longitude);
	}

	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515 * 1.609344;
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public void showExits(Address poi) {
		mCurrentPoi = poi;
		if (mExits != null) {
			SortPoiTask task = new SortPoiTask();
			task.execute(poi);
		}
	}

	private List<SubStationExit> sortExits(Address poi) {
		// TODO Auto-generated method stub
		return null;
	}

	public class PoiDistance {
		public double distance;
		public int exitId;

		public PoiDistance(int exit, double d) {
			this.distance = d;
			this.exitId = exit;
		}
	}

	private class SortPoiTask extends
			AsyncTask<Address, Void, SparseArray<SubStationExit>> {

		ArrayList<PoiDistance> exitDistances = new ArrayList<PoiDistance>();

		@Override
		protected SparseArray<SubStationExit> doInBackground(Address... addr) {
			// TODO Auto-generated method stub
			for (int i = 0; i < mExits.size(); i++) {
				SubStationExit exit = mExits.get(mExits.keyAt(i));
				if (exit.direction.equals("out")
						|| exit.direction.equals("both")) {
					PoiDistance distance = new PoiDistance(exit.id_entrance,
							distance(exit.latlng.latitude,
									exit.latlng.longitude,
									addr[0].getLatitude(),
									addr[0].getLongitude()));
					exitDistances.add(distance);
				}
			}
			Collections.sort(exitDistances, new Comparator<PoiDistance>() {

				@Override
				public int compare(PoiDistance lhs, PoiDistance rhs) {
					// TODO Auto-generated method stub
					return (lhs.distance < rhs.distance) ? -1
							: (lhs.distance > rhs.distance) ? 1 : 0;
				}
			});

			SparseArray<SubStationExit> newarestExits = new SparseArray<SubStationExit>();
			int total = exitDistances.size();
			for (int i = 0; i < 10; i++) {
				newarestExits.append(exitDistances.get(i).exitId,
						mExits.get(exitDistances.get(i).exitId));
			}
			return newarestExits;
		}

		@Override
		protected void onPostExecute(SparseArray<SubStationExit> nearestExits) {
			mListView.setAdapter(new NearestExitsAdapter(getActivity(),
					nearestExits, exitDistances));
		}
	}
}
