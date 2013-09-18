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

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_USER_TYPE = "user_type";
	public static final String KEY_PREF_MAX_WIDTH = "max_width";
	public static final String KEY_PREF_WHEEL_WIDTH = "wheel_width";
	public static final String KEY_PREF_DOWNLOAD_PATH = "download_path";
	
	ListPreference mlsNaviType;
	EditTextPreference metWheelWidth;
	EditTextPreference metMaxWidth;
	EditTextPreference metDownloadPath;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        
        addPreferencesFromResource(R.xml.preferences);
        
        mlsNaviType = (ListPreference) findPreference(KEY_PREF_USER_TYPE);
        
        int index = Integer.parseInt((String) mlsNaviType.getValue()) - 1;           
        if(index >= 0){
        	mlsNaviType.setSummary((String) mlsNaviType.getEntries()[index]);
        }
        
        metMaxWidth = (EditTextPreference) findPreference(KEY_PREF_MAX_WIDTH);
        metMaxWidth.setSummary((String) metMaxWidth.getText() + " " + getString(R.string.sCM));
	    
	    metWheelWidth = (EditTextPreference) findPreference(KEY_PREF_WHEEL_WIDTH);
	    metWheelWidth.setSummary((String) metWheelWidth.getText() + " " + getString(R.string.sCM));
	    
	    //TODO: add data packages list and button update data
	    metDownloadPath = (EditTextPreference) findPreference(KEY_PREF_DOWNLOAD_PATH);
	    metDownloadPath.setSummary((String) metDownloadPath.getText());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
    
	@Override
	protected void onPause() {	
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
		sharedPref.unregisterOnSharedPreferenceChangeListener(this);
		
		super.onPause();
	}
	
    @Override
	public void onResume() {
        super.onResume();
        
 		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
 		sharedPref.registerOnSharedPreferenceChangeListener(this);
    }	
    
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		CharSequence newVal = "";
		Preference Pref = findPreference(key);
		if(key.equals(KEY_PREF_WHEEL_WIDTH) || key.equals(KEY_PREF_MAX_WIDTH))
		{
			newVal = sharedPreferences.getString(key, "40");
        	String toIntStr = (String) newVal;
    		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    		editor.putInt(key + "_int", Integer.parseInt(toIntStr) * 10);
    		editor.commit();
    		
    		if(newVal.length() > 0)
            	Pref.setSummary(newVal  + " " + getString(R.string.sCM));
        }
		else if(key.equals(KEY_PREF_USER_TYPE))
		{
			newVal = sharedPreferences.getString(key, "1");
        	String toIntStr = (String) newVal;
    		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    		int index = Integer.parseInt(toIntStr);
    		editor.putInt(key + "_int", index);
    		editor.commit();
    		
    		index--;           
            if(index >= 0){
            	mlsNaviType.setSummary((String) mlsNaviType.getEntries()[index]);
            }
        }	
		else if(key.equals(KEY_PREF_DOWNLOAD_PATH)){
			newVal = sharedPreferences.getString(key, MainActivity.sUrl);
    		if(newVal.length() > 0)
            	Pref.setSummary(newVal);			
		}
	}	
}