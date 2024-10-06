package com.example.WaterWise.home;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartManager<T extends Chart<?>> {
    private final T chart;

    public ChartManager(T chart) {
        this.chart = chart;
    }

    // Pie chart configuration
    public void configurePieChart(PieChart pieChart, int goal, Integer intake) {
        PieData pieData = generatePieData(goal, intake);
        setupPieChartAppearance(pieChart, pieData, goal, intake);
        pieChart.invalidate();  // Refresh the chart after data changes
    }

    // Method to generate PieData based on goal and intake
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

    // Method to configure the visual appearance of the PieChart
    private void setupPieChartAppearance(PieChart pieChart, PieData pieData, int goal, int intake) {
        float intakePercentage = intake == 0? 0 : (intake * 100f) / goal;
        float intakeInLiters = intake / 1000f;
        String centerText = String.format("%s%%\n%sL",
                (intakePercentage % 1 == 0 ? String.format("%.0f", intakePercentage) : String.format("%.1f", intakePercentage)),
                (intakeInLiters % 1 == 0 ? String.format("%.0f", intakeInLiters) : String.format("%.1f", intakeInLiters))
        );

        pieChart.setData(pieData);
        pieChart.setCenterText(centerText);  // Set center text
        pieChart.setCenterTextSize(40f);
        pieChart.setHoleColor(android.R.color.holo_blue_bright);
        pieChart.getDescription().setEnabled(false);  // Remove the description
        pieChart.setHoleRadius(58f);  // Set the size of the hole in the center of the pie chart
        pieChart.getLegend().setEnabled(false);  // Hide the legend
    }

}

