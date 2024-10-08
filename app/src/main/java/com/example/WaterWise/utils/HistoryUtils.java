package com.example.WaterWise.utils;

import com.example.WaterWise.history.HistoryRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryUtils {

    // Make sure to use the same date format as in your HistoryRecord
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void sortHistoryRecords(List<HistoryRecord> historyList) {
        Collections.sort(historyList, new Comparator<HistoryRecord>() {
            @Override
            public int compare(HistoryRecord hr1, HistoryRecord hr2) {
                try {
                    return dateFormat.parse(hr2.getDate()).compareTo(dateFormat.parse(hr1.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }
}

