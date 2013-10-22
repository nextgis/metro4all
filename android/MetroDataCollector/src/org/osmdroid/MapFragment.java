// Created by plusminus on 00:23:14 - 03.10.2008
package org.osmdroid;

import org.osmdroid.constants.OpenStreetMapConstants;
import org.osmdroid.samplefragments.BaseSampleFragment;
import org.osmdroid.samplefragments.SampleFactory;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.nextgis.metro4all.GoodGuy.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

/**
 * Default map view activity.
 * 
 * @author Marc Kurtz
 * @author Manuel Stahl
 * 
 */
public class MapFragment extends Fragment implements OpenStreetMapConstants
{
    // ===========================================================
    // Constants
    // ===========================================================

    private static final int DIALOG_ABOUT_ID = 1;

    private static final int MENU_SAMPLES = Menu.FIRST + 1;
    private static final int MENU_ABOUT = MENU_SAMPLES + 1;

    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id

    // ===========================================================
    // Fields
    // ===========================================================

    private SharedPreferences mPrefs;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private MinimapOverlay mMinimapOverlay;
	private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private ResourceProxy mResourceProxy;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        setMapView(new MapView(inflater.getContext(), 256, mResourceProxy));
        getMapView().setUseSafeCanvas(true);
        setHardwareAccelerationOff();
        return getMapView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setHardwareAccelerationOff()
    {
        // Turn off hardware acceleration here, or in manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getMapView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        // mResourceProxy = new ResourceProxyImpl(getActivity().getApplicationContext());

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // only do static initialisation if needed
        if (CloudmadeUtil.getCloudmadeKey().length() == 0) {
            CloudmadeUtil.retrieveCloudmadeKey(context.getApplicationContext());
        }

        
        this.mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                getMapView());
        this.mLocationOverlay = new MyLocationNewOverlay(context, new GpsMyLocationProvider(context),
                getMapView());

        mMinimapOverlay = new MinimapOverlay(getActivity(), getMapView().getTileRequestCompleteHandler());
		mMinimapOverlay.setWidth(dm.widthPixels / 5);
		mMinimapOverlay.setHeight(dm.heightPixels / 5);

		mScaleBarOverlay = new ScaleBarOverlay(context);
		mScaleBarOverlay.setCentred(true);
		mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mRotationGestureOverlay = new RotationGestureOverlay(context, getMapView());
		mRotationGestureOverlay.setEnabled(false);

        getMapView().setBuiltInZoomControls(true);
        getMapView().setMultiTouchControls(true);
        getMapView().getOverlays().add(this.mLocationOverlay);
        getMapView().getOverlays().add(this.mCompassOverlay);
        getMapView().getOverlays().add(this.mMinimapOverlay);
		getMapView().getOverlays().add(this.mScaleBarOverlay);
        getMapView().getOverlays().add(this.mRotationGestureOverlay);

        getMapView().getController().setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 1));
        getMapView().scrollTo(mPrefs.getInt(PREFS_SCROLL_X, 0), mPrefs.getInt(PREFS_SCROLL_Y, 0));

		mLocationOverlay.enableMyLocation(new GpsMyLocationProvider(context));
		mCompassOverlay.enableCompass(new InternalCompassOrientationProvider(context));

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause()
    {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, getMapView().getTileProvider().getTileSource().name());
        edit.putInt(PREFS_SCROLL_X, getMapView().getScrollX());
        edit.putInt(PREFS_SCROLL_Y, getMapView().getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, getMapView().getZoomLevel());
        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_COMPASS, mCompassOverlay.isCompassEnabled());
        edit.commit();

        this.mLocationOverlay.disableMyLocation();
        this.mCompassOverlay.disableCompass();

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            getMapView().setTileSource(tileSource);
        } catch (final IllegalArgumentException ignore) {
        }
        if (mPrefs.getBoolean(PREFS_SHOW_LOCATION, false)) {
			this.mLocationOverlay.enableMyLocation(new GpsMyLocationProvider(getActivity()));
        }
        if (mPrefs.getBoolean(PREFS_SHOW_COMPASS, false)) {
			this.mCompassOverlay.enableCompass(new InternalCompassOrientationProvider(getActivity()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Put overlay items first
        getMapView().getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, getMapView());

        // Put samples next
		SubMenu samplesSubMenu = menu.addSubMenu(0, MENU_SAMPLES, Menu.NONE, "Samples")
				.setIcon(android.R.drawable.ic_menu_gallery);
		SampleFactory sampleFactory = SampleFactory.getInstance();
		for (int a = 0; a < sampleFactory.count(); a++) {
			final BaseSampleFragment f = sampleFactory.getSample(a);
			samplesSubMenu.add(f.getSampleTitle()).setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							startSampleFragment(f);
							return true;
						}
					});
		}

        // Put "About" menu item last
        menu.add(0, MENU_ABOUT, Menu.CATEGORY_SECONDARY, R.string.about).setIcon(
                android.R.drawable.ic_menu_info_details);

        super.onCreateOptionsMenu(menu, inflater);
    }

	protected void startSampleFragment(Fragment fragment) {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().hide(this).add(android.R.id.content, fragment, "SampleFragment")
				.addToBackStack(null).commit();
	}

    @Override
    public void onPrepareOptionsMenu(final Menu pMenu)
    {
        getMapView().getOverlayManager().onPrepareOptionsMenu(pMenu, MENU_LAST_ID, getMapView());
        super.onPrepareOptionsMenu(pMenu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (getMapView().getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, getMapView()))
			return true;

		switch (item.getItemId()) {
		case MENU_ABOUT:
			getActivity().showDialog(DIALOG_ABOUT_ID);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @return the mMapView
	 */
	public MapView getMapView() {
		return mMapView;
	}

	/**
	 * @param mMapView the mMapView to set
	 */
	public void setMapView(MapView mMapView) {
		this.mMapView = mMapView;
	}

    // @Override
    // public boolean onTrackballEvent(final MotionEvent event) {
    // return this.mMapView.onTrackballEvent(event);
    // }
}
