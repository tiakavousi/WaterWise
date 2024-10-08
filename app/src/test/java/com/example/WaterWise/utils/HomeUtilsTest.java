package com.example.WaterWise.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.WaterWise.home.IntakeRecord;

import org.junit.Test;

public class HomeUtilsTest {

    @Test
    public void testCreateIntakeRecord() {
        int amount = 500; // milliliters
        IntakeRecord record = HomeUtils.createIntakeRecord(amount);

        assertNotNull(record);
        assertNotNull(record.getDate());
        assertNotNull(record.getTime());
        assertNotNull(record.getAmount());
    }


    @Test
    public void testCalculateIntakePercentage() {
        // Test intake percentage calculation
        assertEquals(75f, HomeUtils.calculateIntakePercentage(2000, 1500), 0.1f);
        assertEquals(0f, HomeUtils.calculateIntakePercentage(2000, 0), 0.1f);
        assertEquals(100f, HomeUtils.calculateIntakePercentage(2000, 2000), 0.1f);
    }

    @Test
    public void testFormatCenterText() {
        // Test center text formatting
        String centerText = HomeUtils.formatCenterText(75f, 1.5f);
        assertEquals("75%\n1.5L", centerText);

        centerText = HomeUtils.formatCenterText(100f, 2.0f);
        assertEquals("100%\n2L", centerText);
    }
}

