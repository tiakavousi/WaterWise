package com.example.WaterWise.history;

/**
 * The HistoryRecord class represents a record of water intake for a specific date.
 * It contains the date of the record and the amount of water intake.
 */
public class HistoryRecord {
    // The date of the water intake record in String format
    private String date;

    // The amount of water intake for the day in milliliters
    private int intake;

    /**
     * Constructor to create a new HistoryRecord.
     *
     * @param date   The date of the record in a String format (e.g., "2024-10-07").
     * @param intake The amount of water intake in milliliters.
     */
    public HistoryRecord(String date, int intake) {
        this.date = date;
        this.intake = intake;
    }

    /**
     * Gets the date of this HistoryRecord.
     *
     * @return The date of the record in a String format.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the amount of water intake for this HistoryRecord.
     *
     * @return The water intake in milliliters.
     */
    public int getIntake() {
        return intake;
    }

    /**
     * Calculates the percentage of water intake relative to a given goal.
     *
     * @param goal The target water intake in milliliters.
     * @return The percentage of the intake relative to the goal. Returns 0 if the goal is non-positive.
     */
    public float calculatePercentage(int goal) {
        return goal > 0 ? (intake * 100f / goal) : 0f;
    }
}

