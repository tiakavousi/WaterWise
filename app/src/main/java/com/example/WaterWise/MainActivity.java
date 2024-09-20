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
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    private List<Record> records = new ArrayList<>();
    private RecordAdapter adapter;
    PieChart pieChart;
    BottomNavigationView bottomNavigationView;
    DataModel dataModel;
    FirestoreHelper firestoreHelper = new FirestoreHelper();

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreHelper.fetchUserData(userId, dataModel, (goal, intake) -> updatePieChart(goal, intake));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get ViewModel
        dataModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(DataModel.class);


        pieChart = findViewById(R.id.pieChart);

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
        updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());


//        List<Record> records = new ArrayList<>();
//// Add sample records
//        records.add(new Record("12:27 PM", "0.5 L"));
//        records.add(new Record("14:45 PM", "0.5 L"));
//        records.add(new Record("16:15 PM", "0.2 L"));
//        records.add(new Record("18:59 PM", "0.7 L"));
//        records.add(new Record("22:27 PM", "1 L"));
//
//        RecordAdapter adapter = new RecordAdapter(records);
//        recyclerView.setAdapter(adapter);
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

        // Create a new record and add it to the list
        Record newRecord = new Record(currentTime, amount + " ml");
        records.add(newRecord);
        dataModel.addRecord(newRecord);
        // Notify the adapter that a new item has been added
        adapter.notifyItemInserted(records.size() - 1);
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData();

        // Ensure the pie chart is up to date when returning to MainActivity
        if (dataModel.getGoal().getValue() != null && dataModel.getIntake().getValue() != null) {
            updatePieChart(dataModel.getGoal().getValue(), dataModel.getIntake().getValue());
        }
        Log.d("Goal: " + dataModel.getGoal().getValue() , " intake: " + dataModel.getIntake().getValue());
    }
}
