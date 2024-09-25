package com.example.WaterWise;

public class HistoryRecord {
    private String date;
    private int percentage;

    public HistoryRecord(String date, int percentage) {
        this.date = date;
        this.percentage = percentage;
    }

    public String getDate() {
        return date;
    }

    public int getPercentage() {
        return percentage;
    }
}

