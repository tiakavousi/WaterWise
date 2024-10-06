package com.example.WaterWise.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.WaterWise.R;
import com.example.WaterWise.auth.EntryActivity;
import com.example.WaterWise.data.DataModel;
import com.example.WaterWise.history.HistoryActivity;
import com.example.WaterWise.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView nameValue, genderValue, weightValue, goalValue;
    private Button signOutButton, signUpButton;
    private DataModel dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // initialize data model
        dataModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(DataModel.class);

        initializeViews();          // Initialize UI components
        observeDataModel();         // Observe DataModel values and update UI accordingly
        setupAuthButtons();         // Set up sign-in/out buttons
        setupBottomNavigation();    // Set up bottom navigation
        setupListeners();           // Set up listeners for UI inputs
    }

    // Initialize views
    private void initializeViews(){
        profilePicture = findViewById(R.id.profilePicture);
        nameValue = findViewById(R.id.nameValue);
        genderValue = findViewById(R.id.genderValue);
        weightValue = findViewById(R.id.weightValue);
        goalValue = findViewById(R.id.dailyGoalValue);
        signOutButton = findViewById(R.id.signOutButton);
        signUpButton = findViewById(R.id.signUpButton);
    }

    // Set up click listeners for editable fields
    private void setupListeners(){
        nameValue.setOnClickListener(v -> showInputDialog("Name","name"));
        genderValue.setOnClickListener(v -> showGenderDialog());
        weightValue.setOnClickListener(v -> showInputDialog("Weight","weight"));
        goalValue.setOnClickListener(v -> showInputDialog("Daily Goal","dailyGoal"));
    }

    // Set up sign-in and sign-out button visibility and actions
    private void setupAuthButtons(){
        // By default, hide both buttons
        signOutButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);

        // Show sign-out button if user is logged in, otherwise show sign-up button
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            signOutButton.setVisibility(View.VISIBLE);

            signOutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, EntryActivity.class));
                finish();
            });
        } else {
            signUpButton.setVisibility(View.VISIBLE);
            signUpButton.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, EntryActivity.class)));
        }
    }

    // Set up bottom navigation and handle navigation item clicks
    private void setupBottomNavigation(){
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
            } else return itemId == R.id.nav_settings;
        });
    }

    // Update the profile picture based on water intake goal progress
    private void updateProfilePhoto(int goal, int intake) {
        double percentage = (double) intake / goal * 100;

        // Set profile picture based on the percentage of goal completed
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

    // Observe changes in the data model and update UI elements accordingly
    private void observeDataModel() {
        dataModel.getName().observe(this, name -> nameValue.setText(name));
        dataModel.getGender().observe(this, gender -> genderValue.setText(gender));
        dataModel.getWeight().observe(this, weight -> weightValue.setText(String.valueOf(weight)));
        dataModel.getGoal().observe(this, goal -> goalValue.setText(String.format("%sml", goal)));
        dataModel.getIntake().observe(this, intake -> updateProfilePhoto(dataModel.getGoal().getValue(), intake));
    }

    // Show an input dialog for updating user details (e.g., name, weight, daily goal)
    private void showInputDialog(String title, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Create an EditText field to get user input
        final EditText input = new EditText(this);
        String currentValue;

        // Set the current value of the field in the EditText for editing
        if (key.equals("weight")) {
            currentValue = weightValue.getText().toString();
        } else if (key.equals("dailyGoal")) {
            currentValue = goalValue.getText().toString();
        } else {
            currentValue = nameValue.getText().toString();
        }
        input.setText(currentValue);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set the save action for the dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                switch (key) {
                    case "name":
                        dataModel.setName(value);
                        break;
                    case "weight":
                        dataModel.setWeight(Integer.parseInt(value));
                        break;
                    case "dailyGoal":
                        dataModel.setGoal(Integer.parseInt(value));
                        break;
                }
                Toast.makeText(SettingsActivity.this, title + " updated", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the cancel action for the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Show a dialog for selecting gender
    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        String[] genders = {"Male", "Female"};

        // Show gender selection options
        builder.setItems(genders, (dialog, which) -> {
            String selectedGender = genders[which];
            dataModel.setGender(selectedGender);
            genderValue.setText(selectedGender);
        });
        builder.show();
    }
}