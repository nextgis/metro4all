package com.nextgis.whichexit;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapMarker {

	
	public LatLng mCoordinates;
	public Marker mMarker;
	private SubStationExit mSubstationExit;
	private SubStation mSubStation;
	
	/**
	 * @return the mSubstationExit
	 */
	public SubStationExit getSubstationExit() {
		return mSubstationExit;
	}

	/**
	 * @param mSubstationExit the mSubstationExit to set
	 */
	public void setSubstationExit(SubStationExit mSubstationExit) {
		this.mSubstationExit = mSubstationExit;
	}

	public MapMarker(LatLng pCoordinates, SubStation pStation, SubStationExit pExit) {
		super();
		this.mCoordinates = pCoordinates;
		mSubStation = pStation;
		mSubstationExit = pExit;
	}
	
	public boolean compareWith(MapMarker pCandidate) {
		return this.mCoordinates.latitude == pCandidate.mCoordinates.latitude && this.mCoordinates.longitude == pCandidate.mCoordinates.longitude;
	}
	
}
