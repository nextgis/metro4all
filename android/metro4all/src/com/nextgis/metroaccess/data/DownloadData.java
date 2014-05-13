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
package com.nextgis.metroaccess.data;

import com.nextgis.metroaccess.DataDownloader;
import com.nextgis.metroaccess.R;

import android.content.Context;
import android.os.Handler;

public class DownloadData {
	private GraphDataItem m_oItem;
	private String m_sURL;
	private Context m_Context;
	private Handler m_EventReceiver;
	
	public DownloadData(Context Context, GraphDataItem oItem, String sURL, Handler eventReceiver) {
		this.m_oItem = oItem;
		this.m_sURL = sURL;
		this.m_Context = Context;
		this.m_EventReceiver = eventReceiver;
	}
	
	public void OnDownload(){
		DataDownloader uploader = new DataDownloader(m_Context, m_oItem, m_Context.getResources().getString(R.string.sDownLoading) + "\n(" + m_oItem.GetLocaleName() + ")", m_EventReceiver);
		uploader.execute(m_sURL);			
	}
}
