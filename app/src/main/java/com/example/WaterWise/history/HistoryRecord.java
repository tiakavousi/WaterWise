package com.example.WaterWise.history;

public class HistoryRecord {
    private String date;
    private int intake;

    public HistoryRecord(String date, int intake) {
        this.date = date;
        this.intake = intake;
    }

    public String getDate() {
        return date;
    }

    public int getIntake() {
        return intake;
    }
    public int calculatePercentage(int goal) {
        return goal > 0 ? (intake * 100 / goal) : 0;
    }
}

