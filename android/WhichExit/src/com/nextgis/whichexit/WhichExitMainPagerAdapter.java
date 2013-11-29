package com.nextgis.whichexit;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;


public class WhichExitMainPagerAdapter extends FragmentPagerAdapter {

	Context mContext;
	
	public WhichExitMainPagerAdapter(Context pContext) {
		super(((FragmentActivity) pContext).getSupportFragmentManager());
		mContext = pContext;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		
		switch(position) {
		case 0: {
//			GoogleMapOptions options = new GoogleMapOptions();
//			options.compassEnabled(false)
//			.rotateGesturesEnabled(false)
//			.tiltGesturesEnabled(false);
			MapFragment mf = new MapFragment();
			return mf;
		}
		case 1: {
			Fragment fragment = new Fragment();
			return fragment;
		}
		default: {
			throw new IllegalStateException("Invalid position");
		}
		}

	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContext.getString(R.string.map).toUpperCase(l);
		case 1:
			return mContext.getString(R.string.poi_list).toUpperCase(l);
		}
		return null;
	}

}
