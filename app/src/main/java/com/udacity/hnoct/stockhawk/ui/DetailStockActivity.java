package com.udacity.hnoct.stockhawk.ui;

import android.os.Bundle;
import android.app.Activity;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.hnoct.stockhawk.R;

import butterknife.BindView;

public class DetailStockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
//        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

}
