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

import android.content.Context;
import android.os.Handler;

public class DownloadData {
	private String sName;
	private String sPath;
	private String sLocName;
	private String sURL;
	private int nVer;
	private boolean bDirected;
	private Context Context;
	private Handler eventReceiver;
	
	public DownloadData(Context Context, String sName, String sPath, String sLocName, String sURL, int nVer, boolean bDirected, Handler eventReceiver) {
		this.sName = sName;
		this.sPath = sPath;
		this.sLocName = sLocName;
		this.sURL = sURL;
		this.nVer = nVer;
		this.bDirected = bDirected;
		this.Context = Context;
		this.eventReceiver = eventReceiver;
	}
	
	public void OnDownload(){
		DataDownloader uploader = new DataDownloader(Context, sPath, sName, sLocName, nVer, bDirected, Context.getResources().getString(R.string.sDownLoading) + "\n(" + sLocName + ")", eventReceiver);
		uploader.execute(sURL);			
	}
}
