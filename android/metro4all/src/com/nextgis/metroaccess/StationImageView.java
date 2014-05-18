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

import java.io.File;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class StationImageView extends SherlockActivity {
	WebView mWebView;
	float width;
    float height;
	float currentHeight;
	String msPath;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.station_image_view);
 
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	    Bundle extras = getIntent().getExtras(); 
	    if(extras != null) {
	
	        // load view
	        mWebView = (WebView)findViewById(R.id.webView);
	        // (*) this line make uses of the Zoom control
	        mWebView.getSettings().setBuiltInZoomControls(true);
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        
	        mWebView.getSettings().setLoadWithOverviewMode(true);
	        mWebView.getSettings().setUseWideViewPort(true);
	        
	        DisplayMetrics displaymetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	        height = displaymetrics.heightPixels;
	        width = displaymetrics.widthPixels;
	        currentHeight = height;
	        
	        msPath = extras.getString("image_path");
	        
	        loadImage();
	        // simply, just load an image
	        //mWebView.loadUrl("file://" + extras.getString("image_path"));
	    }
	}
	
	protected void loadImage(){
		//mWebView.loadUrl("file://" + msPath);
		Bitmap BitmapOfMyImage = BitmapFactory.decodeFile(msPath);  

		File f = new File(msPath);
		
		String sFolder = f.getParent();
		String sName = f.getName();
		String sPath = "file://" + sFolder + "/";
		String sCmd = "<html><center><img src=\"" + sName + "\" vspace=" + (currentHeight / 2 - (BitmapOfMyImage.getHeight() / 2 )) + "></center></html>";
		
		
		mWebView.loadDataWithBaseURL(sPath, sCmd, "text/html", "utf-8", "");
			//This loads the image at the center of thee screen
	    
	}
	
	//this function will set the current height according to screen orientation
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		if(newConfig.equals(Configuration.ORIENTATION_LANDSCAPE)){

			currentHeight=width; 
			loadImage();                 

		}if(newConfig.equals(Configuration.ORIENTATION_PORTRAIT)){

			currentHeight=height;
			loadImage();

		}
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
}
