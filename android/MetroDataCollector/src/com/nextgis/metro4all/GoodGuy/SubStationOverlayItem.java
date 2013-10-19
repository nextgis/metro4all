package com.nextgis.metro4all.GoodGuy;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SubStationOverlayItem extends OverlayItem {

	private SubStation mStation;

	public SubStationOverlayItem(String aUid, String aTitle, String aDescription, GeoPoint aGeoPoint,
			Drawable aMarker, HotspotPlace aHotspotPlace) {
		super(aUid, aTitle, aDescription, aGeoPoint);
		init(aMarker, aHotspotPlace);
	}

	/**
	 * @param aMarker
	 * @param aHotspotPlace
	 */
	private void init(Drawable aMarker, HotspotPlace aHotspotPlace) {
		this.setMarker(aMarker);
		this.setMarkerHotspot(aHotspotPlace);
	}
	
	public SubStationOverlayItem(String aUid, SubStation station, Drawable aMarker, HotspotPlace aHotspotPlace) {
		super(aUid, station.getName(), station.getName_en(), new GeoPoint(station.getLat(), station.getLon()));
		init(aMarker, aHotspotPlace);
		this.mStation = station;
	}

	public void draw(Canvas canvas) {
		//
		
	}

	public SubStation getStation() {
		// TODO Auto-generated method stub
		return mStation;
	}
	
	public View getSubStationPopup(final MapView pMapView, Context pContext, final ISubStationOverlayItemListener listener) {
		LayoutInflater inflater = LayoutInflater.from(pMapView.getContext());
		View popUp = inflater.inflate(R.layout.station_item_popup, null);
		ImageButton moreInfoButton = (ImageButton) popUp
				.findViewById(R.id.stationInfoAction);
		moreInfoButton.setTag(getStation());
		moreInfoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getTag() != null && v.getTag() instanceof SubStation) {
					listener.onShowMoreDetails((SubStation) v.getTag());
				}
			}
		});
		ImageButton closeButton = (ImageButton) popUp
				.findViewById(R.id.stationInfoClose);
		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getTag() != null && v.getTag() instanceof View) {
					// ((View)v.getTag()).setVisibility(View.GONE);
					pMapView.removeView((View) v.getTag());
				}
			}
		});
		closeButton.setTag(popUp);
		TextView tv = (TextView) popUp.findViewById(R.id.stationInfoName);
		tv.setText(getStation().getName());
		pMapView.getController().setCenter(new GeoPoint(getStation().getLat(), getStation().getLon()));

		// popUp.findViewById(R.id.stationEscalatorText).setVisibility(View.GONE);
		// popUp.findViewById(R.id.stationStairsText).setVisibility(View.GONE);
		return popUp;
	}

	public interface ISubStationOverlayItemListener {

		void onShowMoreDetails(SubStation station);
		
	}
}
