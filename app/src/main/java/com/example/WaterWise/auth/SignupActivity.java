package com.example.WaterWise.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.WaterWise.R;
import com.example.WaterWise.data.DataModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * SignupActivity handles the user registration process using Firebase Authentication.
 * It creates a new user with email and password and saves the sign-up date in Firestore.
 */
public class SignupActivity extends AppCompatActivity {
    // Declare Firebase authentication instance and UI elements
    private FirebaseAuth auth;
    private DataModel dataModel;

    // UI elements
    private EditText signupEmail, signupPassword;
    private TextView loginRedirectText;
    private Button signupButton;

    /**
     * Called when the activity is first created.
     * Initializes the UI elements and sets up the click listener for the sign-up and login redirection.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth instance
        initializeUIElements(); // Initialize UI elements
        setupClickListeners(); // Set click listeners
    }

    /**
     * Sets up the click listeners for the sign-up button and login redirection text.
     * Handles the logic for user registration and saving the sign-up date.
     */
    private void setupClickListeners(){
        // Set click listener for sign-up button
        signupButton.setOnClickListener(view -> {

            // Get email and password input from EditTexts
            String user = signupEmail.getText().toString().trim();
            String pass = signupPassword.getText().toString().trim();

            // Validate input fields: Check if email or password are empty
            if (!validateInput(user, pass)) {
                return;
            }

            // Create a new user with Firebase Authentication
            auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                // If user creation is successful
                if (task.isSuccessful()) {
                    // Format the current date as the sign-up date
                    String signUpDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // Save the sign-up date
                    dataModel = new ViewModelProvider(
                            SignupActivity.this,
                            ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                    ).get(DataModel.class);

                    dataModel.setSignUpDate(signUpDate);

                    Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                } else {
                    // Show error message if user creation fails
                    Toast.makeText(SignupActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        // Set click listener for the login redirection text
        loginRedirectText.setOnClickListener(view -> {
            // Redirect to LoginActivity when the user clicks the "Log In" link
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }

    /**
     * Initializes the UI elements such as EditTexts and Buttons.
     */
    private void initializeUIElements() {
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
    }

    /**
     * Validates the input fields to ensure they are not empty.
     *
     * @param user The email input entered by the user.
     * @param pass The password input entered by the user.
     * @return true if both fields are not empty, false otherwise.
     */
    private boolean validateInput(String user, String pass) {
        if (user.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            return false;
        }
        if (pass.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
}
