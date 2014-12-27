/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Author:   Baryshnikov Dmitriy (aka Bishop), polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013 NextGIS
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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.File;

import static com.nextgis.metroaccess.Constants.*;

public class StationImageView extends SherlockActivity {
	WebView mWebView;
	float width;
    float height;
//	float currentHeight;
	String msPath;
    boolean isForLegend;
    private boolean isPortrait, isCrossReference = false;
    private boolean mIsRootActivity;
    private int mStationID;
    private boolean mIsPortalIn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.station_image_view);
 
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load view
        mWebView = (WebView) findViewById(R.id.webView);
        // (*) this line make uses of the Zoom control
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
//        currentHeight = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? height : width;
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            // PARAM_ROOT_ACTIVITY - determine is it called from StationMapActivity or StationExpandableListAdapter
            mIsRootActivity = extras.getBoolean(PARAM_ROOT_ACTIVITY);
            isCrossReference = extras.containsKey(PARAM_ROOT_ACTIVITY); // if PARAM_ROOT_ACTIVITY not contains, it called from another
            msPath = extras.getString(PARAM_SCHEME_PATH);
            mStationID = extras.getInt(PARAM_SEL_STATION_ID, 0);
            mIsPortalIn = extras.getBoolean(PARAM_PORTAL_DIRECTION, true);
            setTitle(String.format(getString(R.string.sSchema), MainActivity.GetGraph().GetStation(mStationID).GetName()));
        } else {
            isForLegend = true;
            setTitle(R.string.sLegend);
        }

        loadImage();
        // simply, just load an image
        //mWebView.loadUrl("file://" + extras.getString("image_path"));
    }
	
	protected void loadImage(){
		//mWebView.loadUrl("file://" + msPath);

        Bitmap BitmapOfMyImage;
        String sFolder;
        String sName;

        if (isForLegend) {
            BitmapOfMyImage = BitmapFactory
                    .decodeResource(getResources(), R.raw.schemes_legend);

            sFolder = "/android_res/raw";
            sName = "schemes_legend.png";

            mWebView.clearCache(true);

        } else {
            BitmapOfMyImage = BitmapFactory.decodeFile(msPath);

            File f = new File(msPath);
            sFolder = f.getParent();
            sName = f.getName();
        }

		String sPath = "file://" + sFolder + "/";

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double deviceRatio = 1.0 * metrics.heightPixels / metrics.widthPixels;
        double imageRatio = 1.0 * BitmapOfMyImage.getHeight() / BitmapOfMyImage.getWidth();
        String fix = deviceRatio > imageRatio ? "width=\"100%\" height=\"auto\"" : "width=\"auto\" height=\"100%\"";

        String sCmd = "<html><center><img style=\"position: absolute; margin: auto; top: 0; left: 0; right: 0; bottom: 0; max-width: 100%; max-height: 100%;\" " + fix + " src=\"" + sName + "\"></center></html>";
//		String sCmd = "<html><center><img width=\"100%\" height=\"auto\" src=\"" + sName + "\" vspace=" + (currentHeight / 2 - (BitmapOfMyImage.getHeight() / 2 )) + "></center></html>";

        mWebView.loadDataWithBaseURL(sPath, sCmd, "text/html", "utf-8", "");
			//This loads the image at the center of thee screen
	}
	
	//this function will set the current height according to screen orientation
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		if(newConfig.equals(Configuration.ORIENTATION_LANDSCAPE)){
            isPortrait = false;
//			currentHeight=width;
			loadImage();                 

		}if(newConfig.equals(Configuration.ORIENTATION_PORTRAIT)){
            isPortrait = true;
//			currentHeight=height;
			loadImage();

		}
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
                // app icon in action bar clicked; go home
                //Intent intent = new Intent(this, StationListView.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            	finish();
                return true;
            case R.id.btn_map:
                if (mIsRootActivity) {
                    Intent intentMap = new Intent(this, StationMapActivity.class);
                    intentMap.putExtra(PARAM_SEL_STATION_ID, mStationID);
                    intentMap.putExtra(PARAM_PORTAL_DIRECTION, mIsPortalIn);
                    intentMap.putExtra(PARAM_ROOT_ACTIVITY, false);
                    startActivityForResult(intentMap, PORTAL_MAP_RESULT);
                } else
                    finish();
                return true;
            case R.id.btn_legend:
                onLegendClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
