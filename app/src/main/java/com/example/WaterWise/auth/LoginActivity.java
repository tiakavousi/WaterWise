package com.example.WaterWise.auth;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.WaterWise.R;
import com.example.WaterWise.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
/**
 * LoginActivity handles user login functionality using Firebase Authentication.
 * It allows the user to enter their email and password to log into the app,
 * redirect to the signup page, or reset their password if forgotten.
 */
public class LoginActivity extends AppCompatActivity {
    // UI elements for email, password input, and buttons
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText, forgotPassword;
    private Button loginButton;
    private FirebaseAuth auth;

    /**
     * Called when the activity is first created.
     * This method sets the layout for the activity, initializes the FirebaseAuth instance,
     * and sets up the UI elements and click listeners.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        initializeUIElements();

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance();

        /* Set up the click listeners for:
        login button, signup redirect text, and forgot password link. */
        setupClickListeners();
    }

    /**
     * Initializes the UI elements of the activity.
     * This method finds the views by their IDs and assigns them to the corresponding variables.
     */
    private void initializeUIElements(){
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
    }

    /**
     * Sets up the click listeners for the login button, signup redirect text, and forgot password link.
     * This method defines the actions that should occur when the respective UI elements are clicked.
     */
    private void setupClickListeners() {
        // Set click listener for login button
        loginButton.setOnClickListener(v -> attemptLogin());

        // Set click listener for signup redirect text
        signupRedirectText.setOnClickListener(
                v -> startActivity(new Intent(
                        LoginActivity.this,
                        SignupActivity.class
                )));

        // Set click listener for forgot password link
        forgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    /**
     * Attempts to log the user in using Firebase Authentication.
     * This method retrieves the email and password from the input fields,
     * validates them, and then attempts to sign in the user.
     */
    private void attemptLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        // Validate email and password fields
        if (!validateInput(email, password)) return;

        // Sign in using Firebase Authentication
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Login Successful",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Login Failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Validates the email and password input fields.
     * This method checks if the email and password fields are not empty and if the email is in a valid format.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return true if the inputs are valid, false otherwise.
     */
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email cannot be empty");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please enter a valid email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password cannot be empty");
            return false;
        }

        return true;
    }

    /**
     * Displays a dialog to allow the user to reset their password if they've forgotten it.
     * This method creates and shows an AlertDialog with input for the user to enter their registered email address.
     */
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        setupForgotPasswordDialog(dialogView, dialog);
        dialog.show();

        // Customize dialog appearance
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
    }

    /**
     * Sets up the forgot password dialog by handling the password reset and cancel actions.
     * This method validates the email input and sends a password reset email if the email is valid.
     *
     * @param dialogView The dialog view that contains the email input and buttons.
     * @param dialog     The AlertDialog instance.
     */
    private void setupForgotPasswordDialog(View dialogView, AlertDialog dialog) {
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        // Handle password reset logic
        dialogView.findViewById(R.id.btnReset).setOnClickListener(view -> {
            String userEmail = emailBox.getText().toString().trim();

            if (TextUtils.isEmpty(userEmail) && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Enter a valid registered email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send reset password email
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Check your email for password reset instructions",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Failed to send reset email",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cancel the dialog
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());
    }
}