package com.example.WaterWise.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.WaterWise.R;
import com.example.WaterWise.data.DataModel;
import com.example.WaterWise.history.HistoryActivity;
import com.example.WaterWise.settings.SettingsActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private DataModel dataModel;
    private List<IntakeRecord> records = new ArrayList<>();
    private IntakeRecordAdapter adapter;
    private PieChart pieChart;
    private ChartManager<PieChart> chartManager;
    private  TextView recordsMessage;

    @Override
    // Initializes the activity and sets up the views and logic
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        chartManager = new ChartManager<>(pieChart);
        setContentView(R.layout.activity_main);
        pieChart = findViewById(R.id.pieChart);
        recordsMessage = findViewById(R.id.recordsMessage);

        setupRecyclerView();
        observeDataModel();
        updatePieChart();
        setupBottomNavigation();
    }

    @Override
    // Handles logic when the activity is resumed
    protected void onResume() {
        super.onResume();
        // Fetch user data again in case anything changed while the app was paused and update the PieChart when the activity resumes
        if (dataModel.getGoal().getValue() != null && dataModel.getIntake().getValue() != null) {
            updatePieChart();
        }
    }

    // Shows the dialog to add water intake
    private void showAddWaterDialog() {
        AddWaterBottomSheetDialog dialog = new AddWaterBottomSheetDialog();
        dialog.setOnWaterAmountSelectedListener(this::addWater);
        dialog.show(getSupportFragmentManager(), "AddWaterBottomSheetDialog");
    }

    // Adds water intake and updates the UI
    private void addWater(int amount) {
        int intake = dataModel.getIntake().getValue();
        intake += amount;
        dataModel.setIntake(intake);

        // Update the PieChart with the new intake and goal
        chartManager.configurePieChart(pieChart, dataModel.getGoal().getValue(), intake);

        // Create a new record for the intake
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        IntakeRecord newRecord = new IntakeRecord(currentTime,currentDate, amount);

        // Add the new record to the list
        records.add(newRecord);

        // Add the record to the data model
        dataModel.addRecord(newRecord);
    }

    // Set up the RecyclerView for displaying daily intake records
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntakeRecordAdapter(records);
        recyclerView.setAdapter(adapter);
    }

    private void observeDataModel() {
        dataModel.getRecords().observe(this, records -> {

            if (records.isEmpty()) {
                // Show a message if there are no records
                recordsMessage.setText("Stay hydrated!");
            } else {
                // Hide the message if records are available
                recordsMessage.setVisibility(View.GONE);
                // Update the adapter with the fetched records
                adapter.setRecords(records);
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }
        });
        dataModel.getGoal().observe(this, goal -> updatePieChart());
        dataModel.getIntake().observe(this, intake -> updatePieChart());
    }
    private void updatePieChart() {
        Integer goal = dataModel.getGoal().getValue();
        Integer intake = dataModel.getIntake().getValue();
        if (goal != null && intake != null) {
            chartManager.configurePieChart(pieChart, goal, intake);
        }
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

        // Hide the home item as the user is already on the home screen
        menu.findItem(R.id.nav_home).setVisible(false);

        // Show the "add water" item in the navigation bar
        menu.findItem(R.id.nav_add_water).setVisible(true);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_history) {
                // Navigate to HistoryActivity when history is selected
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_add_water) {
                // Show add water dialog when "add water" is selected
                showAddWaterDialog();
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Navigate to SettingsActivity when settings is selected
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
