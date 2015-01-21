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

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

public class StationExpandableListView extends ExpandableListView {

    public StationExpandableListView(Context context) {
        super(context);
    }

    public StationExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    // http://stackoverflow.com/a/20997828/2088273
    public void smoothScrollToPositionFromTop(final int position) {
        View child = getChildAtPosition(position);
        // There's no need to scroll if child is already at top or view is already scrolled to its end
        if ((child != null) && ((child.getTop() == 0) || ((child.getTop() > 0) && !ViewCompat.canScrollVertically(this, 1)))) {
            return;
        }

        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    view.setOnScrollListener(null);

                    // Fix for scrolling bug
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setSelection(position);
                        }
                    });
                }
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) { }
        });

        // Perform scrolling to position
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                smoothScrollToPositionFromTop(position, 0);
            }
        });
    }

    public View getChildAtPosition(final int position) {
        final int index = position - getFirstVisiblePosition();
        if ((index >= 0) && (index < getChildCount())) {
            return getChildAt(index);
        } else {
            return null;
        }
    }
}