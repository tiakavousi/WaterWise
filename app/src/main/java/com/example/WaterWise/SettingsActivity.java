package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    ImageView profilePicture;
    RadioButton maleButton, femaleButton;
    EditText weightInput, goalInput;
    Button logoutButton, signUpButton, btnSaveGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profilePicture = findViewById(R.id.profilePicture);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        weightInput = findViewById(R.id.weightInput);
        goalInput = findViewById(R.id.goalInput);
        logoutButton = findViewById(R.id.logoutButton);
        signUpButton = findViewById(R.id.signUpButton);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);

        // Initially hide both buttons
        logoutButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);

        // Retrieve and update profile photo based on goal and progress
        int goal = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("goal", 2000);
        int intake = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("intake", 0);
        updateProfilePhoto(goal, intake);

        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in, show logout button
            logoutButton.setVisibility(View.VISIBLE);

            logoutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, EntryActivity.class));
                finish();
            });
        } else {
            // User is not logged in, show sign up button
            signUpButton.setVisibility(View.VISIBLE);

            // Handle sign up button click
            signUpButton.setOnClickListener(v -> {
                startActivity(new Intent(SettingsActivity.this, EntryActivity.class));
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_history) {
                startActivity(new Intent(SettingsActivity.this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }

            return false;
        });

        // Handle Save Goal button click
        btnSaveGoal.setOnClickListener(v -> {
            try {
                int newGoal = Integer.parseInt(goalInput.getText().toString());
                getSharedPreferences("WaterTracker", MODE_PRIVATE).edit().putInt("goal", newGoal).apply();
                Toast.makeText(SettingsActivity.this, "Goal Set!", Toast.LENGTH_SHORT).show();
                updateProfilePhoto(newGoal, getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("intake", 0));
            } catch (NumberFormatException e) {
                Toast.makeText(SettingsActivity.this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfilePhoto(int goal, int intake) {
        double percentage = (double) intake / goal * 100;

        if (percentage <= 25) {
            profilePicture.setImageResource(R.drawable.thirsty_cat);
        } else if (percentage <= 50) {
            profilePicture.setImageResource(R.drawable.drinking_cat);
        } else if (percentage <= 75) {
            profilePicture.setImageResource(R.drawable.cool_cat);
        } else {
            profilePicture.setImageResource(R.drawable.happy_cat);
        }
    }
}
