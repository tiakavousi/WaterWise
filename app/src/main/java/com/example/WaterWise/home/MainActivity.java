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

/**
 * MainActivity is the central screen of the WaterWise app.
 * It displays the user's water intake progress using a pie chart,
 * shows a list of daily water intake records, and provides navigation options.
 */
public class MainActivity extends AppCompatActivity {
    private DataModel dataModel;
    private List<IntakeRecord> records = new ArrayList<>();
    private IntakeRecordAdapter adapter;
    private PieChart pieChart;
    private ChartManager<PieChart> chartManager;
    private  TextView recordsMessage;
    private int goal;
    private int intake;

    /**
     * Called when the activity is starting. Initializes views, sets up LiveData observers, and configures the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    // Initializes the activity and sets up the views and logic
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization
        dataModel = new ViewModelProvider(this).get(DataModel.class);

        // Set initial values for goal and intake from DataModel
        goal = dataModel.getGoal().getValue() != null ? dataModel.getGoal().getValue() : DataModel.DEFAULT_GOAL;
        intake = dataModel.getIntake().getValue() != null ? dataModel.getIntake().getValue() : DataModel.DEFAULT_INTAKE;
        chartManager = new ChartManager<>(pieChart);
        setContentView(R.layout.activity_main);
        pieChart = findViewById(R.id.pieChart);
        recordsMessage = findViewById(R.id.recordsMessage);

        setupRecyclerView();
        // Observe changes to LiveData
        observeDataModel();
        // Initial chart update
        updatePieChart();
        setupBottomNavigation();
    }

    /**
     * Displays a dialog to add water intake.
     */
    private void showAddWaterDialog() {
        AddWaterBottomSheetDialog dialog = new AddWaterBottomSheetDialog();
        dialog.setOnWaterAmountSelectedListener(this::addWater);
        dialog.show(getSupportFragmentManager(), "AddWaterBottomSheetDialog");
    }

    /**
     * Adds a specified amount of water to the current intake and updates the UI.
     *
     * @param amount The amount of water to add, in milliliters.
     */
    private void addWater(int amount) {
        intake += amount;
        dataModel.setIntake(intake);

        // Update the PieChart with the new intake and goal
        updatePieChart();

        // Create a new record for the intake
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        IntakeRecord newRecord = new IntakeRecord(currentTime,currentDate, amount);

        // Add the new record to the list
        records.add(newRecord);

        // Add the record to the data model
        dataModel.addRecord(newRecord);
    }

    /**
     * Sets up the RecyclerView for displaying daily water intake records.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntakeRecordAdapter(records);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Observes changes in the DataModel and updates the UI accordingly.
     */
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

        dataModel.getGoal().observe(this, goalFromDataModel -> {
            goal = goalFromDataModel != null ? goalFromDataModel : DataModel.DEFAULT_GOAL;
            updatePieChart();
        });

        dataModel.getIntake().observe(this, intakeFromDataModel -> {
            intake = intakeFromDataModel != null ? intakeFromDataModel : DataModel.DEFAULT_INTAKE;
            updatePieChart();
        });
    }

    /**
     * Updates the PieChart with the current goal and intake values.
     */
    private void updatePieChart() {
        chartManager.configurePieChart(pieChart, goal, intake);
    }

    /**
     * Sets up the bottom navigation menu for navigating between different sections of the app.
     */
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
