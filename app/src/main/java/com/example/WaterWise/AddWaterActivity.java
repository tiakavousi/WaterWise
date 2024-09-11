package com.example.WaterWise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddWaterActivity extends AppCompatActivity {

    EditText edtWaterAmount;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);

        edtWaterAmount = findViewById(R.id.edtWaterAmount);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            int amount = Integer.parseInt(edtWaterAmount.getText().toString());
            int progress = getSharedPreferences("WaterTracker", MODE_PRIVATE).getInt("progress", 0);
            progress += amount;

            getSharedPreferences("WaterTracker", MODE_PRIVATE).edit().putInt("progress", progress).apply();
            Toast.makeText(AddWaterActivity.this, "Water added!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the main screen
        });
    }
}
