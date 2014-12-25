/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Author:   Dmitry Baryshnikov (polimax@mail.ru), Stanislav Petriakov
 ******************************************************************************
*   Copyright (C) 2014 NextGIS
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class ButtonListAdapter extends BaseAdapter {

	protected Context m_oContext;
	protected LayoutInflater m_oInfalInflater;
	protected int m_sFromStationLine;
    protected int m_sToStationLine;
	protected String m_sFromStationName;
	protected String m_sFromEntranceName;
	protected String m_sToStationName;
	protected String m_sToEntranceName;
    protected String sNotSet;
    protected ImageButton ibtnLocateFrom;
	protected View.OnClickListener ibtnLocateFromListener;

	public ButtonListAdapter(Context c) {
		this.m_oContext = c;
		this.m_oInfalInflater = (LayoutInflater) m_oContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		sNotSet = (String) m_oContext.getResources().getText(R.string.sNotSet);
		this.m_sFromStationName = sNotSet;
		this.m_sToStationName = sNotSet;
		this.m_sFromEntranceName = sNotSet;
		this.m_sToEntranceName = sNotSet;
	}
	
	@Override
	public int getCount() {
		return 3;//TODO: add "set my conditions" and "set from map"
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch(position){
		case 0://create from pane
			return CreateFromPane(convertView);
		case 1://create to pane
			return CreateToPane(convertView);
		case 2://create from map pane
			return CreateAdds(convertView, (String)m_oContext.getResources().getText(R.string.sLimits));
		case 3://create conditions pane
			break;
		}
		return null;
	}
	
	protected View CreateFromPane(View convertView){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.fromto_layout, null);
		}

        if (ibtnLocateFrom == null) {   // add "locate me" button
            ibtnLocateFrom = new ImageButton(convertView.getContext());
            ibtnLocateFrom.setImageResource(R.drawable.ic_action_location_found);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            ibtnLocateFrom.setLayoutParams(lp);
            ibtnLocateFrom.setBackgroundResource(0);
            ibtnLocateFrom.setFocusable(false);
            ibtnLocateFrom.setOnClickListener(ibtnLocateFromListener);
            ((LinearLayout) convertView).addView(ibtnLocateFrom);
        }

		ImageView ivMarkIcon = (ImageView)convertView.findViewById(R.id.ivMarkIcon);
		ivMarkIcon.setImageResource(R.drawable.ic_geomarker_a);

        ImageView ivSmallIcon = (ImageView)convertView.findViewById(R.id.ivSmallIcon);

        File imgFile = new File(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/icons", m_sFromStationLine + "5.png");
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            myBitmap = Bitmap.createBitmap(myBitmap, myBitmap.getWidth()/4, 0, myBitmap.getWidth()/2, myBitmap.getHeight(), matrix, true);

            ivSmallIcon.setImageBitmap(myBitmap);
            ivSmallIcon.setVisibility(View.VISIBLE);
        } else
            ivSmallIcon.setVisibility(View.GONE);

		ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
//		ivIcon.setImageResource(R.drawable.ic_geomarker_a);
        ivIcon.setVisibility(View.GONE);
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(R.string.sFromStation);

		String sStationName = !m_sFromStationName.equals(sNotSet) ? m_sFromStationName : m_oContext.getResources().getText(R.string.sStationName) + ": " + m_sFromStationName;
		TextView tvStationName = (TextView)convertView.findViewById(R.id.tvStationName);
		tvStationName.setText(sStationName);
		
		String sEntranceName = !m_sFromEntranceName.equals(sNotSet) ? m_sFromEntranceName : m_oContext.getResources().getText(R.string.sEntranceName) + ": " + m_sFromEntranceName;
		TextView tvEntranceName = (TextView)convertView.findViewById(R.id.tvEntranceName);
		tvEntranceName.setText(sEntranceName);
		
		return convertView;
	}

	protected View CreateToPane(View convertView){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.fromto_layout, null);
		}

        ImageView ivMarkIcon = (ImageView)convertView.findViewById(R.id.ivMarkIcon);
        ivMarkIcon.setImageResource(R.drawable.ic_geomarker_b);

        ImageView ivSmallIcon = (ImageView)convertView.findViewById(R.id.ivSmallIcon);

        File imgFile = new File(MainActivity.GetGraph().GetCurrentRouteDataPath() + "/icons", m_sToStationLine + "5.png");
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            myBitmap = Bitmap.createBitmap(myBitmap, myBitmap.getWidth()/4, 0, myBitmap.getWidth()/2, myBitmap.getHeight(), matrix, true);

            ivSmallIcon.setImageBitmap(myBitmap);
            ivSmallIcon.setVisibility(View.VISIBLE);
        } else
            ivSmallIcon.setVisibility(View.GONE);

        ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
//		ivIcon.setImageResource(R.drawable.ic_geomarker_b);
        ivIcon.setVisibility(View.GONE);
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(R.string.sToStation);

        String sStationName = !m_sToStationName.equals(sNotSet) ? m_sToStationName : m_oContext.getResources().getText(R.string.sStationName) + ": " + m_sToStationName;
		TextView tvStationName = (TextView)convertView.findViewById(R.id.tvStationName);
		tvStationName.setText(sStationName);
		
		String sExitName = !m_sToEntranceName.equals(sNotSet) ? m_sToEntranceName : m_oContext.getResources().getText(R.string.sExitName) + ": " + m_sToEntranceName;
		TextView tvExitName = (TextView)convertView.findViewById(R.id.tvEntranceName);
		tvExitName.setText(sExitName);

		return convertView;
	}	
	
	protected View CreateAdds(View convertView, String sText){
		if (convertView == null) {
			convertView = m_oInfalInflater.inflate(R.layout.additional_layout, null);
		}
		
		TextView tvPaneName = (TextView)convertView.findViewById(R.id.tvPaneName);
		tvPaneName.setText(sText);		

		return convertView;
	}

    public void setFromStationLine(int sFromStationLine) {
        this.m_sFromStationLine = sFromStationLine;
    }

	public String getFromStationName() {
		return m_sFromStationName;
	}

	public void setFromStationName(String sFromStationName) {
		this.m_sFromStationName = sFromStationName;
	}

	public String getFromEntranceName() {
		return m_sFromEntranceName;
	}

	public void setFromEntranceName(String sFromEntranceName) {
		this.m_sFromEntranceName = sFromEntranceName;
	}

    public void setToStationLine(int sToStationLine) {
        this.m_sToStationLine = sToStationLine;
    }

	public String getToStationName() {
		return m_sToStationName;
	}

	public void setToStationName(String sToStationName) {
		this.m_sToStationName = sToStationName;
	}

	public String getToEntranceName() {
		return m_sToEntranceName;
	}

	public void setToEntranceName(String sToEntranceName) {
		this.m_sToEntranceName = sToEntranceName;
	}

    public void clear(){
        this.m_sFromStationName = sNotSet;
        this.m_sToStationName = sNotSet;
        this.m_sFromEntranceName = sNotSet;
        this.m_sToEntranceName = sNotSet;

        notifyDataSetChanged();
    }

    public void setOnLocateFromListener(View.OnClickListener listener) {
        ibtnLocateFromListener = listener;
    }
}
