package com.example.WaterWise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    PieChart pieChart;
    BottomNavigationView bottomNavigationView;
    DataModel dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get ViewModel
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        // Observe goal and intake changes in ViewModel
        dataModel.getGoal().observe(this, this::updateGoal);
        dataModel.getIntake().observe(this, intake -> {
            if (dataModel.getGoal().getValue() != null) {
                updatePieChart(dataModel.getGoal().getValue(), intake);
            }
        });

        // Set initial goal and intake if necessary
        if (dataModel.getGoal().getValue() == null) {
            dataModel.setGoal(2000);  // Default goal
        }
        if (dataModel.getIntake().getValue() == null) {
            dataModel.setIntake(0);   // Default intake
        }

        pieChart = findViewById(R.id.pieChart);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setVisible(false);
        menu.findItem(R.id.nav_add_water).setVisible(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_add_water) {
                showAddWaterDialog();
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());
    }

    private void updateGoal(int goal) {
        if (dataModel.getIntake().getValue() != null) {
            updatePieChart(goal, dataModel.getIntake().getValue());
        }
    }


    @SuppressLint("DefaultLocale")
    private void updatePieChart(int goal, int intake) {
        int remaining = goal - intake;
        int intakePercentage = (intake * 100) / goal;
        int intakeLiters = intake / 1000;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(intake));
        if (remaining > 0) {
            entries.add(new PieEntry(remaining));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Water Consumption");
        dataSet.setColors(Color.BLUE, Color.GRAY);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setCenterText(String.format("%d%%\n%dL", intakePercentage, intakeLiters));
        pieChart.setCenterTextSize(40f);
        pieChart.setHoleColor(android.R.color.holo_blue_bright);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(58f);
        pieChart.getLegend().setEnabled(false);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();
    }

    private void showAddWaterDialog() {
        AddWaterBottomSheetDialog dialog = new AddWaterBottomSheetDialog();
        dialog.setOnWaterAmountSelectedListener(this::addWater);
        dialog.show(getSupportFragmentManager(), "AddWaterBottomSheetDialog");
    }

    private void addWater(int amount) {
        int intake = dataModel.getIntake().getValue() != null ? dataModel.getIntake().getValue() : 0;
        intake += amount;
        dataModel.setIntake(intake);
        updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the pie chart is up to date when returning to MainActivity
        if (dataModel.getGoal().getValue() != null && dataModel.getIntake().getValue() != null) {
            updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        }
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());
    }
}
