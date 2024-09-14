package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    ImageView profilePicture;
    RadioButton maleButton, femaleButton;
    EditText weightInput, goalInput;
    Button logoutButton, signUpButton; // Depending on login status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Create this layout

        profilePicture = findViewById(R.id.profilePicture);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        weightInput = findViewById(R.id.weightInput);
        goalInput = findViewById(R.id.goalInput);
        logoutButton = findViewById(R.id.logoutButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Initially hide both buttons
        logoutButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);

        int goal = getIntent().getIntExtra("goal", 2000); // Default to 2000 if not found
        int progress = getIntent().getIntExtra("progress", 0);
        updateProfilePhoto(goal, progress); // Call the new method to update the photo

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

            // Handle sign up button click (you'll need to implement this)
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
            } else if (itemId == R.id.nav_home) { // Assuming you have a 'nav_home' item in your menu
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }

            return false;
        });
    }

    private void updateProfilePhoto( int goal,  int progress) {
        double percentage = (double) progress / goal * 100;

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
