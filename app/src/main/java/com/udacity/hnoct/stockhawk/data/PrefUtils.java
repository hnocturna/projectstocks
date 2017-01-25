package com.udacity.hnoct.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.udacity.hnoct.stockhawk.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

    /**
     * Helper Method for converting a String of date-price pairs to a List of date-price Entries
     * @param stockHistory stock history in line-separated date-price pairs
     * @return List of Entry with date-price pairs
     */
    public static List<Entry> createGraphEntries(String stockHistory) {
        // Create List to populate with Entries
        List<Entry> historyEntries = new LinkedList<Entry>();

        // Split the lines at each new line
        String[] stockHistoryLines = stockHistory.split("\n");

        // Read lines backwards because most recent line is first and we need data sorted from oldest
        // to newest
        for (int i = stockHistoryLines.length - 1; i >= 0; i--) {
            long date = Long.parseLong(stockHistoryLines[i].split(",")[0]);
            float price = Float.parseFloat(stockHistoryLines[i].split(",")[1]);

            historyEntries.add(new Entry(date, price));
        }

        if (historyEntries.size() >= 0) {
            // If data is successfully parsed, return the List<Entry> Object
            return historyEntries;
        } else {
            // Else return null
            return null;
        }
    }

    /**
     * Converts date in milliseconds to user-readable date
     * @param dateInMillis date in milliseconds
     * @return Date in MMM dd, yyyy format
     *      e.g. Jan 03, 1999 or Dec 25, 2005
     */
    public static String getFormattedMonthDay(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(dateInMillis);
    }

    public static String getFormattedMonthDayShort(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, ''yy");
        return sdf.format(dateInMillis);
    }
}
