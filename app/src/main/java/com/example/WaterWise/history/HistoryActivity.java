package com.example.WaterWise.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.WaterWise.R;
import com.example.WaterWise.data.DataModel;
import com.example.WaterWise.home.MainActivity;
import com.example.WaterWise.settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private DataModel dataModel;
    private int userGoal;
    private TextView dayOfWeekTextView, dateTextView, goalTextView, remainingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        dataModel.getGoal().observe(this, goal -> {userGoal = goal;});

        initializeUIElements();
        displayDayOfWeek();
        observeGoalAndIntake();
        setUpBottomNavigationBar();
        setUpHistoryRecyclerView();
        dataModel.loadHistoryRecords();
    }

    private void initializeUIElements(){
        dayOfWeekTextView = findViewById(R.id.dayOfWeekTextView);
        dateTextView = findViewById(R.id.dateTextView);
        goalTextView = findViewById(R.id.goal_text);
        remainingTextView = findViewById(R.id.remaining_text);
    }

    private void setUpHistoryRecyclerView() {
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        dataModel.getHistoryRecords().observe(this, historyList -> {
            sortHistoryRecords(historyList);
            // Fetch the goal from the data model
            int goalInMiliLiters = dataModel.getGoal().getValue();
            HistoryRecordAdapter adapter = new HistoryRecordAdapter(historyList, goalInMiliLiters);
            recyclerView2.setAdapter(adapter);
        });
    }
    private void sortHistoryRecords(List<HistoryRecord> historyList) {
        Collections.sort(historyList, (record1, record2)-> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date1 = dateFormat.parse(record1.getDate());
                Date date2 = dateFormat.parse(record2.getDate());
                return date2.compareTo(date1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    private void observeGoalAndIntake() {
        dataModel.getGoal().observe(this, goal -> {
            double goalInLiters = goal / 1000.0;
            String goalText = (goalInLiters % 1 == 0) ? String.format("%.0f L", goalInLiters) : String.format("%.1f L", goalInLiters);
            goalTextView.setText("Goal: " + goalText);
        });

        dataModel.getIntake().observe(this, intake -> {
            double intakeInLiters = intake / 1000.0;
            double goalInLiters = dataModel.getGoal().getValue() / 1000.0;
            double remainingInLiters = goalInLiters - intakeInLiters;
            String remainingText = (remainingInLiters % 1 == 0) ? String.format("%.0f L", (remainingInLiters > 0 ? remainingInLiters : 0)) : String.format("%.1f L", (remainingInLiters > 0 ? remainingInLiters : 0));
            remainingTextView.setText("Remaining: " + remainingText);
        });
    }

    public void displayDayOfWeek() {
        Date currentDate = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String currentDay = sdf.format(new Date());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        dayOfWeekTextView.setText(currentDay);
        dateTextView.setText(formattedDate);
    }

    public void setUpBottomNavigationBar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_add_water).setVisible(false);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_settings) {
                startActivity(new Intent(HistoryActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                return true;
            } else return itemId == R.id.nav_history;
        });
    }
}
