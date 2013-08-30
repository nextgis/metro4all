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

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;


public class MetaDownloader extends AsyncTask<String, Void, Void> {
    private String msContent;
    private Context moContext;
    private String msError = null;
    private ProgressDialog moDownloadDialog;
    private String msDownloadDialogMsg;
    private Handler moEventReceiver;
    private boolean mbShowProgress;
    private HttpGet moHTTPGet;
	
    public MetaDownloader(Context c, String sMsg, Handler eventReceiver, boolean bShowProgress) {        
        super();
        
        mbShowProgress = bShowProgress;
        moContext = c;
       	moDownloadDialog = null;
        moEventReceiver = eventReceiver;
        msDownloadDialogMsg = sMsg;  
        moHTTPGet = null;
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	
    	if(mbShowProgress){
    		moDownloadDialog = new ProgressDialog(moContext);
    		moDownloadDialog.setMessage(msDownloadDialogMsg);
    		moDownloadDialog.show();
    	}
    } 

    @Override
    protected Void doInBackground(String... urls) {
        if(IsNetworkAvailible(moContext))
        {    	
	        try {
	        	String sURL = urls[0];
	        	
	        	moHTTPGet = new HttpGet(sURL);
	            
	            Log.d(MainActivity.TAG, "HTTPGet URL " + sURL);

	            HttpClient Client = new DefaultHttpClient();
	            HttpResponse response = Client.execute(moHTTPGet);
	            HttpEntity entity = response.getEntity();
	                        
	            if(moEventReceiver != null){
		            Bundle bundle = new Bundle();
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
						bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, false);
						msContent = EntityUtils.toString(entity, HTTP.UTF_8);
			            bundle.putString(MainActivity.BUNDLE_PAYLOAD_KEY, msContent);
			            bundle.putInt(MainActivity.BUNDLE_EVENTSRC_KEY, 1);
					}
					else{
						bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
						bundle.putString(MainActivity.BUNDLE_MSG_KEY, moContext.getString(R.string.sNetworkGetErr));
					}				
					
		            Message oMsg = new Message();
		            oMsg.setData(bundle);
	            	
	            	moEventReceiver.sendMessage(oMsg);
	            }
	            
	        } catch (ClientProtocolException e) {
	        	msError = e.getMessage();
	            cancel(true);
	        } catch (IOException e) {
	        	msError = e.getMessage();
	            cancel(true);
	        }
        }
        else {
            if(moEventReceiver != null){
                Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
                bundle.putString(MainActivity.BUNDLE_MSG_KEY, moContext.getString(R.string.sNetworkUnreachErr));
                
                Message oMsg = new Message();
                oMsg.setData(bundle);            	
            	moEventReceiver.sendMessage(oMsg);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
    	super.onPostExecute(unused);
    	
    	DismissDowloadDialog();
    	
        if (msError != null) {
            if(moEventReceiver != null){
            	Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
                bundle.putString(MainActivity.BUNDLE_MSG_KEY, msError);
                
                Message oMsg = new Message();
                oMsg.setData(bundle);            	
            	moEventReceiver.sendMessage(oMsg);
            }
        }
    }

    protected void DismissDowloadDialog(){
		if(moDownloadDialog != null){
			moDownloadDialog.dismiss();
		}	
	}
	
	public void Abort(){
		if(moHTTPGet != null)
			moHTTPGet.abort();		
		DismissDowloadDialog();
		this.cancel(true);
	}
	
	static boolean IsNetworkAvailible(Context c)
	{
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);  
        
        NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null){ 		
			int netType = info.getType();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				return info.isConnected();
			} 
			else if (netType == ConnectivityManager.TYPE_MOBILE && !tm.isNetworkRoaming()){
				return info.isConnected();
			} 
		}
		return false;
	}	
}
