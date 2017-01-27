package com.udacity.hnoct.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.hnoct.stockhawk.R;
import com.udacity.hnoct.stockhawk.data.Contract;
import com.udacity.hnoct.stockhawk.graph.DateAxisValueFormatter;
import com.udacity.hnoct.stockhawk.graph.DateValueFormatter;
import com.udacity.hnoct.stockhawk.data.PrefUtils;
import com.udacity.hnoct.stockhawk.graph.StockAxisValueFormatter;
import com.udacity.hnoct.stockhawk.graph.StockMarkerView;

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
    private final static float MONTH_IN_MILLIS = 86400f * 7 * 30 * 1000;
    private final static float YEAR_IN_MILLIS = 86400f * 365 * 1000;

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
    @BindView(R.id.symbol) TextView symbolText;
    @BindView(R.id.price) TextView priceText;
    @BindView(R.id.changeAbsolute) TextView changeAbsoluteText;
    @BindView(R.id.changePercent) TextView changePercentText;

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

        // Retrieve data for stock
        String symbol = data.getString(IDX_SYMBOL);
        float price = data.getFloat(IDX_PRICE);
        float percentChange = data.getFloat(IDX_PERCENT_CHANGE);
        float absoluteChange = data.getFloat(IDX_ABSOLUTE_CHANGE);

        if (absoluteChange < 0) {
            // If the change is negative, set the background of the text to red
            changeAbsoluteText.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red, null));
            changePercentText.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red, null));
        }

        // Fill the views with data
        symbolText.setText(symbol);
        priceText.setText(PrefUtils.formatDollars(price));
        changePercentText.setText(PrefUtils.formatPercentage(percentChange / 100));
        changeAbsoluteText.setText(PrefUtils.formatDollarsWithPlus(absoluteChange));

        // Convert the historical data to graphable data
        List<Entry> historicalEntries = PrefUtils.createGraphEntries(data.getString(IDX_HISTORY));
        if (historicalEntries == null || historicalEntries.size() == 0) {
            // No data to graph
            Timber.v("No historical data found!");
            return;
        }
        Timber.v(historicalEntries.toString());

        final LineDataSet dataSet = new LineDataSet(historicalEntries, "Historical Stock Data");

        // Plot the values as dates instead of millis
        dataSet.setValueFormatter(new DateValueFormatter());

        // Set formatter for X and Y-axis
        XAxis xAxis = stockChart.getXAxis();
        final DateAxisValueFormatter dateAxisFormatter = new DateAxisValueFormatter(1);
        xAxis.setValueFormatter(dateAxisFormatter);
        xAxis.setLabelCount(4);

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
        dataSet.setLineWidth(2);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData stockData = new LineData(dataSet);

        stockChart.setMarker(new StockMarkerView(getActivity(), R.layout.stock_marker_view, stockChart));
        stockChart.setAutoScaleMinMaxEnabled(true);     // Scales the graph so data is always in view
        stockChart.setKeepPositionOnRotation(true);     // Keeps same position on rotation
        stockChart.setData(stockData);
        stockChart.getViewPortHandler().setMaximumScaleX(31.738262f);
        stockChart.animateX(750);

        stockChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float visibleRange = stockChart.getVisibleXRange();
                if (dateAxisFormatter.getScale() != 1 && visibleRange > YEAR_IN_MILLIS) {
                    dateAxisFormatter.setScale(1);
                } else if (dateAxisFormatter.getScale() != 2 && visibleRange < YEAR_IN_MILLIS && visibleRange > MONTH_IN_MILLIS) {
                    dateAxisFormatter.setScale(2);
                }
                return false;
            }
        });
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
