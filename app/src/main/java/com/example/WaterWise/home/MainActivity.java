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
import com.example.WaterWise.data.FirestoreHelper;
import com.example.WaterWise.history.HistoryActivity;
import com.example.WaterWise.settings.SettingsActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private List<IntakeRecord> records = new ArrayList<>(); // List of intake records for the day
    private IntakeRecordAdapter adapter; // Adapter for RecyclerView to display intake records
    private PieChart pieChart; // Pie chart for visualizing daily water intake vs goal
    private BottomNavigationView bottomNavigationView; // Bottom navigation bar for navigating between different sections of the app
    private DataModel dataModel; // ViewModel to manage and observe data
    private FirestoreHelper firestoreHelper; // Helper class to handle interactions with Firestore
    private ChartManager<PieChart> chartManager; // Manager for configuring and handling PieChart
    private  TextView recordsMessage; // TextView to display messages when no records are available

    // Fetches user data from Firestore and updates the PieChart
    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreHelper.fetchUserData(userId, dataModel, (goal, intake) -> updatePieChart(goal, intake));
    }

    // Updates the PieChart with the user's goal and intake
    private void updatePieChart(int goal, int intake) {
        chartManager.configurePieChart(pieChart, goal, intake);
    }

    // Shows the dialog to add water intake
    private void showAddWaterDialog() {
        AddWaterBottomSheetDialog dialog = new AddWaterBottomSheetDialog();
        dialog.setOnWaterAmountSelectedListener(this::addWater);
        dialog.show(getSupportFragmentManager(), "AddWaterBottomSheetDialog");
    }

    // Adds water intake and updates the UI and Firestore
    private void addWater(int amount) {
        int intake = dataModel.getIntake().getValue() != null ? dataModel.getIntake().getValue() : 0;
        intake += amount;
        dataModel.setIntake(intake);

        // Update the PieChart with the new intake and goal
        updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());

        // Save updated intake to Firestore
        firestoreHelper.saveUserData(
                dataModel.getName().getValue(),
                dataModel.getGoal().getValue(),
                dataModel.getWeight().getValue(),
                dataModel.getGender().getValue()
        );

        // Create a new record for the intake and save it in Firestore
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        IntakeRecord newRecord = new IntakeRecord(currentTime,currentDate, amount);

        // Add the new record to the list
        records.add(newRecord);
        // Add the record to the data model
        dataModel.addRecord(newRecord);
        // Save the record to Firestore
        firestoreHelper.saveWaterIntakeRecord(currentTime,currentDate, amount);
    }

    // Set up the RecyclerView for displaying daily intake records
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntakeRecordAdapter(records);
        recyclerView.setAdapter(adapter);
    }

    // Fetches today's intake records and updates the RecyclerView
    private void fetchTodaysRecords() {
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
    }

    @Override
    // Initializes the activity and sets up the views and logic
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        firestoreHelper = new FirestoreHelper();
        setContentView(R.layout.activity_main);
        pieChart = findViewById(R.id.pieChart);
        recordsMessage = findViewById(R.id.recordsMessage);

        // Initialize the ChartManager for the PieChart
        chartManager = new ChartManager<>(pieChart);

        // Set up the RecyclerView
        setupRecyclerView();

        // Fetch today's intake records
        fetchTodaysRecords();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
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

        // Fetch the user data and observe changes in the goal and intake LiveData
        fetchUserData();
        dataModel.getGoal().observe(this, goal -> {
            if (goal != null && dataModel.getIntake().getValue() != null) {
                updatePieChart(goal, dataModel.getIntake().getValue());
            }
        });
        dataModel.getIntake().observe(this, intake -> {
            if (dataModel.getGoal().getValue() != null) {
                updatePieChart(dataModel.getGoal().getValue(), intake);
            }
        });
    }

    // Handles logic when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        // Fetch user data again in case anything changed while the app was paused
        fetchUserData();
        if (dataModel.getGoal().getValue() != null && dataModel.getIntake().getValue() != null) {
            // Update the PieChart when the activity resumes
            updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        }
    }
}
