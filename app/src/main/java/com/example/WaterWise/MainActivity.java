package com.example.WaterWise;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

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
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = findViewById(R.id.pieChart);
        int goal = getGoal();
        int intake = getIntake();

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
                intent.putExtra("goal", goal);
                intent.putExtra("intake", intake);
                startActivity(intent);
                return true;
            }
            return false;
        });

        updatePieChart(goal, intake);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    private int getGoal() {
        return getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000);
    }

    private int getIntake() {
        return getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("intake", 0);
    }

    private void updatePieChart(int goal, int intake) {
        int remaining = goal - intake;
        float intakePercentage = (intake * 100f) / goal;
        float intakeLiters = intake / 1000f;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(intake));
        if (remaining > 0) {
            entries.add(new PieEntry(remaining));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Water Consumption");
        dataSet.setColors(Color.BLUE, Color.GRAY);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setCenterText(String.format("%.0f%%\n%.2fL", intakePercentage, intakeLiters));
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
        int intake = getIntake();
        intake += amount;
        getSharedPreferences("WaterTracker", MODE_PRIVATE).edit().putInt("intake", intake).apply();
        updatePieChart(getGoal(), intake);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePieChart(getGoal(), getIntake());
    }
}
