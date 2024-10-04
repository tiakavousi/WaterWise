package com.example.WaterWise.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.WaterWise.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * EntryActivity serves as the entry point of the app.
 * It checks if a user is currently authenticated with Firebase.
 * If a user is logged in, the app navigates to the MainActivity.
 * If no user is logged in, it navigates to the SignupActivity.
 */
public class EntryActivity extends AppCompatActivity {
    // FirebaseAuth instance to manage user authentication
    private FirebaseAuth auth;

    /**
     * onCreate method is called when the activity is first created.
     * It checks the authentication status of the user.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Get the currently authenticated user (if any)
        FirebaseUser currentUser = auth.getCurrentUser();

        // Check if the user is already authenticated
        if (currentUser != null) {
            startActivity(new Intent(EntryActivity.this, MainActivity.class));
        } else {
            // If no user is authenticated, redirect to SignupActivity
            startActivity(new Intent(EntryActivity.this, SignupActivity.class));
        }
        // Finish EntryActivity to prevent returning to this activity when pressing the back button
        finish();
    }
}
