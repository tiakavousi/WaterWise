package com.example.WaterWise;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    PieChart pieChart;
    Button btnAddWater, btnSetGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        pieChart = findViewById(R.id.pieChart);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnSetGoal = findViewById(R.id.btnSetGoal);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_add_water) {
                startActivity(new Intent(MainActivity.this, AddWaterActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                int goal = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000);
                int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);
                intent.putExtra("goal", goal);
                intent.putExtra("progress", progress);
                startActivity(intent);
                return true;
            }
            return false;
        });

        int goal = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000); // Default goal: 2000 ml
        int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);

        updatePieChart(goal, progress);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        btnAddWater.setOnClickListener(v -> {
            AddWaterBottomSheetDialog dialog = new AddWaterBottomSheetDialog();
            dialog.setOnWaterAmountSelectedListener(this::addWater);
            dialog.show(getSupportFragmentManager(), "AddWaterBottomSheetDialog");
        });

        btnSetGoal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SetGoalActivity.class);
            startActivity(intent);
        });

    }

    private void updatePieChart(int goal, int progress) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Add the consumed amount entry
        entries.add(new PieEntry(progress, "Consumed"));

        // Add the remaining amount entry
        int remaining = goal - progress;
        if (remaining > 0) {
            entries.add(new PieEntry(remaining, "Remaining"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Water Consumption");
        int[] customColors = {Color.BLUE, Color.GRAY};
        dataSet.setColors(customColors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Customize the pie chart appearance
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(53f);
        pieChart.setCenterText("Water Tracker");
        pieChart.setCenterTextSize(18f);

        // Refresh the pie chart
        pieChart.invalidate();
    }

    private void addWater(int amount) {
        int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);
        progress += amount;

        getSharedPreferences("WaterTracker", MODE_PRIVATE).edit().putInt("progress", progress).apply();
        updatePieChart(getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000), progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int goal = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000);
        int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);
        updatePieChart(goal, progress);
    }
}