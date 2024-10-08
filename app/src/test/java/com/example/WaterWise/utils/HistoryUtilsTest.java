package com.example.WaterWise.utils;

import static org.junit.Assert.assertEquals;

import com.example.WaterWise.history.HistoryRecord;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HistoryUtilsTest {

    @Test
    public void testSortHistoryRecords() {
        // Create a list of HistoryRecord objects with different dates
        List<HistoryRecord> historyList = new ArrayList<>();
        historyList.add(new HistoryRecord("2024-01-01", 1000));
        historyList.add(new HistoryRecord("2023-12-31", 500));
        historyList.add(new HistoryRecord("2024-10-09", 200));

        // Call the sorting method
        HistoryUtils.sortHistoryRecords(historyList);

        // Verify that the list is sorted in descending order (latest dates first)
        assertEquals("2024-10-09", historyList.get(0).getDate());
        assertEquals("2024-01-01", historyList.get(1).getDate());
        assertEquals("2023-12-31", historyList.get(2).getDate());
    }
}
