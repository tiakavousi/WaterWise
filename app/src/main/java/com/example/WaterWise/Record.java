package com.example.WaterWise;

public class Record {
    private String time;
    private String amount;

    public Record(String time, String amount) {
        this.time = time;
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public String getAmount() {
        return amount;
    }
}

