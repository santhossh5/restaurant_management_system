package com.santhossh.restaurant_management_system;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ViewOrdersActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        // Initialize and set the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight "View Orders" as the default selection
        bottomNavigationView.setSelectedItemId(R.id.action_view_orders);

        // Set a listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.action_view_orders) {
                    // Stay on the same activity (View Orders)
                    return true;
                } else if (itemId == R.id.action_home) {
                    startActivity(new Intent(ViewOrdersActivity.this, ChefHomePage.class));
                    return true;
                } else if (itemId == R.id.action_update_inventory) {
                    startActivity(new Intent(ViewOrdersActivity.this, UpdateInventoryActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure "View Orders" is highlighted when coming back from another activity
        bottomNavigationView.setSelectedItemId(R.id.action_view_orders);
    }
}
