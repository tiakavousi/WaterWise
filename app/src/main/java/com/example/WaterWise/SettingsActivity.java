package com.example.WaterWise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {
    ImageView profilePicture;
    TextView nameValue, genderValue, weightValue, dailyGoalValue;
    Button signOutButton, signUpButton;
    DataModel dataModel;
    FirestoreHelper firestoreHelper = new FirestoreHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dataModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(DataModel.class);

        // Initialize views
        profilePicture = findViewById(R.id.profilePicture);
        nameValue = findViewById(R.id.nameValue);
        genderValue = findViewById(R.id.genderValue);
        weightValue = findViewById(R.id.weightValue);
        dailyGoalValue = findViewById(R.id.dailyGoalValue);
        signOutButton = findViewById(R.id.signOutButton);
        signUpButton = findViewById(R.id.signUpButton);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestoreHelper.fetchUserData(userId, dataModel, (goal, intake) -> observeDataModel());
        }



        updateProfilePhoto(dataModel.getGoal().getValue() != null ? dataModel.getGoal().getValue() : 2000,
                dataModel.getIntake().getValue() != null ? dataModel.getIntake().getValue() : 0);

        nameValue.setOnClickListener(v -> showInputDialog("Name","name"));
        genderValue.setOnClickListener(v -> showGenderDialog());
        weightValue.setOnClickListener(v -> showInputDialog("Weight","weight"));
        dailyGoalValue.setOnClickListener(v -> showInputDialog("Daily Goal","dailyGoal"));

        // Sign up and Sign out
        signOutButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
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

    private void observeDataModel() {
        dataModel.getName().observe(this, name -> nameValue.setText(name != null ? name : "name not set"));

        dataModel.getGender().observe(this, gender -> genderValue.setText(gender != null ? gender : "Gender not set"));

        dataModel.getWeight().observe(this, weight -> weightValue.setText(weight != null ? weight : "Weight not set"));

        dataModel.getGoal().observe(this, goal -> {
            dailyGoalValue.setText(goal != null ? String.format("%sml", goal) : "Goal not set");
            updateProfilePhoto(goal != null ? goal : 2000,
                    dataModel.getIntake().getValue() != null ? dataModel.getIntake().getValue() : 0);
        });

        dataModel.getIntake().observe(this, intake -> {
        });
    }

    @SuppressLint("DefaultLocale")
    private void showInputDialog(String title, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);

        String currentValue;
        if (key.equals("weight")) {
            currentValue = weightValue.getText().toString();
        } else if (key.equals("dailyGoal")) {
            currentValue = dailyGoalValue.getText().toString();
        } else {
            currentValue = nameValue.getText().toString();
        }
        input.setText(currentValue);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                switch (key) {
                    case "name":
                        dataModel.setName(value);
                        break;
                    case "weight":
                        dataModel.setWeight(value + " kg");
                        break;
                    case "dailyGoal":
                        dataModel.setGoal(Integer.parseInt(value));
                        break;
                }

                firestoreHelper.saveUserData(
                        dataModel.getName().getValue(),
                        dataModel.getGoal().getValue(),
                        dataModel.getIntake().getValue(),
                        dataModel.getWeight().getValue(),
                        dataModel.getGender().getValue()
                );
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
            dataModel.setGender(selectedGender);
            genderValue.setText(selectedGender);

            firestoreHelper.saveUserData(
                    dataModel.getName().getValue(),
                    dataModel.getGoal().getValue(),
                    dataModel.getIntake().getValue(),
                    dataModel.getWeight().getValue(),
                    dataModel.getGender().getValue()
            );
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestoreHelper.fetchUserData(userId, dataModel, (goal, intake) -> observeDataModel());
        }
    }

}