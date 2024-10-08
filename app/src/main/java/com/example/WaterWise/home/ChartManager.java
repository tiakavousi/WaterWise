package com.example.WaterWise.home;

import android.graphics.Color;

import com.example.WaterWise.utils.HomeUtils;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * ChartManager is a utility class to handle the configuration of various charts.
 * It is designed to work with charts of type T, which extends the Chart class.
 * Currently, it focuses on configuring PieCharts.
 *
 * @param <T> The type of chart, extending Chart.
 */
public class ChartManager<T extends Chart<?>> {
    private final T chart;
    /**
     * Constructor to initialize the ChartManager with a chart object.
     *
     * @param chart The chart object of type T.
     */
    public ChartManager(T chart) {
        this.chart = chart;
    }

    /**
     * Configures the PieChart to display the water consumption information.
     *
     * @param pieChart The PieChart to be configured.
     * @param goal     The user's daily water goal in milliliters.
     * @param intake   The current water intake in milliliters.
     */
    public void configurePieChart(PieChart pieChart, int goal, Integer intake) {
        // Generate PieData without colors
        PieData pieData = HomeUtils.generatePieData(goal, intake);

        // Set colors for the PieDataSet here, since this depends on Android classes
        PieDataSet dataSet = (PieDataSet) pieData.getDataSet();
        dataSet.setColors(Color.BLUE, Color.GRAY);  // Set colors for the chart

        // Apply appearance settings and display the chart
        setupPieChartAppearance(pieChart, pieData, goal, intake);
        pieChart.invalidate();  // Refresh the chart after data changes
    }

    /**
     * Generates PieData for the PieChart based on the user's goal and intake.
     *
     * @param goal   The user's daily water goal in milliliters.
     * @param intake The current water intake in milliliters.
     * @return PieData containing entries for intake and remaining goal.
     */
    private PieData generatePieData(int goal, int intake) {
        float remainingAmount = goal - intake;

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(intake));
        if (remainingAmount > 0) {
            pieEntries.add(new PieEntry(remainingAmount));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Water Consumption");
        pieDataSet.setColors(Color.BLUE, Color.GRAY);  // Set colors for the chart
        pieDataSet.setDrawValues(false);  // Hide values on the chart

        return new PieData(pieDataSet);
    }

    /**
     * Configures the visual appearance of the PieChart, including text and colors.
     *
     * @param pieChart    The PieChart to be configured.
     * @param pieData     The data to be set in the PieChart.
     * @param goal        The user's daily water goal in milliliters.
     * @param intake      The current water intake in milliliters.
     */
    private void setupPieChartAppearance(PieChart pieChart, PieData pieData, int goal, int intake) {

        float intakePercentage = HomeUtils.calculateIntakePercentage(goal, intake);
//        float intakePercentage = intake == 0? 0 : (intake * 100f) / goal;
        float intakeInLiters = intake / 1000f;
        String centerText = HomeUtils.formatCenterText(intakePercentage, intakeInLiters);

//
//        String centerText = String.format("%s%%\n%sL",
//                (intakePercentage % 1 == 0 ? String.format("%.0f", intakePercentage) : String.format("%.1f", intakePercentage)),
//                (intakeInLiters % 1 == 0 ? String.format("%.0f", intakeInLiters) : String.format("%.1f", intakeInLiters))
//        );

        pieChart.setData(pieData);
        pieChart.setCenterText(centerText);  // Set center text
        pieChart.setCenterTextSize(40f);
        pieChart.setHoleColor(android.R.color.holo_blue_bright);
        pieChart.getDescription().setEnabled(false);  // Remove the description
        pieChart.setHoleRadius(58f);  // Set the size of the hole in the center of the pie chart
        pieChart.getLegend().setEnabled(false);  // Hide the legend
    }

}

