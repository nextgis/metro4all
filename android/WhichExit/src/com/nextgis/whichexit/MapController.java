package com.nextgis.whichexit;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapController implements OnCameraChangeListener,
		OnMapLoadedCallback {

	private static final int MAX_MARKERS_ON_MAP = 1500;
	private static final float ZOOM_THRESHOLD = 12.0f;
	public static final String TAG = MapController.class.getSimpleName();

	DBWrapper mDBWrapper;
	private GoogleMap mGoogleMap;

	private ArrayList<MapMarker> mSubExitMarkers;
	private ArrayList<MapMarker> mVisibleMarkers;
	private ArrayList<MapMarker> mSubStationMarkers;

	private Context mContext;
	private VisibleRegion mVisibleRegion;
	private float mLastZoom;

	public MapController(Context pContext, GoogleMap pMap) {
		mContext = pContext;
		mDBWrapper = new DBWrapper(pContext);
		mDBWrapper.open();
		if (mDBWrapper.mDb == null) {
			throw new IllegalStateException("Unable to open Database");
		}
		mGoogleMap = pMap;
		mVisibleMarkers = new ArrayList<MapMarker>();
		mSubExitMarkers = new ArrayList<MapMarker>();
		mSubStationMarkers = new ArrayList<MapMarker>();
		Cursor c = mDBWrapper.getAllportals();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			SubStationExit exit = new SubStationExit(c);
			double latitude = c.getDouble(c
					.getColumnIndexOrThrow(DBWrapper.PORTALS_LAT_COLUMN));
			double longitude = c.getDouble(c
					.getColumnIndexOrThrow(DBWrapper.PORTALS_LON_COLUMN));
			LatLng mMarkerCoordinates = new LatLng(latitude, longitude);
			MapMarker mMarker = new MapMarker(mMarkerCoordinates, null, exit);
			mSubExitMarkers.add(mMarker);
			c.moveToNext();
		}
		c = mDBWrapper.getAllstations();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			SubStation station = SubStation.fromCursor(c);
			double latitude = station.getLat();
			double longitude = station.getLon();
			LatLng mStationCoordinates = new LatLng(latitude, longitude);
			MapMarker mMarker = new MapMarker(mStationCoordinates, station,
					null);
			mSubStationMarkers.add(mMarker);
			c.moveToNext();
		}
		mLastZoom = mGoogleMap.getCameraPosition().zoom;
	}

	private void cleanupInvisibleMarkers(VisibleRegion pVisibleRegion) {
		Log.d(TAG, "Cleanup started...");
		float mCameraZoom = mGoogleMap.getCameraPosition().zoom;
		if (mCameraZoom < ZOOM_THRESHOLD && mLastZoom >= ZOOM_THRESHOLD
				|| mCameraZoom > ZOOM_THRESHOLD && mLastZoom <= ZOOM_THRESHOLD) {
			// we just passed threshold
			removeAllVisibleMarkers();
			return;
		}

		LatLngBounds regionBounds = pVisibleRegion.latLngBounds;
		ArrayList<MapMarker> candidatesToDelete = new ArrayList<MapMarker>();
		for (MapMarker visibleMarker : mVisibleMarkers) {
			if (isMarkerOffRegion(regionBounds, visibleMarker)) {
				candidatesToDelete.add(visibleMarker);
				visibleMarker.mMarker.remove();
			}
		}
		mVisibleMarkers.removeAll(candidatesToDelete);
		Log.d(TAG, "Cleanup finished...");
	}

	private synchronized void removeAllVisibleMarkers() {
		for (MapMarker marker : mVisibleMarkers) {
			marker.mMarker.remove();
		}
		mVisibleMarkers.clear();
	}

	private void addVisibleMarkers(VisibleRegion pVisibleRegion) {
		Log.d(TAG, "Add visible markers started...");
		LatLngBounds regionBounds = pVisibleRegion.latLngBounds;
		ArrayList<MapMarker> candidatesToAdd = new ArrayList<MapMarker>();
		float mCameraZoom = mGoogleMap.getCameraPosition().zoom;

		if (mCameraZoom > ZOOM_THRESHOLD) {
			for (MapMarker candidateMarker : mSubExitMarkers) {
				if (isMarkerInRegion(regionBounds, candidateMarker)
						&& !isMarkerOnMap(candidateMarker)) {
					candidatesToAdd.add(candidateMarker);
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions
							.draggable(false)
							.anchor(0.5f, 0.5f)
							.position(candidateMarker.mCoordinates);
					if(candidateMarker.getSubstationExit().direction.equals("in"))
						mMarkerOptions.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.entrance));
					else 
						mMarkerOptions.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.exit));
					candidateMarker.mMarker = mGoogleMap
							.addMarker(mMarkerOptions);
//					this.mVisibleMarkers.add(candidateMarker);
					if (candidatesToAdd.size() + mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
						break;
					}
				}
			}
		} else {
			for (MapMarker candidateMarker : mSubStationMarkers) {
				if (isMarkerInRegion(regionBounds, candidateMarker)
						&& !isMarkerOnMap(candidateMarker)) {
					candidatesToAdd.add(candidateMarker);
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions
							.draggable(false)
							.anchor(0.5f, 0.5f)
							.position(candidateMarker.mCoordinates)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.underground));
					candidateMarker.mMarker = mGoogleMap
							.addMarker(mMarkerOptions);
//					this.mVisibleMarkers.add(candidateMarker);
					if (candidatesToAdd.size() + mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
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
		for (MapMarker marker : mVisibleMarkers) {
			if (marker.compareWith(candidateMarker)) {
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
	}

	private boolean isMarkerInRegion(LatLngBounds regionBounds,
			MapMarker visiblePointer) {
		return regionBounds.contains(visiblePointer.mCoordinates);
	}

	public void renderMap() {
		float mCameraZoom = mGoogleMap.getCameraPosition().zoom;
		if (mCameraZoom < ZOOM_THRESHOLD && mLastZoom >= ZOOM_THRESHOLD
				|| mCameraZoom > ZOOM_THRESHOLD && mLastZoom <= ZOOM_THRESHOLD) {
			// we just passed threshold
			removeAllVisibleMarkers();
		}

		LatLngBounds visibleRegion = mGoogleMap.getProjection()
				.getVisibleRegion().latLngBounds;

		if (mCameraZoom > ZOOM_THRESHOLD) {
			for (MapMarker marker : mSubExitMarkers) {
				if (isMarkerInRegion(visibleRegion, marker)) {
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions.draggable(false).anchor(0.5f, 0.5f)
							.position(marker.mCoordinates);
					if (marker.getSubstationExit().direction.equals("in"))
						mMarkerOptions.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.entrance));
					else
						mMarkerOptions.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.exit));
					marker.mMarker = mGoogleMap.addMarker(mMarkerOptions);
					this.mVisibleMarkers.add(marker);
					if (mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
						break;
					}
				}
			}
		} else {
			for (MapMarker marker : mSubStationMarkers) {
				if (isMarkerInRegion(visibleRegion, marker)) {
					MarkerOptions mMarkerOptions = new MarkerOptions();
					mMarkerOptions
							.draggable(false)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.underground))
							.anchor(0.5f, 0.5f).position(marker.mCoordinates);
					marker.mMarker = mGoogleMap.addMarker(mMarkerOptions);
					this.mVisibleMarkers.add(marker);
					if (mVisibleMarkers.size() >= MAX_MARKERS_ON_MAP) {
						break;
					}
				}
			}
		}
	}

	private Bitmap getMarkerBitmap(int resourceId) {
		return BitmapFactory
				.decodeResource(mContext.getResources(), resourceId);
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
		mVisibleRegion = mGoogleMap.getProjection().getVisibleRegion();
		addVisibleMarkers(mGoogleMap.getProjection().getVisibleRegion());
		mLastZoom = mGoogleMap.getCameraPosition().zoom;
	}

	public void release() {
		mGoogleMap = null;
	}

	@Override
	public void onMapLoaded() {
		this.renderMap();
	}

	public void showPOI(Address poi) {
		// TODO Auto-generated method stub
		LatLng poiCoordinates = new LatLng(poi.getLatitude(),
				poi.getLongitude());
		double bestDistance = 10000d;
		MapMarker bestMarker = mSubExitMarkers.get(0);
		bestDistance = distance(bestMarker.mCoordinates, poiCoordinates);
		for (MapMarker marker : mSubExitMarkers) {
			if (distance(marker.mCoordinates, poiCoordinates) < bestDistance) {
				bestMarker = marker;
				bestDistance = distance(marker.mCoordinates, poiCoordinates);
			}
		}
		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				bestMarker.mCoordinates, 17f));
		// renderMap();
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

	public void showExit(SubStationExit exit) {
		// TODO Auto-generated method stub
		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				exit.latlng, 17f));
	}
}
