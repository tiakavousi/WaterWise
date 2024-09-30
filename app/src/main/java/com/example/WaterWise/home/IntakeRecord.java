package com.example.WaterWise.home;

public class IntakeRecord {
    private String time;
    private String date;
    private int amount;

    public IntakeRecord(String time, String date, int amount) {
        this.time = time;
        this.date = date;
        this.amount = amount;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }
}

