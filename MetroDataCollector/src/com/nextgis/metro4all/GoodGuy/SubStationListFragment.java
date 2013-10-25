package com.nextgis.metro4all.GoodGuy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link SubStationListFragment.OnSubStationListFragmentListener} interface to
 * handle interaction events. Use the {@link SubStationListFragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public class SubStationListFragment extends Fragment {


	private OnSubStationListFragmentListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment SubStationListFragment.
	 */

	public static SubStationListFragment newInstance() {
		SubStationListFragment fragment = new SubStationListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public SubStationListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sub_station_list, container,
				false);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onShowMoreDetails(SubStation station) {
		if (mListener != null) {
			mListener.onShowMoreDetails(station);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSubStationListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
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
	public interface OnSubStationListFragmentListener {
		// TODO: Update argument type and name
		public void onShowMoreDetails(SubStation station);
	}

}
