package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private FirestoreHelper firestoreHelper;
    private TextView dayOfWeekTextView, dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dayOfWeekTextView = findViewById(R.id.dayOfWeekTextView);
        dateTextView = findViewById(R.id.dateTextView);
        firestoreHelper = new FirestoreHelper();

        displayDayOfWeek();

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
