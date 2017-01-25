package com.udacity.hnoct.stockhawk.data;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by hnoct on 1/24/2017.
 */

public class DateAxisValueFormatter implements IAxisValueFormatter {


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (axis.getLabelCount() <= 2) {
            return PrefUtils.getFormattedMonthDay((long) value);
        } else {
            return PrefUtils.getFormattedMonthDayShort((long) value);
        }

    }
}
