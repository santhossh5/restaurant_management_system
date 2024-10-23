package com.santhossh.restaurant_management_system;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private BarGraphViewActivity barGraphViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports); // Ensure this layout contains BarGraphView

        // Initialize the BarGraphView
        barGraphViewActivity = findViewById(R.id.barGraphView);

        // Example sales data for the last 7 days
        List<Float> salesData = new ArrayList<>();
        salesData.add(1000f);  // Day 1
        salesData.add(1500f);  // Day 2
        salesData.add(2000f);  // Day 3
        salesData.add(1700f);  // Day 4
        salesData.add(1200f);  // Day 5
        salesData.add(1800f);  // Day 6
        salesData.add(2200f);  // Day 7

        // Labels for the days
        List<String> labels = new ArrayList<>();
        labels.add("Day 1");
        labels.add("Day 2");
        labels.add("Day 3");
        labels.add("Day 4");
        labels.add("Day 5");
        labels.add("Day 6");
        labels.add("Day 7");

        // Set the data to the BarGraphView
        barGraphViewActivity.setData(salesData, labels);
    }
}
