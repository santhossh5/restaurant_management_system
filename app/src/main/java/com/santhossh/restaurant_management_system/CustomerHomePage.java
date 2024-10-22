package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomerHomePage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);  // Link to the XML layout

        // Initialize and set the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView table_no = findViewById(R.id.TableNumber);
        table_no.setText("Table no: "+TableManager.getInstance().getTableNumber());

        // Initialize the Button
        btnCheckout = findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHomePage.this, CheckoutActivity.class);
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

                if (itemId == R.id.action_menu) {
                    // Navigate to the MenuActivity
                    startActivity(new Intent(CustomerHomePage.this, MenuActivity.class));
                    return true;
                } else if (itemId == R.id.action_home) {
                    // Stay on the same activity (Home)
                    return true;
                } else if (itemId == R.id.action_order_status) {
                    // Navigate to the OrderStatusActivity
                    startActivity(new Intent(CustomerHomePage.this, OrderStatusActivity.class));
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
        // Ensure "Home" is highlighted when coming back from another activity
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

}
