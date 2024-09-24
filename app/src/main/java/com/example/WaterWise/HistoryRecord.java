package com.example.WaterWise;

public class HistoryRecord {
    private String date;
    private int intake;
    private int percentage;

    public HistoryRecord(String date, int intake, int percentage) {
        this.date = date;
        this.intake = intake;
        this.percentage = percentage;
    }

    public String getDate() {
        return date;
    }

    public int getIntake() {
        return intake;
    }

    public int getPercentage() {
        return percentage;
    }
}

