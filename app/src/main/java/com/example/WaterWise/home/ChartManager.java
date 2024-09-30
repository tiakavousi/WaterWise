package com.example.WaterWise.home;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ChartManager<T extends Chart<?>> {
    private final T chart;

    public ChartManager(T chart) {
        this.chart = chart;
    }

    // Pie chart configuration
    public void configurePieChart(PieChart pieChart, int goal, Integer intake) {
        if (intake == null) {
            intake = 0;
        }
        float remainingAmount = goal - intake;
        float intakePercentage = intake == 0? 0 : (intake * 100f) / goal;
        float intakeInLiters = intake / 1000f;

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(intake));
        if (remainingAmount > 0) {
            pieEntries.add(new PieEntry(remainingAmount));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Water Consumption");
        pieDataSet.setColors(Color.BLUE, Color.GRAY);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        String centerText = String.format("%s%%\n%sL",
                (intakePercentage % 1 == 0 ? String.format("%.0f", intakePercentage) : String.format("%.1f", intakePercentage)),
                (intakeInLiters % 1 == 0 ? String.format("%.0f", intakeInLiters) : String.format("%.1f", intakeInLiters))
        );
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(40f);
        pieChart.setHoleColor(android.R.color.holo_blue_bright);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(58f);
        pieChart.getLegend().setEnabled(false);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();
    }
}

