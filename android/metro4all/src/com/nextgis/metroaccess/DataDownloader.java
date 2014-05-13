/******************************************************************************
 * Project:  Metro Access
 * Purpose:  Routing in subway for disabled.
 * Author:   Baryshnikov Dmitriy (aka Bishop), polimax@mail.ru
 ******************************************************************************
*   Copyright (C) 2013,2014 NextGIS
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import com.nextgis.metroaccess.data.GraphDataItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;


public class DataDownloader extends AsyncTask<String, String, String> {
    private Context m_oContext;
    private boolean m_bSucces = true;
    private ProgressDialog m_oDownloadDialog;
    private String m_sDownloadDialogMsg;
    private Handler m_oEventReceiver;
    private String  m_sTmpOutFile;
    private GraphDataItem  m_oItem;
	
    public DataDownloader(Context c, GraphDataItem oItem, String sMsg, Handler eventReceiver) {        
        super();
        m_oContext = c;
       	m_oDownloadDialog = null;
        m_oEventReceiver = eventReceiver;
        m_sDownloadDialogMsg = sMsg;  
        m_oItem = oItem;
        
        File dir = m_oContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File zipFile = new File(dir, m_oItem.GetPath() + ".zip");		

        m_sTmpOutFile = zipFile.getAbsolutePath();
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
		m_oDownloadDialog = new ProgressDialog(m_oContext);
		m_oDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_oDownloadDialog.setMessage(m_sDownloadDialogMsg);
		m_oDownloadDialog.setCancelable(false);
		m_oDownloadDialog.show();
    } 
    
    @Override
    protected String doInBackground(String... aurl) {
    	int count;
    	try {
    		URL url = new URL(aurl[0]);
    		URLConnection connetion = url.openConnection();
    		connetion.connect();
    		int lenghtOfFile = connetion.getContentLength();
    		if(lenghtOfFile <= 0){
                Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
                bundle.putInt(MainActivity.BUNDLE_EVENTSRC_KEY, 2);
                bundle.putString(MainActivity.BUNDLE_MSG_KEY, m_oContext.getString(R.string.sNetworkUnreachErr));
                
                Message msg = new Message();
                msg.setData(bundle);
                if(m_oEventReceiver != null){
                	m_oEventReceiver.sendMessage(msg);
                }    			
    		}
    		InputStream input = new BufferedInputStream(url.openStream());
    		OutputStream output = new FileOutputStream(m_sTmpOutFile);
    		byte data[] = new byte[1024];
    		long total = 0;
    		while ((count = input.read(data)) != -1) {
    			total += count;
    			publishProgress(""+(int)((total*100)/lenghtOfFile));
    			output.write(data, 0, count);
    		}
    		output.close();
    		input.close();
    	} catch (Exception e) {
    		m_bSucces = false;
    		
            Bundle bundle = new Bundle();
            bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
            bundle.putInt(MainActivity.BUNDLE_EVENTSRC_KEY, 2);
            bundle.putString(MainActivity.BUNDLE_MSG_KEY, e.getLocalizedMessage());
            
            Message msg = new Message();
            msg.setData(bundle);
            if(m_oEventReceiver != null){
            	m_oEventReceiver.sendMessage(msg);
            } 		
    		
    	}
    	return null;
	}
    
    protected void onProgressUpdate(String... progress) {
    	m_oDownloadDialog.setProgress(Integer.parseInt(progress[0]));
    }
    
    @Override
    protected void onPostExecute(String unused) {
    	m_oDownloadDialog.dismiss();
    	if(m_bSucces){
    		try {
    			unzip();                	
    		} catch (IOException e) {
    			e.printStackTrace();
    			
                Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);
                bundle.putInt(MainActivity.BUNDLE_EVENTSRC_KEY, 2);
                bundle.putString(MainActivity.BUNDLE_MSG_KEY, e.getLocalizedMessage());
                
                Message msg = new Message();
                msg.setData(bundle);
                if(m_oEventReceiver != null){
                	m_oEventReceiver.sendMessage(msg);
                }    			
    		}
    	}
	}
    
    public void unzip() throws IOException {
    	m_oDownloadDialog = new ProgressDialog(m_oContext);
    	m_oDownloadDialog.setMessage(m_oContext.getResources().getString(R.string.sZipExtractionProcess));
    	m_oDownloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	m_oDownloadDialog.setCancelable(false);
    	m_oDownloadDialog.show();
    	new UnZipTask(m_oItem, m_oContext.getExternalFilesDir(MainActivity.GetRouteDataDir()) + File.separator + m_oItem.GetPath()).execute(m_sTmpOutFile);
    }
    
    private class UnZipTask extends AsyncTask<String, Void, Boolean> {
    	private GraphDataItem  m_oItem;
    	private String msPath;
    	
        public UnZipTask(GraphDataItem oItem, String sSubPath) {        
            super();
            m_oItem = oItem;
            msPath = sSubPath;
        }
        
    	@Override
    	protected Boolean doInBackground(String... params) {
    		
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.BUNDLE_EVENTSRC_KEY, 2);
    
    		String filePath = params[0];
    		File archive = new File(filePath);
    		try {
    			DeleteRecursive(new File(msPath));
    			ZipFile zipfile = new ZipFile(archive);
    			for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements();) {
    				ZipEntry entry = (ZipEntry) e.nextElement();
    				unzipEntry(zipfile, entry, msPath);
    			}
    			zipfile.close();
    			archive.delete();
    		
    			JSONObject oJSONRoot = new JSONObject();

            	oJSONRoot.put("name", m_oItem.GetName());
				oJSONRoot.put("name_" + Locale.getDefault().getLanguage(), m_oItem.GetLocaleName());
				oJSONRoot.put("ver", m_oItem.GetVersion());
				oJSONRoot.put("directed", m_oItem.GetDirected());            
            
	            String sJSON = oJSONRoot.toString();
	            File file = new File(msPath, MainActivity.GetMetaFileName());
	            if(MainActivity.writeToFile(file, sJSON)){
	            	//store data
	            	//create sqlite db
	            	//Creating and saving the graph
		            bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, false);
	            } 
	            else{
		            bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);            	
	                bundle.putString(MainActivity.BUNDLE_MSG_KEY, "write failed");
	            }
    		
			} 
    		catch (JSONException e) {
				e.printStackTrace();
	            bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);            	
				bundle.putString(MainActivity.BUNDLE_MSG_KEY, e.getLocalizedMessage());
				return false;
			}
			catch (Exception e) {
	            bundle.putBoolean(MainActivity.BUNDLE_ERRORMARK_KEY, true);            	
	            bundle.putString(MainActivity.BUNDLE_MSG_KEY, e.getLocalizedMessage());
				return false;
			}    		
                  
            Message msg = new Message();
            msg.setData(bundle);
            if(m_oEventReceiver != null){
            	m_oEventReceiver.sendMessage(msg);
            }   		
    		return true;
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		m_oDownloadDialog.dismiss();
    	}
    	
    	private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {
    		if (entry.isDirectory()) {
    			createDir(new File(outputDir, entry.getName()));
    			return;
    		}
    		File outputFile = new File(outputDir, entry.getName());
    		if (!outputFile.getParentFile().exists()) {
    			createDir(outputFile.getParentFile());
    		}

    		BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
    		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
    		try {
    			byte[] _buffer = new byte[1024];
    			copyStream(inputStream, outputStream, _buffer, 1024);
    		} finally {
    			outputStream.flush();
    			outputStream.close();
    			inputStream.close();
    		}
    	}
    	
    	private void copyStream( InputStream is, OutputStream os, byte[] buffer, int bufferSize ) throws IOException {
			try {
				for (;;) {
					int count = is.read( buffer, 0, bufferSize );
					if ( count == -1 ) { break; }
					os.write( buffer, 0, count );
				}
			} catch ( IOException e ) {
				throw e;
			}
		}    	
    	
    	private void createDir(File dir) {
    		if (dir.exists()) {
    			return;
    		}
    		if (!dir.mkdirs()) {
    			throw new RuntimeException("Can not create dir " + dir);
    		}
    	}
    	
    	private void DeleteRecursive(File fileOrDirectory) {
    	    if (fileOrDirectory.isDirectory())
    	        for (File child : fileOrDirectory.listFiles())
    	            DeleteRecursive(child);

    	    fileOrDirectory.delete();
    	}
    }
}
