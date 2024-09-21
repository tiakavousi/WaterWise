package com.example.WaterWise;

public class Record {
    private String time;
    private String date;
    private String amount;

    public Record(String time, String date, String amount) {
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

    public String getAmount() {
        return amount;
    }
}

