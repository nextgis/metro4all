package com.nextgis.metro4all.GoodGuy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.nextgis.metro4all.GoodGuy.SubStationsOverlay.SubStationsOverlayListener;
import com.nextgis.metro4all.GoodGuy.utils.Consts;

public class MapActivity extends ActionBarActivity implements SubStationsOverlayListener{

	private static final int DIALOG_ABOUT_ID = 1;

    // ===========================================================
    // Constructors
    // ===========================================================
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_map);

        // FrameLayout mapContainer = (FrameLayout) findViewById(R.id.map_container);
        // RelativeLayout parentContainer = (RelativeLayout) findViewById(R.id.parent_container);
        FragmentManager fm = this.getSupportFragmentManager();

        SubwayMapFragment mapFragment = new SubwayMapFragment();

        fm.beginTransaction().add(R.id.map_container, mapFragment).commit();
    }

    @Override
    protected Dialog onCreateDialog(final int id)
    {
        Dialog dialog;

        switch (id) {
            case DIALOG_ABOUT_ID:
                return new AlertDialog.Builder(this).setIcon(R.drawable.ic_map)
                        .setTitle(R.string.app_name).setMessage(R.string.about_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int whichButton)
                            {
                                //
                            }
                        }).create();

            default:
                dialog = null;
                break;
        }
        return dialog;
    }

	@Override
	public void onShowMoreDetails(SubStation station) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, GoodDeedActivity.class);
		intent.putExtra(Consts.PARAM_STATION_ID, station.getId());
		startActivity(intent);
	}
}
