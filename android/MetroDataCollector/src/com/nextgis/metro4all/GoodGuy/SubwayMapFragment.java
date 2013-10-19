package com.nextgis.metro4all.GoodGuy;

import org.osmdroid.MapFragment;

import android.content.Context;
import android.os.Bundle;

public class SubwayMapFragment extends MapFragment {

	@Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
        SubStationsOverlay subStations = new SubStationsOverlay(context.getResources().getDrawable(R.drawable.ic_subwaystation_msk), context, getActivity());
        getMapView().getOverlays().add(subStations);
    }
}
