package com.example.WaterWise;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EntryActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Check if the user is already logged in
        if (currentUser != null) {
            // User is logged in, redirect to home page (MainActivity)
            startActivity(new Intent(EntryActivity.this, MainActivity.class));
        } else {
            // User is not logged in, redirect to SignupActivity
            startActivity(new Intent(EntryActivity.this, SignupActivity.class));
        }
        // Finish this activity so the user can't return to it
        finish();
    }
}
