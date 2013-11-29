package com.nextgis.whichexit;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker {

	
	public LatLng mCoordinates;
	public Bitmap mBitmap;
	public Marker mMarker;
	
	/**
	 * @param mCoordinates
	 * @param mBitmap
	 */
	public MapMarker(LatLng pCoordinates, Bitmap pBitmap) {
		super();
		this.mCoordinates = pCoordinates;
		this.mBitmap = pBitmap;
	}
	
	/**
	 * @return the mBitmap
	 */
	public Bitmap getmBitmap() {
		return mBitmap;
	}
	/**
	 * @param mBitmap the mBitmap to set
	 */
	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
	
	public boolean compareWith(MapMarker pCandidate) {
		return this.mCoordinates.latitude == pCandidate.mCoordinates.latitude && this.mCoordinates.longitude == pCandidate.mCoordinates.longitude;
	}
	
}
