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
import com.example.WaterWise.utils.HistoryUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * HistoryActivity displays the user's history of water intake along with the current day's
 * date, goal, and remaining water intake to achieve the goal. It uses a RecyclerView to
 * display history records and integrates with the BottomNavigationView for app navigation.
 */
public class HistoryActivity extends AppCompatActivity {
    private DataModel dataModel;
    private int userGoal;
    // UI elements for displaying day of the week and current date
    private TextView dayOfWeekTextView, dateTextView;

    /**
     * Called when the activity is first created. Initializes the UI and sets up the RecyclerView,
     * BottomNavigationView, and observes LiveData for changes.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Initialize ViewModel and observe goal
        dataModel = new ViewModelProvider(this).get(DataModel.class);
        dataModel.getGoal().observe(this, goal -> {userGoal = goal;});

        initializeUIElements();
        displayDayOfWeek();
        setUpBottomNavigationBar();
        setUpHistoryRecyclerView();
        dataModel.loadHistoryRecords();
    }

    /**
     * Initialize UI elements such as TextViews for displaying the day of the week,
     * current date, goal, and remaining water intake.
     */
    private void initializeUIElements(){
        dayOfWeekTextView = findViewById(R.id.dayOfWeekTextView);
        dateTextView = findViewById(R.id.dateTextView);
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and an adapter for displaying history records.
     * The data is observed from the ViewModel and sorted by date in descending order.
     */
    private void setUpHistoryRecyclerView() {
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        // Observe the history records from the ViewModel
        dataModel.getHistoryRecords().observe(this, historyList -> {
            // Sort history records by date (most recent first)
            sortHistoryRecords(historyList);
            // Fetch the goal from the data model
            int goalInMiliLiters = dataModel.getGoal().getValue();
            // Set the adapter for the RecyclerView
            HistoryRecordAdapter adapter = new HistoryRecordAdapter(historyList, goalInMiliLiters);
            recyclerView2.setAdapter(adapter);
        });
    }

    /**
     * Sorts the history records by date in descending order (most recent first).
     *
     * @param historyList List of history records to be sorted.
     */
    private void sortHistoryRecords(List<HistoryRecord> historyList) {
//        Collections.sort(historyList, (record1, record2)-> {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            try {
//                // Parse the dates from strings and compare them
//                Date date1 = dateFormat.parse(record1.getDate());
//                Date date2 = dateFormat.parse(record2.getDate());
//                return date2.compareTo(date1); // Sort in descending order
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return 0;
//        });
        HistoryUtils.sortHistoryRecords(historyList);
    }


    /**
     * Displays the current day of the week and date at the top of the activity.
     */
    public void displayDayOfWeek() {
        Date currentDate = new Date();
        // Format the current day of the week (e.g., "Monday")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String currentDay = sdf.format(new Date());

        // Format the current date (e.g., "30 Aug 2024")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Set the day of the week and date in the TextViews
        dayOfWeekTextView.setText(currentDay);
        dateTextView.setText(formattedDate);
    }

    /**
     * Sets up the BottomNavigationView to handle navigation between different activities.
     */
    public void setUpBottomNavigationBar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_add_water).setVisible(false);
        // Set navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_settings) {
                startActivity(new Intent(HistoryActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                return true;
            } else return itemId == R.id.nav_history; // Stay on the current activity
        });
    }
}
