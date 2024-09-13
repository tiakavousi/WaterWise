package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PieChart pieChart;
    TextView txtProgress;
    TextView txtGoal;
    Button btnAddWater, btnSetGoal, signOutButton;
    TextView userName;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = findViewById(R.id.pieChart);
        txtProgress = findViewById(R.id.txtProgress);
        txtGoal = findViewById(R.id.txtGoal);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnSetGoal = findViewById(R.id.btnSetGoal);

        // sign in / sign up
        logout = findViewById(R.id.logout);
        userName = findViewById(R.id.userName);

        // Set click listener for logout
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Sign the user out
            startActivity(new Intent(MainActivity.this, EntryActivity.class));  // Go back to EntryActivity
            finish();  // Close MainActivity
        });

        int goal = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000); // Default goal: 2000 ml
        int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);

        updatePieChart(goal, progress);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        btnAddWater.setOnClickListener(v -> {
            // Show the bottom sheet dialog
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
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set colors for the pie chart

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Customize the pie chart appearance
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false); // Disable description text
        pieChart.setHoleRadius(45f); // Make a hollow center
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText("Water Tracker");
        pieChart.setCenterTextSize(18f);

        // Refresh the pie chart
        pieChart.invalidate();

        txtProgress.setText("Progress: " + progress + " ml");
        txtGoal.setText("Goal: " + goal + " ml");
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
