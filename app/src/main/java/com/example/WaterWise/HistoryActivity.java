package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private DataModel dataModel;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private TextView dayOfWeekTextView, dateTextView, goalTextView, remainingTextView;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dataModel = new ViewModelProvider(this).get(DataModel.class);


        dayOfWeekTextView = findViewById(R.id.dayOfWeekTextView);
        dateTextView = findViewById(R.id.dateTextView);
        goalTextView = findViewById(R.id.goal_text);
        remainingTextView = findViewById(R.id.remaining_text);

        displayDayOfWeek();
        observeGoalAndIntake();

        FirestoreHelper firestoreHelper = new FirestoreHelper();
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        firestoreHelper.fetchHistory(historyList -> {
            Log.d("MainActivity!!!!", "Setting adapter with history size: " + historyList.size());
            HistoryAdapter adapter = new HistoryAdapter(historyList);
            recyclerView2.setAdapter(adapter);
        });

        // Bottom NavBar
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
}
