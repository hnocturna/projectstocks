package com.udacity.hnoct.stockhawk.graph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.hnoct.stockhawk.data.PrefUtils;

/**
 * Created by hnoct on 1/24/2017.
 */

public class StockAxisValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return PrefUtils.formatDollars(value);
    }
}
