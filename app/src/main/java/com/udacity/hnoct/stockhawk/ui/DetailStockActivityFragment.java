package com.udacity.hnoct.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.udacity.hnoct.stockhawk.R;
import com.udacity.hnoct.stockhawk.data.Contract;
import com.udacity.hnoct.stockhawk.data.DateAxisValueFormatter;
import com.udacity.hnoct.stockhawk.data.DateValueFormatter;
import com.udacity.hnoct.stockhawk.data.PrefUtils;
import com.udacity.hnoct.stockhawk.data.StockAxisValueFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailStockActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /*Constants*/
    private final static String LOG_TAG = DetailStockActivityFragment.class.getSimpleName();
    private final static int DETAIL_LOADER = 1;

    // Column Projection
    private static final String[] STOCK_COLUMNS = new String[] {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };

    // Indices for the columns
    private static final int IDX_ID = 0;
    private static final int IDX_SYMBOL = 1;
    private static final int IDX_PRICE = 2;
    private static final int IDX_PERCENT_CHANGE = 3;
    private static final int IDX_ABSOLUTE_CHANGE = 4;
    private static final int IDX_HISTORY = 5;

    // Views to be populated
    @BindView(R.id.detail_stock_chart) LineChart stockChart;

    /*Member Variables*/
    Uri mStockUri;

    public DetailStockActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize the View
        View rootView =  inflater.inflate(R.layout.fragment_detail_stock, container, false);
        ButterKnife.bind(this, rootView);

        // Get the historical stock data from the URI for the stock
        mStockUri = getActivity().getIntent().getData();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.v("In onCreateLoader");
        if (mStockUri == null) {
            Timber.v("No URI found!");
            return null;
        }
        Timber.v("Uri: " + mStockUri);

        return new CursorLoader(
                getActivity(),
                mStockUri,
                STOCK_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            // No cursor returned
            Timber.v("No cursor returned");
            return;
        }
        if (!data.moveToFirst()) {
            // Cursor returns no data
            Timber.v("No data returned by cursor");
            return;
        }

        // Convert the historical data to graphable data
        List<Entry> historicalEntries = PrefUtils.createGraphEntries(data.getString(IDX_HISTORY));
        if (historicalEntries == null || historicalEntries.size() == 0) {
            // No data to graph
            Timber.v("No historical data found!");
            return;
        }
        Timber.v(historicalEntries.toString());

        LineDataSet dataSet = new LineDataSet(historicalEntries, "Historical Stock Data");

        // Plot the values as dates instead of millis
        dataSet.setValueFormatter(new DateValueFormatter());

        // Set formatter for X and Y-axis
        XAxis xAxis = stockChart.getXAxis();
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        xAxis.setLabelCount(4, true);

        YAxis yAxis = stockChart.getAxisLeft();
        yAxis.setValueFormatter(new StockAxisValueFormatter());

        stockChart.getAxisRight().setEnabled(false);    // Remove right axis

        // Set styling for data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.setColor((getResources().getColor(R.color.colorPrimary, null)));
            dataSet.setValueTextColor(getResources().getColor(android.R.color.white, null));
        } else {
            //noinspection deprecation
            dataSet.setColor((getResources().getColor(R.color.colorPrimary)));
            //noinspection deprecation
            dataSet.setValueTextColor(getResources().getColor(android.R.color.white));
        }

        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(getResources().getDrawable(R.drawable.graph_gradient_fill, null));
        LineData stockData = new LineData(dataSet);

        stockChart.setData(stockData);
        stockChart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
