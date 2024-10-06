package com.example.WaterWise.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.WaterWise.R;
import com.example.WaterWise.data.FirestoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * SignupActivity handles the user registration process using Firebase Authentication.
 * It creates a new user with email and password and saves the sign-up date in Firestore.
 */
public class SignupActivity extends AppCompatActivity {
    // Declare Firebase authentication instance and UI elements
    private FirebaseAuth auth;
//    private DataModel dataModel;
    private FirestoreHelper firestoreHelper;

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

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

//        dataModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
//                .get(DataModel.class);

        // Initialize UI elements
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Set click listener for sign-up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get email and password input from EditTexts
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();

                // Validate input fields: Check if email or password are empty
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else {
                    // Create a new user with Firebase Authentication
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If user creation is successful
                            if (task.isSuccessful()) {
                                // Format the current date as the sign-up date
                                String signUpDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                // Save the sign-up date to Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String userId = auth.getCurrentUser().getUid();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("signUpDate", signUpDate);
//                                dataModel.setSignUpDate(signUpDate);


                                // Add the sign-up date to the Firestore database for the current user
                                db.collection("users").document(userId).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // If saving sign-up date is successful, show success message and redirect to LoginActivity
                                            Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        } else {
                                            // Show error message if saving sign-up date fails
                                            Toast.makeText(
                                                    SignupActivity.this,
                                                     "Failed to save sign-up date: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            } else {
                                // Show error message if user creation fails
                                Toast.makeText(SignupActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        // Set click listener for the login redirection text
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to LoginActivity when the user clicks the "Log In" link
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }
}
