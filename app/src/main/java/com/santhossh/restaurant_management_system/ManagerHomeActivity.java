package com.santhossh.restaurant_management_system;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ManagerHomeActivity extends AppCompatActivity {
    private Button btnMenuManagement, btnOrderStatus, btnStaffManagement, btnInventoryManagement, btnFeedbackManagement;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ImageView btnLogout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity_home);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        btnMenuManagement = findViewById(R.id.btnMenuManagement);
        btnOrderStatus = findViewById(R.id.btnOrderStatus);
        btnStaffManagement = findViewById(R.id.btnStaffManagement);
        btnInventoryManagement = findViewById(R.id.btnInventoryManagement);
        btnFeedbackManagement = findViewById(R.id.btnFeedbackManagement);
        btnLogout = findViewById(R.id.btnLogout); // Logout button

        btnMenuManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Menu Management Activity
                Intent intent = new Intent(ManagerHomeActivity.this, ManagerMenuActivity.class);
                startActivity(intent);
            }
        });

        btnOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Order Status Activity
                Intent intent = new Intent(ManagerHomeActivity.this, ManagerOrderStatusActivity.class);
                startActivity(intent);
            }
        });

        btnStaffManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Staff Management Activity (Currently null)
                Intent intent = new Intent(ManagerHomeActivity.this, AssignWaitersActivity_Laks.class);
                startActivity(intent);
            }
        });

        btnInventoryManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Inventory Management Activity (Currently null)
                Intent intent = new Intent(ManagerHomeActivity.this, InventoryActivity_laks.class);
                startActivity(intent);
            }
        });

        btnFeedbackManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, MainActivity_laks.class);
                startActivity(intent);
            }
        });

        // Implement Logout functionality with confirmation dialog
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog(); // Call the confirmation dialog
            }
        });
    }

    // Function to show the logout confirmation dialog
    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog
        new AlertDialog.Builder(ManagerHomeActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, proceed to logout
                        mAuth.signOut(); // Sign out the user
                        Intent intent = new Intent(ManagerHomeActivity.this, StaffLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                        startActivity(intent); // Redirect to LoginActivity1
                        finish(); // Close current activity
                    }
                })
                .setNegativeButton("No", null) // If user selects "No", just dismiss the dialog
                .show();
    }
}
