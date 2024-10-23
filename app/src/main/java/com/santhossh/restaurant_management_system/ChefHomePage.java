package com.santhossh.restaurant_management_system;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChefHomePage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button btnViewOrders, btnUpdateInventory;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_home);  // Link to the XML layout

        // Initialize the CoordinatorLayout
        coordinatorLayout = findViewById(R.id.coordinatorLayout);  // Add an ID to your root layout if not already done

        // Initialize and set the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize the buttons
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnUpdateInventory = findViewById(R.id.btnUpdateInventory);
        Log.d("ChefHomePage", "Buttons initialized");

        // Set click listeners for buttons
        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChefHomePage.this, ViewOrdersActivity.class);
                startActivity(intent);
            }
        });

        btnUpdateInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChefHomePage.this, UpdateInventoryActivity.class);
                startActivity(intent);
            }
        });

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight Home as the default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        // Set a listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Get the item ID

                if (itemId == R.id.action_view_orders) {
                    // Navigate to the ViewOrdersActivity
                    startActivity(new Intent(ChefHomePage.this, orderStatusActivityChef.class));
                    return true;
                } else if (itemId == R.id.action_home) {
                    // Stay on the same activity (Home)
                    return true;
                } else if (itemId == R.id.action_update_inventory) {
                    // Navigate to the UpdateInventoryActivity
                    startActivity(new Intent(ChefHomePage.this, UpdateInventoryActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Start the color-changing background animation
        startColorAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure "Home" is highlighted when coming back from another activity
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Method to start the background color animation
    private void startColorAnimation() {
        // Define start and end colors
        int colorStart = Color.parseColor("#dcb8e3"); // Yellow
        int colorEnd = Color.parseColor("#f0d8ef");   // Orange

        // Create an ObjectAnimator to animate the background color
        ObjectAnimator colorAnim = ObjectAnimator.ofObject(coordinatorLayout,
                "backgroundColor",
                new ArgbEvaluator(),
                colorStart,
                colorEnd);

        // Set the duration and repeat the animation infinitely with reverse
        colorAnim.setDuration(3000);  // 3 seconds for a full transition
        colorAnim.setRepeatMode(ObjectAnimator.REVERSE);
        colorAnim.setRepeatCount(ObjectAnimator.INFINITE);
        colorAnim.start();
    }
}
