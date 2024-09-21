package com.example.WaterWise.charts;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ChartManager<T extends Chart<?>> {
    private T chart;

    public ChartManager(T chart) {
        this.chart = chart;
    }

    // Pie chart configuration
    public void configurePieChart(PieChart pieChart, int goal, int intake) {
        int remainingAmount = goal - intake;
        int intakePercentage = (intake * 100) / goal;
        int intakeInLiters = intake / 1000;

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(intake));
        if (remainingAmount > 0) {
            pieEntries.add(new PieEntry(remainingAmount));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Water Consumption");
        pieDataSet.setColors(Color.BLUE, Color.GRAY);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText(String.format("%d%%\n%dL", intakePercentage, intakeInLiters));
        pieChart.setCenterTextSize(40f);
        pieChart.setHoleColor(android.R.color.holo_blue_bright);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(58f);
        pieChart.getLegend().setEnabled(false);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();
    }
}

