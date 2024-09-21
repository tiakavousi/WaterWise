package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.WaterWise.charts.ChartManager;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private List<Record> records = new ArrayList<>();
    private RecordAdapter adapter;
    private PieChart pieChart;
    private BottomNavigationView bottomNavigationView;
    private DataModel dataModel;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private ChartManager<PieChart> chartManager;

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreHelper.fetchUserData(userId, dataModel, (goal, intake) -> updatePieChart(goal, intake));
    }

    private void updatePieChart(int goal, int intake) {
        chartManager.configurePieChart(pieChart, goal, intake);
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
        // Save updated intake to Firestore
        firestoreHelper.saveUserData(
                dataModel.getName().getValue(),
                dataModel.getGoal().getValue(),
                intake,  // Save the updated intake
                dataModel.getWeight().getValue(),
                dataModel.getGender().getValue()
        );

        // Get current time for the record
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // Create a new record and add it to the list
        Record newRecord = new Record(currentTime,currentDate, amount + " ml");
        records.add(newRecord);
        dataModel.addRecord(newRecord);
        // Notify the adapter that a new item has been added
        adapter.notifyItemInserted(records.size() - 1);
        // Save the new record in Firestore's 'records' sub-collection
        firestoreHelper.saveWaterIntakeRecord(currentTime,currentDate, amount + " ml");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        setContentView(R.layout.activity_main);
        pieChart = findViewById(R.id.pieChart);
        chartManager = new ChartManager<>(pieChart);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(records);
        recyclerView.setAdapter(adapter);


        dataModel.getRecords().observe(this, records -> {
            adapter.setRecords(records);
            adapter.notifyDataSetChanged();
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData();

        // Ensure the pie chart is up to date when returning to MainActivity
        if (dataModel.getGoal().getValue() != null && dataModel.getIntake().getValue() != null) {
            updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        }
    }
}
