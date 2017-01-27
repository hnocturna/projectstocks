package com.udacity.hnoct.stockhawk.graph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.hnoct.stockhawk.data.PrefUtils;

/**
 * Created by hnoct on 1/24/2017.
 */

public class DateAxisValueFormatter implements IAxisValueFormatter {
    int scale;
    public DateAxisValueFormatter(int scale) {
        this.scale = scale;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (scale == 2) {
            return PrefUtils.getFormattedMonthDay((long) value);
        } else if (scale == 1) {
            return PrefUtils.getFormattedMonthYear((long) value);
        }
        return null;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }
}
