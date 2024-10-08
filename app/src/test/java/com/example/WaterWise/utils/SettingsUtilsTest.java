package com.example.WaterWise.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.WaterWise.R;

import org.junit.Test;

public class SettingsUtilsTest {

    @Test
    public void testValidateInput() {
        assertFalse(SettingsUtils.validateInput("1500", "dailyGoal"));
        assertFalse(SettingsUtils.validateInput("100", "dailyGoal"));
        assertTrue(SettingsUtils.validateInput("John Doe", "name"));
        assertFalse(SettingsUtils.validateInput("", "name"));
        assertTrue(SettingsUtils.validateInput("70", "weight"));
        assertFalse(SettingsUtils.validateInput("300", "weight")); // Out of range
    }

    @Test
    public void testGetProfileImage() {
        int result = SettingsUtils.getProfileImage(2000, 500);
        assertEquals(R.drawable.thirsty_cat, result);

        result = SettingsUtils.getProfileImage(2000, 1000);
        assertEquals(R.drawable.drinking_cat, result);

        result = SettingsUtils.getProfileImage(2000, 1500);
        assertEquals(R.drawable.cool_cat, result);

        result = SettingsUtils.getProfileImage(2000, 2000);
        assertEquals(R.drawable.happy_cat, result);
    }
}

