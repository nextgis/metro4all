package com.nextgis.whichexit;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link NearestExitsListFragment.OnExitSelectedListener} interface to
 * handle interaction events. Use the
 * {@link NearestExitsListFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class NearestExitsListFragment extends Fragment {

	SparseArray<SubStation> mSubStations;
//	ArrayList<SubStation> mSubStations;
	SparseArray<SubStationExit> mExits;

	private OnExitSelectedListener mListener;

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
		return inflater.inflate(R.layout.fragment_nearest_exits_list,
				container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		init(activity);
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
		if(mExits != null)
			return;
		mSubStations = new SparseArray<SubStation>();
		DBWrapper mDB = new DBWrapper(context);
		Cursor c = mDB.getAllstations();
		c.moveToFirst();
		while(!c.isAfterLast()) {
			SubStation station = SubStation.fromCursor(c);
			mSubStations.append(station.getId_station(), station);
			c.moveToNext();
		}
		
		c = mDB.getAllportals();
		mExits = new SparseArray<SubStationExit>();
		c.moveToFirst();
		while(!c.isAfterLast()) {
			SubStationExit exit = new SubStationExit(c);
			mExits.append(exit.id_entrance, exit);
			c.moveToNext();
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
		public void onExitSelected(SubStationExit exit);
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
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
//		LatLng poiCoordinates = new LatLng(poi.getLatitude(),
//				poi.getLongitude());
//		double bestDistance = 10000d;
//		MapMarker bestMarker = mSubExitMarkers.get(0);
//		bestDistance = distance(bestMarker.mCoordinates, poiCoordinates);
//		for (MapMarker marker : mSubExitMarkers) {
//			if (distance(marker.mCoordinates, poiCoordinates) < bestDistance) {
//				bestMarker = marker;
//				bestDistance = distance(marker.mCoordinates, poiCoordinates);
//			}
//		}
	}
}
