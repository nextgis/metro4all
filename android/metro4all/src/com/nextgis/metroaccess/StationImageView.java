/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Authors:  Baryshnikov Dmitriy aka Bishop (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2013-2015 NextGIS
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/
package com.nextgis.metroaccess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nextgis.metroaccess.data.StationItem;

import java.io.File;

import static com.nextgis.metroaccess.Constants.BUNDLE_STATIONID_KEY;
import static com.nextgis.metroaccess.Constants.PARAM_ROOT_ACTIVITY;
import static com.nextgis.metroaccess.Constants.PARAM_SCHEME_PATH;
import static com.nextgis.metroaccess.Constants.PORTAL_MAP_RESULT;

public class StationImageView extends SherlockActivity {
	private WebView mWebView;
    private Bundle bundle;

	private String msPath;
    private boolean isCrossReference = false;
    private boolean mIsRootActivity, isForLegend;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.station_image_view);
 
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load view
        mWebView = (WebView) findViewById(R.id.webView);
        // (*) this line make uses of the Zoom control
        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        bundle = getIntent().getExtras();
        if(bundle!= null) {
            // PARAM_ROOT_ACTIVITY - determine is it called from StationMapActivity or StationExpandableListAdapter
            mIsRootActivity = bundle.getBoolean(PARAM_ROOT_ACTIVITY);
            isCrossReference = bundle.containsKey(PARAM_ROOT_ACTIVITY); // if PARAM_ROOT_ACTIVITY not contains, it called from another
            msPath = bundle.getString(PARAM_SCHEME_PATH);

            StationItem station = MainActivity.GetGraph().GetStation(bundle.getInt(BUNDLE_STATIONID_KEY, 0));
            String title = station == null ? getString(R.string.sFileNotFound) : String.format(getString(R.string.sSchema), station.GetName());
            setTitle(title);
        } else {
            isForLegend = true;
            setTitle(R.string.sLegend);

            Tracker t = ((Analytics) getApplication()).getTracker();
            t.setScreenName(Analytics.SCREEN_LAYOUT + " " + Analytics.LEGEND);
            t.send(new HitBuilders.AppViewBuilder().build());
        }

        if (!loadImage()) {
            mWebView.setVisibility(View.GONE);
            findViewById(R.id.tvLayoutError).setVisibility(View.VISIBLE);
        }
    }
	
	protected boolean loadImage(){
        Bitmap BitmapOfMyImage;
        String sFolder, sName;

        if (isForLegend) {
            BitmapOfMyImage = BitmapFactory.decodeResource(getResources(), R.raw.schemes_legend);
            sFolder = "/android_res/raw";
            sName = "schemes_legend.png";

            mWebView.clearCache(true);
        } else {
            File f = new File(msPath);

            if (!f.exists()) {
                return false;
            } else {
                BitmapOfMyImage = BitmapFactory.decodeFile(msPath);
                sFolder = f.getParent();
                sName = f.getName();
            }
        }

		String sPath = "file://" + sFolder + "/";

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double deviceRatio = 1.0 * metrics.heightPixels / metrics.widthPixels;
        double imageRatio = 1.0 * BitmapOfMyImage.getHeight() / BitmapOfMyImage.getWidth();
        String fix = deviceRatio > imageRatio ? "width=\"100%\" height=\"auto\"" : "width=\"auto\" height=\"100%\"";

        // background-color: rgba(0, 0, 0, 0.01); is a fix for showing image on some webkit versions
        String sCmd = "<html><center><img style=\"background-color:rgba(0,0,0,0.01);position:absolute;margin:auto;top:0;left:0;right:0;bottom:0;max-width:100%;max-height:100%;\" " +
                fix + " src=\"" + sName + "\" alt=\"TEST\"></center></html>";

        mWebView.loadDataWithBaseURL(sPath, sCmd, "text/html", "utf-8", "");

        return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getSupportMenuInflater();
        infl.inflate(R.menu.menu_station_list, menu);
        menu.findItem(R.id.btn_legend).setEnabled(!isForLegend).setVisible(!isForLegend);
        menu.findItem(R.id.btn_map).setEnabled(!isForLegend && isCrossReference).setVisible(!isForLegend && isCrossReference);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_LAYOUT, Analytics.BACK, Analytics.SCREEN_LAYOUT);

                // app icon in action bar clicked; go home
                //Intent intent = new Intent(this, StationListView.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            	finish();
                return true;
            case R.id.btn_map:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_LAYOUT, Analytics.BTN_MAP, Analytics.ACTION_BAR);

                if (mIsRootActivity) {
                    Intent intentMap = new Intent(this, StationMapActivity.class);
                    intentMap.putExtras(bundle);
                    intentMap.putExtra(PARAM_ROOT_ACTIVITY, false);
                    startActivityForResult(intentMap, PORTAL_MAP_RESULT);
                } else
                    finish();
                return true;
            case R.id.btn_legend:
                ((Analytics) getApplication()).addEvent(Analytics.SCREEN_LAYOUT, Analytics.LEGEND, Analytics.ACTION_BAR);
                onLegendClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        ((Analytics) getApplication()).addEvent(Analytics.SCREEN_LAYOUT, Analytics.BACK, Analytics.SCREEN_LAYOUT);

        super.onBackPressed();
    }

    public void onLegendClick() {
        Intent intentView = new Intent(this, StationImageView.class);
        startActivity(intentView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PORTAL_MAP_RESULT:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
