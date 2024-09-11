package com.example.WaterWise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SetGoalActivity extends AppCompatActivity {

    EditText edtGoal;
    Button btnSetGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        edtGoal = findViewById(R.id.edtGoal);
        btnSetGoal = findViewById(R.id.btnSetGoal);

        btnSetGoal.setOnClickListener(v -> {
            int goal = Integer.parseInt(edtGoal.getText().toString());
            getSharedPreferences("WaterTracker", MODE_PRIVATE).edit().putInt("goal", goal).apply();
            Toast.makeText(SetGoalActivity.this, "Goal Set!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the main screen
        });
    }
}
