package com.udacity.hnoct.stockhawk.data;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by hnoct on 1/24/2017.
 */

public class DateValueFormatter implements IValueFormatter {
    /*Member Variables*/

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value == entry.getX()) {
            return PrefUtils.getFormattedMonthDay((long) value);
        } else {
            return "$" + Float.toString(value);
        }
    }
}
