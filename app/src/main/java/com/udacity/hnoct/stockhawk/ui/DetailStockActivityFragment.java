package com.udacity.hnoct.stockhawk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.hnoct.stockhawk.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailStockActivityFragment extends Fragment {

    public DetailStockActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_stock, container, false);
    }
}
