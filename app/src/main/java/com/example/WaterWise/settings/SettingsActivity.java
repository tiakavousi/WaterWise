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
import com.example.WaterWise.utils.SettingsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * SettingsActivity allows the user to view and edit their personal information,
 * such as their name, gender, weight, and daily water intake goal.
 * The activity also provides options for user sign-in and sign-out.
 */
public class SettingsActivity extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView nameValue, genderValue, weightValue, goalValue;
    private Button signOutButton, signUpButton;
    private DataModel dataModel;

    /**
     * Called when the activity is first created. Initializes the UI components and ViewModel,
     * sets up listeners, observes LiveData, and configures navigation options.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
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

    /**
     * Initializes views used in this activity.
     */
    private void initializeViews(){
        profilePicture = findViewById(R.id.profilePicture);
        nameValue = findViewById(R.id.nameValue);
        genderValue = findViewById(R.id.genderValue);
        weightValue = findViewById(R.id.weightValue);
        goalValue = findViewById(R.id.dailyGoalValue);
        signOutButton = findViewById(R.id.signOutButton);
        signUpButton = findViewById(R.id.signUpButton);
    }

    /**
     * Sets up click listeners for the editable fields: name, gender, weight, and daily goal.
     */
    private void setupListeners(){
        nameValue.setOnClickListener(v -> showInputDialog("Name","name"));
        genderValue.setOnClickListener(v -> showGenderDialog());
        weightValue.setOnClickListener(v -> showInputDialog("Weight","weight"));
        goalValue.setOnClickListener(v -> showInputDialog("Daily Goal","dailyGoal"));
    }

    /**
     * Configures the visibility and click actions for the sign-in and sign-out buttons.
     * If a user is logged in, the sign-out button is shown. Otherwise, the sign-up button is shown.
     */
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

    /**
     * Sets up bottom navigation options and handles the actions based on the selected menu item.
     */
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

    /**
     * Updates the profile picture based on the user's water intake progress.
     *
     * @param goal The user's daily water intake goal in milliliters.
     * @param intake The user's current water intake in milliliters.
     */
    private void updateProfilePhoto(int goal, int intake) {
        int profileImageResId = SettingsUtils.getProfileImage(goal, intake);
        profilePicture.setImageResource(profileImageResId);
    }


    /**
     * Observes changes in the DataModel and updates the UI elements accordingly.
     */
    private void observeDataModel() {
        dataModel.getName().observe(this, name -> nameValue.setText(name));
        dataModel.getGender().observe(this, gender -> genderValue.setText(gender));
        dataModel.getWeight().observe(this, weight -> weightValue.setText(String.valueOf(weight)));
        dataModel.getGoal().observe(this, goal -> goalValue.setText(String.format("%sml", goal)));
        dataModel.getIntake().observe(this, intake ->
                updateProfilePhoto(dataModel.getGoal().getValue(), intake)
        );
    }

    /**
     * Shows an input dialog for updating user information such as name, weight, or daily goal.
     *
     * @param title The title of the dialog.
     * @param key The key indicating which value (name, weight, or daily goal) is being updated.
     */
    private void showInputDialog(String title, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Create an EditText field to get user input
        final EditText input = new EditText(this);
        String currentValue = "";

        // Set the current value of the field in the EditText for editing
        if (key.equals("weight")) {
            currentValue = weightValue.getText().toString().replace("kg", "").trim();
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (key.equals("dailyGoal")) {
            currentValue = goalValue.getText().toString().replace("ml", "").trim();
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            currentValue = nameValue.getText().toString();
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        input.setText(currentValue);
        builder.setView(input);

        // Set the save action for the dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            String value = input.getText().toString().trim();

            // Call validation method and proceed if the input is valid
            if (validateInput(value, key)) {
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
                Toast.makeText(
                        SettingsActivity.this,
                        title + " updated",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Set the cancel action for the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Validates the input based on the key (e.g., weight, dailyGoal) and ensures that the value
     * is within the allowed range. Displays a Toast message if the input is invalid.
     *
     * @param value The input value to be validated.
     * @param key   The key indicating which value (name, weight, or daily goal) is being validated.
     * @return True if the input is valid, false otherwise.
     */
    private boolean validateInput(String value, String key) {
        boolean isValid = SettingsUtils.validateInput(value, key);
        if (!isValid) {
            switch (key) {
                case "weight":
                    Toast.makeText(
                            SettingsActivity.this,
                            "Weight must be between 1 and 200 kg.",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                case "dailyGoal":
                    Toast.makeText(
                            SettingsActivity.this,
                            "Daily goal must be between 2L and 5L.",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                case "name":
                    Toast.makeText(
                            SettingsActivity.this,
                            "Name cannot be empty.",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                default:
                    Toast.makeText(
                            SettingsActivity.this,
                            "Invalid input format.",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }
        return isValid;
    }

    /**
     * Shows a dialog for selecting the user's gender.
     */
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