package com.example.WaterWise.home;

/**
 * This class represents a water intake record, containing information about the time, date, and amount of water consumed.
 */
public class IntakeRecord {
    // Fields to store the time of intake, date of intake, and the amount of water consumed
    private String time;
    private String date;
    private int amount;

    /**
     * Constructor to initialize an intake record with time, date, and amount.
     *
     * @param time   The time of intake in a specific format (e.g., "hh:mm a").
     * @param date   The date of intake in a specific format (e.g., "yyyy-MM-dd").
     * @param amount The amount of water consumed, in milliliters.
     */
    public IntakeRecord(String time, String date, int amount) {
        this.time = time;
        this.date = date;
        this.amount = amount;
    }

    /**
     * Getter for the date of the intake record.
     *
     * @return The date of the intake.
     */
    public String getDate() {
        return date;
    }
    /**
     * Getter for the time of the intake record.
     *
     * @return The time of the intake.
     */
    public String getTime() {
        return time;
    }
    /**
     * Getter for the amount of water consumed.
     *
     * @return The amount of water consumed, in milliliters.
     */
    public int getAmount() {
        return amount;
    }
}

