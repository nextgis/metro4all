/******************************************************************************
 * Project:  Metro4All
 * Purpose:  Routing in subway.
 * Authors:  Stanislav Petriakov
 ******************************************************************************
 *   Copyright (C) 2015 NextGIS
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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Button mPortalNameButton;
    private IViewHolderClick mViewHolderClick;

    public void setChecked() {
        mPortalNameButton.setBackgroundColor(mPortalNameButton.getResources().getColor(R.color.portalChecked));
    }

    public void setNormal() {
        mPortalNameButton.setBackgroundDrawable(mPortalNameButton.getResources().getDrawable(R.drawable.horizontal_item_selector));
    }

    public void setInvalid() {
        mPortalNameButton.setBackgroundColor(mPortalNameButton.getResources().getColor(R.color.portalInvalid));
    }

    public static interface IViewHolderClick {
        public void onItemClick(View caller, int position);
    }

    public ViewHolder(View itemView) {
        super(itemView);
        mPortalNameButton = (Button) itemView.findViewById(R.id.btnPortal);
    }

    public void setName(String name) {
        mPortalNameButton.setText(name);
    }

    public void setOnClickListener(IViewHolderClick listener) {
        mPortalNameButton.setOnClickListener(this);
        mViewHolderClick = listener;
    }

    @Override
    public void onClick(View view) {
        mViewHolderClick.onItemClick(view, getPosition());
    }
}
