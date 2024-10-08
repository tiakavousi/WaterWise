package com.example.WaterWise.utils;


import com.example.WaterWise.home.IntakeRecord;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeUtils {

    /**
     * Creates a new IntakeRecord with the current date and time.
     *
     * @param amount The amount of water intake in milliliters.
     * @return A new IntakeRecord object with the current date and time.
     */
    public static IntakeRecord createIntakeRecord(int amount) {
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return new IntakeRecord(currentTime, currentDate, amount);
    }

    /**
     * Generates PieData for the PieChart based on the user's goal and intake.
     * This method no longer handles the color setup.
     *
     * @param goal   The user's daily water goal in milliliters.
     * @param intake The current water intake in milliliters.
     * @return PieData containing entries for intake and remaining goal.
     */
    public static PieData generatePieData(int goal, int intake) {
        float remainingAmount = goal - intake;

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(intake));
        if (remainingAmount > 0) {
            pieEntries.add(new PieEntry(remainingAmount));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Water Consumption");
        pieDataSet.setDrawValues(false);  // Hide values on the chart

        return new PieData(pieDataSet);  // Return PieData without color settings
    }

    /**
     * Calculates the water intake percentage based on the goal and intake.
     *
     * @param goal   The user's daily water goal in milliliters.
     * @param intake The current water intake in milliliters.
     * @return The intake percentage.
     */
    public static float calculateIntakePercentage(int goal, int intake) {
        return intake == 0 ? 0 : (intake * 100f) / goal;
    }

    /**
     * Formats the center text for the PieChart.
     *
     * @param intakePercentage The percentage of the water intake.
     * @param intakeInLiters   The current water intake in liters.
     * @return The formatted center text to be displayed on the PieChart.
     */
    public static String formatCenterText(float intakePercentage, float intakeInLiters) {
        return String.format("%s%%\n%sL",
                (intakePercentage % 1 == 0 ? String.format("%.0f", intakePercentage) : String.format("%.1f", intakePercentage)),
                (intakeInLiters % 1 == 0 ? String.format("%.0f", intakeInLiters) : String.format("%.1f", intakeInLiters))
        );
    }
}

