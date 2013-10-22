package com.nextgis.metro4all.GoodGuy;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy.bitmap;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;
import org.osmdroid.views.safecanvas.ISafeCanvas;

import com.nextgis.metro4all.GoodGuy.SubStationOverlayItem.ISubStationOverlayItemListener;
import com.nextgis.metro4all.GoodGuy.utils.db.DBHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SubStationsOverlay extends ItemizedOverlay<SubStationOverlayItem>
		implements ItemizedOverlay.OnFocusChangeListener, ISubStationOverlayItemListener {

	private boolean mFocusChanged = false;
	private View mPopupView = null;
	private Context mContext;
	private DBHelper mDB;
	private Cursor stationCursor;
	private SubStationsOverlayListener mListener;

	public SubStationsOverlay(Drawable pDefaultMarker, Context pContext,
			Activity listener) {
		super(pDefaultMarker, new DefaultResourceProxyImpl(pContext));
		this.mContext = pContext;
		if(listener instanceof SubStationsOverlayListener)
			this.mListener = (SubStationsOverlayListener)listener;
		else 
			throw new IllegalStateException(String.format("Activity %s must implement SubStationsOverlayListener interface", listener.getClass().getSimpleName()));
		mDB = new DBHelper(mContext);
		mDB.open();
		populate();
		setOnFocusChangeListener(this);
	}

	@Override
	protected SubStationOverlayItem createItem(int stationId) {
		SubStationOverlayItem item;
		stationCursor = (stationCursor != null) ? stationCursor : mDB
				.getAllstations();
		stationCursor.moveToPosition(stationId);
		// c.moveToFirst();
		SubStation station = SubStation.fromCursor(stationCursor);
		// stationCursor.close();
		item = new SubStationOverlayItem(station.getName(), station, mContext
				.getResources().getDrawable(R.drawable.ic_subwaystation_msk),
				HotspotPlace.CENTER);
		return item;
	}

	@Override
	public void onFocusChanged(ItemizedOverlay<?> overlay, OverlayItem newFocus) {
		mFocusChanged = true;
		getFocus();
	}

	@Override
	protected boolean onTap(int index) {
		setFocus(getItem(index));
		return true;
	}

	@Override
	protected void drawSafe(ISafeCanvas canvas, MapView mapView, boolean shadow) {
		if (mFocusChanged) {
			mFocusChanged = false;

			// Remove any current focus
			if (mPopupView != null)
				mapView.removeView(mPopupView);

			SubStationOverlayItem item = this.getFocus();
			if (item != null) {
				mPopupView = getPopupView(mapView, item);
				MapView.LayoutParams lp = new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						item.getPoint(), MapView.LayoutParams.BOTTOM_CENTER, 0,
						0);
				mapView.addView(mPopupView, lp);
				mPopupView.setVisibility(View.VISIBLE);
			}
		}
		super.drawSafe(canvas, mapView, shadow);
	}

	protected View getPopupView(final MapView mapView,
			SubStationOverlayItem item) {
		return item.getSubStationPopup(mapView, mContext, this);
	}

	@Override
	public void onShowMoreDetails(SubStation station) {
		if (mListener != null) {
			mListener.onShowMoreDetails(station);
		}

	}

	// @Override
	// protected void onDrawItem(ISafeCanvas canvas, SampleOverlayItem item,
	// Point curScreenCoords, final float aMapOrientation) {
	// super.onDrawItem(canvas, item, curScreenCoords, aMapOrientation);
	// }

	@Override
	public int size() {
		stationCursor = (stationCursor != null) ? stationCursor : mDB
				.getAllstations();
		stationCursor.moveToFirst();
		int count = stationCursor.getCount();
		// c.close();
		return count;
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		return false;
	}

	public interface SubStationsOverlayListener {
		public void onShowMoreDetails(SubStation station);
	}
}
