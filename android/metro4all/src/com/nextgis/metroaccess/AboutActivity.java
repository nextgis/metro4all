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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockActivity {
    private TextView txtVersion;
    private TextView txtDescription;
    private ImageView imgLogo;

    private String versionName = "unknown";
    private String versionCode = "unknown";

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.about);

        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);

        imgLogo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLogoClicked();
            }
        });

        String pkgName = this.getPackageName();
        try {
            PackageManager pm = this.getPackageManager();
            versionName = pm.getPackageInfo(pkgName, 0).versionName;
            versionCode = Integer.toString(pm.getPackageInfo(this.getPackageName(), 0).versionCode);
        } catch (NameNotFoundException e) {
        }

        txtVersion.setText("v. " + versionName + " (rev. " + versionCode + ")");
        
       	getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void onLogoClicked() {
        Intent browserIntent = new Intent("android.intent.action.VIEW",
                Uri.parse("http://nextgis.ru"));
        startActivity(browserIntent);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
