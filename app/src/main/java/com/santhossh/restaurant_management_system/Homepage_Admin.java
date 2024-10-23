package com.santhossh.restaurant_management_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Homepage_Admin extends AppCompatActivity {

    // Define views
    private TextView welcomeText, userName, employee_count;
    private CardView manageEmployeeCard, reportsCard, deliveryCard, manageShiftsCard;  // Added manageShiftsCard

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_homepage);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        userName = findViewById(R.id.userName);

        // Card views
        employee_count = findViewById(R.id.employeeCountText);
        manageEmployeeCard = findViewById(R.id.manageEmployeeCard);
        reportsCard = findViewById(R.id.reportsCard);
        deliveryCard = findViewById(R.id.deliveryCard);  // Add delivery card
        manageShiftsCard = findViewById(R.id.manageShiftsCard);  // New card for managing shifts

        // Set default username (You can update this dynamically)
        userName.setText("Vinoth Kumar C");

        getEmployeeCount();

        // Set click listeners for card views
        manageEmployeeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to "View Employee" page
                Intent intent = new Intent(Homepage_Admin.this, ViewEmployeeActivity.class);
                startActivity(intent);
            }
        });

        reportsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to "Reports" page
                Intent intent = new Intent(Homepage_Admin.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for delivery card to show alert dialog
        deliveryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show alert dialog
                showNoDeliveryAlert();
            }
        });

        // Set click listener for managing employee shifts
        manageShiftsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to "Manage Employee Shifts" page
                Intent intent = new Intent(Homepage_Admin.this, ManageEmployeeShiftsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to show alert dialog when Delivery is clicked
    private void showNoDeliveryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Delivery Orders")
                .setMessage("Currently, no delivery orders are being accepted.")
                .setCancelable(false)  // Prevent the dialog from being dismissed when touching outside
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();  // Close the dialog when user clicks OK
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    // Method to retrieve employee count from the Firebase database
    private void getEmployeeCount() {
        // Reference to the employees collection in Firebase
        CollectionReference employeesRef = FirebaseFirestore.getInstance().collection("employees");

        // Get all documents in the employees collection
        employeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the total count of employees
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    int employeeCount = querySnapshot.size();
                    Log.d("Homepage", "count"+employeeCount);
                    // Update the employee count TextView
                    employee_count.setText(""+employeeCount);
                }
            } else {
                Toast.makeText(Homepage_Admin.this, "Error fetching employee count", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
