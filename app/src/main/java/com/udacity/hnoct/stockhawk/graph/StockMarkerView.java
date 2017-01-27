package com.udacity.hnoct.stockhawk.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.udacity.hnoct.stockhawk.R;
import com.udacity.hnoct.stockhawk.data.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by hnoct on 1/25/2017.
 */

public class StockMarkerView extends MarkerView {
    // Member Variables
    private MPPointF mOffset;
    @BindView(R.id.marker_text) TextView markerText;

    public StockMarkerView(Context context, int layoutResource, LineChart lineChart) {
        super(context, layoutResource);
        ButterKnife.bind(this);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Format the String to pass to the TextView
        String formattedDate = PrefUtils.getFormattedMonthDay((long) e.getX());
        String formattedDollar = PrefUtils.formatDollars(e.getY());

        // Set the text
        markerText.setText(formattedDate + " " + formattedDollar);

        super.refreshContent(e, highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // Marker will be set to static to prevent clipping
        // Currently set to center in portrait mode and about 1/5th the way down the Canvas
        super.draw(canvas, (float) (canvas.getWidth()/1.75), canvas.getHeight()/5);
    }

    @Override
    public MPPointF getOffset() {
        // Not required, but keeping it for now because draw function was set with the offset on
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
