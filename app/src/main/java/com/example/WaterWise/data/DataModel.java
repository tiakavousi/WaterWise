package com.example.WaterWise.data;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.WaterWise.history.HistoryRecord;
import com.example.WaterWise.home.IntakeRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DataModel serves as the ViewModel that provides data to the UI components and helps manage the application's data.
 * It integrates SharedPreferences for local data storage and Firestore for cloud synchronization, allowing for reactive data updates.
 */
public class DataModel extends AndroidViewModel {
    // Firebase Firestore helper for handling Firestore operations
    private FirestoreHelper firestoreHelper;

    // SharedPreferences keys for storing and retrieving user data locally
    private static final String KEY_GOAL = "key_goal";
    private static final String KEY_INTAKE = "key_intake";
    private static final String KEY_RECORDS = "key_records";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_WEIGHT = "key_weight";
    private static final String KEY_GENDER = "key_gender";
    private static final String KEY_SIGN_UP_DATE = "key_sign_up_date";

    // Default values for user properties
    public static final int DEFAULT_GOAL = 2000;
    public static final int DEFAULT_INTAKE = 0;
    public static final int DEFAULT_WEIGHT = 40;
    public static final String DEFAULT_NAME = "User";
    public static final String DEFAULT_GENDER = "Female";
    public static final String DEFAULT_SIGN_UP_DATE = "Not Available";
    private static final String KEY_LAST_RESET_DATE = "key_last_reset_date";

    // SharedPreferences for local data storage
    private SharedPreferences sharedPreferences;
    private Gson gson;

    // LiveData fields for reactive UI updates
    private MutableLiveData<Integer> goal = new MutableLiveData<>();
    private MutableLiveData<Integer> intake = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<Integer> weight = new MutableLiveData<>();
    private MutableLiveData<String> gender = new MutableLiveData<>();
    private MutableLiveData<List<IntakeRecord>> records = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<HistoryRecord>> historyRecords = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> signUpDate = new MutableLiveData<>();

    /**
     * Constructor for the DataModel.
     * Initializes data model, shared preferences, and Firestore helper.
     *
     * @param application The application instance.
     */
    public DataModel(Application application) {
        super(application);
        String sharedPrefsName = getSharedPrefsName();
        sharedPreferences = application.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
        gson = new Gson();
        firestoreHelper = new FirestoreHelper();

        // Reset intake data if a new day starts
        checkAndResetDataIfNeeded();

        // Load data from shared preferences
        loadAllData();
//        checkAndFetchSignUpDate();

        // Start listening to Firestore for data updates
        startListeningToFirestore();
    }

    /**
     * Gets the name for shared preferences based on the user ID.
     *
     * @return The name for shared preferences.
     */
    private String getSharedPrefsName() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return "waterwise_prefs_" + userId;
    }

    /**
     * Resets intake data if today is a new day based on the last reset date.
     */
    private void checkAndResetDataIfNeeded() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastResetDate = sharedPreferences.getString(KEY_LAST_RESET_DATE, null);

        if (lastResetDate == null || !lastResetDate.equals(currentDate)) {
            // Reset daily intake and records for the new day
            setIntake(0);
            setRecords(new ArrayList<>());
            sharedPreferences.edit().putString(KEY_LAST_RESET_DATE, currentDate).apply();
            Log.d("Intake After RESET", getIntake().getValue() + "");
        } else{
            Log.d("DataModel", "No reset needed.");
        }
    }

    // Getter methods for LiveData fields
    public MutableLiveData<Integer> getGoal() { return goal; }
    public MutableLiveData<Integer> getIntake() { return intake; }
    public MutableLiveData<String> getName() { return name; }
    public MutableLiveData<Integer> getWeight() { return weight; }
    public MutableLiveData<String> getGender() { return gender; }
    public MutableLiveData<List<IntakeRecord>> getRecords() { return records; }
    public MutableLiveData<List<HistoryRecord>> getHistoryRecords() { return historyRecords;}
    public MutableLiveData<String> getSignUpDate() { return signUpDate; }

    /**
     * Sets the user's name, updates shared preferences, and synchronizes with Firestore.
     *
     * @param nameValue The new name of the user.
     */
    public void setName(String nameValue) {
        name.setValue(nameValue);
        saveToPreferences(KEY_NAME, nameValue);
        firestoreHelper.saveUserData(nameValue, goal.getValue(), weight.getValue(), gender.getValue());
    }

    /**
     * Sets the user's weight, updates shared preferences, and synchronizes with Firestore.
     *
     * @param weightValue The new weight of the user.
     */
    public void setWeight(int weightValue) {
        weight.setValue(weightValue);
        saveToPreferences(KEY_WEIGHT, weightValue);
        firestoreHelper.saveUserData(name.getValue(), goal.getValue(), weightValue, gender.getValue());
    }

    /**
     * Sets the user's gender, updates shared preferences, and synchronizes with Firestore.
     *
     * @param genderValue The new gender of the user.
     */
    public void setGender(String genderValue) {
        gender.setValue(genderValue);
        saveToPreferences(KEY_GENDER, genderValue);
        firestoreHelper.saveUserData(name.getValue(), goal.getValue(), weight.getValue(), genderValue);
    }

    /**
     * Sets the user's intake records, and updates shared preferences.
     *
     * @param newRecords The new list of intake records.
     */
    public void setRecords(List<IntakeRecord> newRecords) {
        records.setValue(newRecords);
        saveToPreferences(KEY_RECORDS, gson.toJson(newRecords));
    }

    /**
     * Sets the user's daily water intake goal, updates shared preferences, and synchronizes with Firestore.
     *
     * @param goalValue The new daily intake goal.
     */
    public void setGoal(int goalValue) {
        goal.setValue(goalValue);
        saveToPreferences(KEY_GOAL, goalValue);
        firestoreHelper.saveUserData(name.getValue(), goalValue, weight.getValue(), gender.getValue());
    }

    /**
     * Sets the user's current water intake, and updates shared preferences.
     *
     * @param intakeValue The new intake value.
     */
    public void setIntake(int intakeValue) {
        intake.setValue(intakeValue);
        saveToPreferences(KEY_INTAKE, intakeValue);
    }

    /**
     * Adds a new water intake record, updates the LiveData list, and saves it to Firestore.
     *
     * @param record The new intake record to be added.
     */
    public void addRecord(IntakeRecord record) {
        List<IntakeRecord> currentRecords = records.getValue();
        if (currentRecords != null) {
            currentRecords.add(record);
            setRecords(currentRecords);
        }
        firestoreHelper.saveWaterIntakeRecord(record.getTime(),record.getDate(), record.getAmount());
    }

    /**
     * Sets the user's sign-up date and saves it to SharedPreferences.
     *
     * @param signUpDateValue The sign-up date of the user.
     */
    public void setSignUpDate(String signUpDateValue) {
        signUpDate.setValue(signUpDateValue);
        saveToPreferences(KEY_SIGN_UP_DATE, signUpDateValue);
        firestoreHelper.saveSignUpDate(signUpDateValue);
    }

//    public void checkAndFetchSignUpDate() {
//        // Check if sign-up date is already available in LiveData or SharedPreferences
//        if (getSignUpDate().getValue() == null || getSignUpDate().getValue().isEmpty() ||
//                getSignUpDate().getValue().equals(DEFAULT_SIGN_UP_DATE)) {
//
//            // Fetch sign-up date from Firestore
//            firestoreHelper.fetchSignUpDate(fetchedSignUpDateStr -> {
//                if (fetchedSignUpDateStr != null) {
//                    // Set and save the fetched sign-up date in LiveData and SharedPreferences
//                    setSignUpDate(fetchedSignUpDateStr);
//                } else {
//                    Log.e("DataModel", "Failed to fetch sign-up date from Firestore.");
//                }
//            });
//        } else {
//            Log.d("DataModel", "Sign-up date is already available: " + getSignUpDate().getValue());
//        }
//    }


    /**
     * Loads all data from shared preferences.
     */
    private void loadAllData() {
        loadFromPreferences(goal, KEY_GOAL, DEFAULT_GOAL);
        loadFromPreferences(intake, KEY_INTAKE, DEFAULT_INTAKE);
        loadRecords();
        loadFromPreferences(name, KEY_NAME, DEFAULT_NAME);
        loadFromPreferences(weight, KEY_WEIGHT, DEFAULT_WEIGHT);
        loadFromPreferences(gender, KEY_GENDER, DEFAULT_GENDER);
        loadFromPreferences(signUpDate, KEY_SIGN_UP_DATE, DEFAULT_SIGN_UP_DATE);
    }

    /**
     * Loads data from shared preferences into MutableLiveData.
     *
     * @param liveData      The LiveData field to be set.
     * @param key           The key in shared preferences.
     * @param defaultValue  The default value if the key does not exist.
     * @param <T>           The type of data to be loaded.
     */
    private <T> void loadFromPreferences(MutableLiveData<T> liveData, String key, T defaultValue) {
        if (defaultValue instanceof Integer) {
            liveData.setValue((T) Integer.valueOf(sharedPreferences.getInt(key, (Integer) defaultValue)));
        } else if (defaultValue instanceof String) {
            String value = sharedPreferences.getString(key, (String) defaultValue);
            if (value != null && !value.isEmpty()) {
                liveData.setValue((T) value);
            } else {
                liveData.setValue(defaultValue);
            }
        }
    }

    /**
     * Loads the user's water intake records from shared preferences.
     */
    private void loadRecords() {
        String json = sharedPreferences.getString(KEY_RECORDS, null);
        Type type = new TypeToken<List<IntakeRecord>>() {}.getType();
        List<IntakeRecord> savedRecords = json != null ? gson.fromJson(json, type) : new ArrayList<>();
        records.setValue(savedRecords);
        for ( IntakeRecord i : records.getValue()) {
            Log.d("r e c o r d s", i.getAmount()+"");
        }
    }

    /**
     * Saves data to SharedPreferences.
     *
     * @param key   The key to be saved in SharedPreferences.
     * @param value The value to be saved.
     */
    private void saveToPreferences(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }

    /**
     * Starts listening to Firestore for changes and updates the LiveData fields accordingly.
     */
    private void startListeningToFirestore() {
        firestoreHelper.fetchUserData(this, (goal, intake) -> {
            setGoal(goal);
            setIntake(intake);
        });
    }

    public void loadHistoryRecords() {
        String signUpDateStr = getSignUpDate().getValue();
        if (signUpDateStr == null || signUpDateStr.isEmpty()) {
            Log.d("DataModel", "Sign-up date is missing.");
            historyRecords.setValue(new ArrayList<>()); // Handle empty sign-up date
            return;
        }
        String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Generate a list of dates from sign-up date to current date
        List<String> allDates = getDatesBetween(signUpDateStr, currentDateStr);

        // Fetch intake records for all dates
        loadIntakeRecordsForDates(allDates);
    }

    /**
     * Fetches intake records for a list of dates and updates LiveData.
     *
     * @param allDates The list of dates to fetch intake records for.
     */
    private void loadIntakeRecordsForDates(List<String> allDates) {
        firestoreHelper.fetchIntakeForDates(allDates, historyRecordsResult -> {
            if (historyRecordsResult != null && !historyRecordsResult.isEmpty()) {
                historyRecords.setValue(historyRecordsResult); // Update LiveData with the full history records
            } else {
                Log.d("DataModel", "No history records found.");
                historyRecords.setValue(new ArrayList<>());
            }
        });
    }

    private List<String> getDatesBetween(String startDateStr, String endDateStr) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
//        startDateStr = "2024-10-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            // Parse the start and end dates from strings
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            // Set the calendar to the start date
            calendar.setTime(startDate);

            while (!calendar.getTime().after(endDate)) {
                dates.add(dateFormat.format(calendar.getTime())); // Add the date string
                calendar.add(Calendar.DATE, 1); // Move to the next day
            }

        } catch (ParseException e) {
            Log.e("DataModel", "ParseException in getDatesBetween", e);
        }

        return dates;
    }
}