package com.example.WaterWise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {
    ImageView profilePicture;
    TextView nicknameValue, genderValue, weightValue, dailyGoalValue;
    SharedPreferences sharedPreferences;
    Button signOutButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        profilePicture = findViewById(R.id.profilePicture);
        nicknameValue = findViewById(R.id.nicknameValue);
        genderValue = findViewById(R.id.genderValue);
        weightValue = findViewById(R.id.weightValue);
        dailyGoalValue = findViewById(R.id.dailyGoalValue);
        signOutButton = findViewById(R.id.signOutButton);
        signUpButton = findViewById(R.id.signUpButton);

        sharedPreferences = getSharedPreferences("WaterTracker", MODE_PRIVATE);

        // Set initial values from SharedPreferences or default values
        nicknameValue.setText(sharedPreferences.getString("nickname", "Tia"));
        genderValue.setText(sharedPreferences.getString("gender", "Female"));
        weightValue.setText(sharedPreferences.getString("weight", "48kg"));
        dailyGoalValue.setText(sharedPreferences.getString("dailyGoal", "3L"));

        // Update profile picture based on current intake and goal
        int goal = sharedPreferences.getInt("goal", 3000);  // Default 3L goal
        int intake = sharedPreferences.getInt("intake", 0);  // Default intake 0
        updateProfilePhoto(goal, intake);

        // Set click listeners for updating fields
        nicknameValue.setOnClickListener(v -> showInputDialog("Nickname", "Enter your nickname", "nickname", nicknameValue));
        genderValue.setOnClickListener(v -> showGenderDialog());
        weightValue.setOnClickListener(v -> showInputDialog("Weight", "Enter your weight", "weight", weightValue));
        dailyGoalValue.setOnClickListener(v -> showInputDialog("Daily Goal", "Enter your daily goal (L)", "dailyGoal", dailyGoalValue));

        // Sign up and Sign out
        signOutButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in, show logout button
            signOutButton.setVisibility(View.VISIBLE);

            signOutButton.setOnClickListener(v -> {
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

        // Navigation menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_add_water).setVisible(false);
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

    private void showInputDialog(String title, String hint, String key, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setHint(hint);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                sharedPreferences.edit().putString(key, value).apply();
                textView.setText(value);
                Toast.makeText(SettingsActivity.this, title + " updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        String[] genders = {"Male", "Female"};
        builder.setItems(genders, (dialog, which) -> {
            String selectedGender = genders[which];
            sharedPreferences.edit().putString("gender", selectedGender).apply();
            genderValue.setText(selectedGender);
        });
        builder.show();
    }

}