package com.nextgis.whichexit;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MomentsMapController implements OnCameraChangeListener,
		OnMapLoadedCallback {

	private static final int MAX_MARKERS_ON_MAP = 100;
	public static final String TAG = MomentsMapController.class.getSimpleName();
	DBWrapper mDBWrapper;
	private GoogleMap mGoogleMap;

	private ArrayList<MapMarker> mAllMarkers;
	private ArrayList<MapMarker> mVisibleMarkers;
	private Context mContext;

	public MomentsMapController(Context pContext, GoogleMap pMap) {
		mContext = pContext;
		mDBWrapper = new DBWrapper(pContext);
		mDBWrapper.open();
		if (mDBWrapper.mDb == null) {
			throw new IllegalStateException("Unable to open Database");
		}
		mGoogleMap = pMap;
		mVisibleMarkers = new ArrayList<MapMarker>();
		mAllMarkers = new ArrayList<MapMarker>();
		Cursor c = mDBWrapper.getAllportals();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			double latitude = c.getDouble(c
					.getColumnIndexOrThrow(DBWrapper.PORTALS_LAT_COLUMN));
			double longitude = c.getDouble(c
					.getColumnIndexOrThrow(DBWrapper.PORTALS_LON_COLUMN));
			LatLng mMarkerCoordinates = new LatLng(latitude, longitude);
			MapMarker mMarker = new MapMarker(mMarkerCoordinates,
					null);
			mAllMarkers.add(mMarker);
			c.moveToNext();
		}
	}

	private void cleanupInvisibleMarkers(VisibleRegion pVisibleRegion) {
		Log.d(TAG, "Cleanup started...");
		LatLngBounds regionBounds = pVisibleRegion.latLngBounds;
		ArrayList<MapMarker> candidatesToDelete = new ArrayList<MapMarker>();
		for (MapMarker visiblePointer : mVisibleMarkers) {
			if (isMarkerOffRegion(regionBounds, visiblePointer)) {
				candidatesToDelete.add(visiblePointer);
				visiblePointer.mMarker.remove();
			}
		}
		mVisibleMarkers.removeAll(candidatesToDelete);
		Log.d(TAG, "Cleanup finished...");
	}
	
	private void addVisibleMarkers(VisibleRegion pVisibleRegion) {
		Log.d(TAG, "Add visible markers started...");
		LatLngBounds regionBounds = pVisibleRegion.latLngBounds;
		ArrayList<MapMarker> candidatesToAdd = new ArrayList<MapMarker>();
		for (MapMarker candidateMarker : mAllMarkers) {
			if (isMarkerInRegion(regionBounds, candidateMarker) && !isMarkerOnMap(candidateMarker)) {
				candidatesToAdd.add(candidateMarker);
				Bitmap mMarkerBitmap = randomMarkerBitmap();
				if (mMarkerBitmap != null) {
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions
							.draggable(false)
							.icon(BitmapDescriptorFactory
									.fromBitmap(mMarkerBitmap))
							.anchor(0.5f, 0.5f).position(candidateMarker.mCoordinates);
					candidateMarker.mMarker = mGoogleMap.addMarker(mMarkerOptions);
					this.mVisibleMarkers.add(candidateMarker);
					mMarkerBitmap.recycle();
					if(candidatesToAdd.size() + mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
						break;
					}
				}
			}
		}
		mVisibleMarkers.addAll(candidatesToAdd);
		Log.d(TAG, "Add visible markers finished...");
	}

	private boolean isMarkerOnMap(MapMarker candidateMarker) {
		// TODO Auto-generated method stub
		for(MapMarker marker : mVisibleMarkers) {
			if(marker.compareWith(candidateMarker)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param regionBounds
	 * @param visiblePointer
	 * @return
	 */
	private boolean isMarkerOffRegion(LatLngBounds regionBounds,
			MapMarker visiblePointer) {
		return !regionBounds.contains(visiblePointer.mCoordinates);
		// return visiblePointer.mCoordinates.latitude <
		// regionBounds.southwest.latitude
		// || visiblePointer.mCoordinates.longitude <
		// regionBounds.southwest.longitude
		//
		// || visiblePointer.mCoordinates.latitude >
		// regionBounds.northeast.latitude
		// || visiblePointer.mCoordinates.longitude >
		// regionBounds.northeast.longitude;
	}

	private boolean isMarkerInRegion(LatLngBounds regionBounds,
			MapMarker visiblePointer) {
		return regionBounds.contains(visiblePointer.mCoordinates);
		// return visiblePointer.mCoordinates.latitude >
		// regionBounds.southwest.latitude
		// && visiblePointer.mCoordinates.longitude >
		// regionBounds.southwest.longitude
		//
		// && visiblePointer.mCoordinates.latitude <
		// regionBounds.northeast.latitude
		// && visiblePointer.mCoordinates.longitude <
		// regionBounds.northeast.longitude;
	}

	public void renderMap() {
		LatLngBounds visibleRegion = mGoogleMap.getProjection()
				.getVisibleRegion().latLngBounds;
		for (MapMarker marker : mAllMarkers) {
			if (isMarkerInRegion(visibleRegion, marker)) {
				Bitmap mMarkerBitmap = randomMarkerBitmap();
				if (mMarkerBitmap != null) {
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions
							.draggable(false)
							.icon(BitmapDescriptorFactory
									.fromBitmap(mMarkerBitmap))
							.anchor(0.5f, 0.5f).position(marker.mCoordinates);
					marker.mMarker = mGoogleMap.addMarker(mMarkerOptions);
					this.mVisibleMarkers.add(marker);
					if(mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
						break;
					}
					mMarkerBitmap.recycle();
				}
			}
		}
	}

	private Bitmap randomMarkerBitmap() {
		String markerAssetFileName = String.format("markers/%d_114x114.jpg",
				(int) (Math.random() * 92));
		Bitmap toReturn = null;
		try {
			toReturn = BitmapFactory.decodeStream(mContext.getAssets().open(
					markerAssetFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public void onCameraChange(CameraPosition pCameraPosition) {
		// TODO Auto-generated method stub
		Log.d(TAG, String.format("Camera zoom: %f", pCameraPosition.zoom));
		Log.d(TAG, String.format("Camera center: (%f, %f)",
				pCameraPosition.target.latitude,
				pCameraPosition.target.longitude));
		Log.d(TAG, String
				.format("Map far left: (%f, %f)", mGoogleMap.getProjection()
						.getVisibleRegion().farLeft.latitude, mGoogleMap
						.getProjection().getVisibleRegion().farLeft.longitude));
		cleanupInvisibleMarkers(mGoogleMap.getProjection().getVisibleRegion());
		addVisibleMarkers(mGoogleMap.getProjection().getVisibleRegion());
	}



	public void release() {
		mGoogleMap = null;
	}

	@Override
	public void onMapLoaded() {
		this.renderMap();
	}

}
