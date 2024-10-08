package com.example.WaterWise.utils;

import com.example.WaterWise.R;

public class SettingsUtils {

    /**
     * Validates the input based on the key (e.g., weight, dailyGoal) and ensures that the value
     * is within the allowed range.
     *
     * @param value The input value to be validated.
     * @param key   The key indicating which value (name, weight, or daily goal) is being validated.
     * @return True if the input is valid, false otherwise.
     */
    public static boolean validateInput(String value, String key) {
        try {
            switch (key) {
                case "weight":
                    int weight = Integer.parseInt(value);
                    return weight > 0 && weight <= 200; // Weight must be between 1 and 200 kg

                case "dailyGoal":
                    int dailyGoal = Integer.parseInt(value);
                    return dailyGoal >= 2000 && dailyGoal <= 5000; // Goal must be between 2L and 5L

                case "name":
                    return !value.isEmpty(); // Name cannot be empty

                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }
    }

    /**
     * Determines which profile image to display based on the percentage of the user's water intake goal.
     *
     * @param goal   The user's daily water intake goal in milliliters.
     * @param intake The user's current water intake in milliliters.
     * @return The resource ID of the appropriate image.
     */
    public static int getProfileImage(int goal, int intake) {
        double percentage = (double) intake / goal * 100;

        if (percentage <= 25) {
            return R.drawable.thirsty_cat;
        } else if (percentage <= 50) {
            return R.drawable.drinking_cat;
        } else if (percentage <= 75) {
            return R.drawable.cool_cat;
        } else {
            return R.drawable.happy_cat;
        }
    }
}

