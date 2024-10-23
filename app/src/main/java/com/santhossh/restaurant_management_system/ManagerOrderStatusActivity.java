package com.santhossh.restaurant_management_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerOrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManagerOrderAdapter managerOrderAdapter;
    private List<Customer_Order> customerOrderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ImageView btnLogout; // Logout button

    private static final String TAG = "OrderStatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity_order_status);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerOrderList = new ArrayList<>();
        managerOrderAdapter = new ManagerOrderAdapter(this, customerOrderList);
        recyclerView.setAdapter(managerOrderAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Initialize Logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog(); // Call the confirmation dialog
            }
        });

        // Fetch orders from Firebase
        fetchOrders();
    }

    // Function to show the logout confirmation dialog
    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog
        new AlertDialog.Builder(ManagerOrderStatusActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, proceed to logout
                        mAuth.signOut(); // Sign out the user
                        Intent intent = new Intent(ManagerOrderStatusActivity.this, StaffLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                        startActivity(intent); // Redirect to LoginActivity1
                        finish(); // Close current activity
                    }
                })
                .setNegativeButton("No", null) // If user selects "No", just dismiss the dialog
                .show();
    }

    private void fetchOrders() {
        // Specify the document ID directly
        String documentId = "oQNunJYjNaAQomaZ3COt";
        Log.d(TAG, "Listening for real-time updates from document ID: " + documentId);

        // Listen for real-time updates to the 'orders' collection
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .addSnapshotListener((orderTask, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        Toast.makeText(ManagerOrderStatusActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (orderTask != null) {
                        Log.d(TAG, "Orders fetched successfully, count: " + orderTask.size());
                        customerOrderList.clear(); // Clear previous orders
                        for (QueryDocumentSnapshot orderSnapshot : orderTask) {
                            String sessionId = orderSnapshot.getId(); // Get the session ID
                            Log.d(TAG, "Fetching food items for session ID: " + sessionId);
                            fetchFoodItems(documentId, sessionId); // Fetch food items for each session ID
                        }
                    } else {
                        Log.e(TAG, "No orders found.");
                        Toast.makeText(ManagerOrderStatusActivity.this, "No orders found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchFoodItems(String documentId, String sessionId) {
        // Listen for real-time updates to the 'foods' sub-collection
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .document(sessionId)  // The session ID document
                .collection("foods")  // Sub-collection for food items
                .addSnapshotListener((foodTask, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen for food items failed.", e);
                        Toast.makeText(ManagerOrderStatusActivity.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (foodTask != null) {
                        // Create a new order object for the current session
                        Customer_Order customerOrder = new Customer_Order();
                        customerOrder.setSessionId(sessionId); // Set the session ID for the order

                        Map<String, Integer> foodItems = new HashMap<>();
                        for (QueryDocumentSnapshot foodSnapshot : foodTask) {
                            String foodName = foodSnapshot.getString("name");
                            int quantity = foodSnapshot.getLong("quantity").intValue();
                            String orderStatus = foodSnapshot.getString("orderStatus");
                            String tableNumber = foodSnapshot.getString("tableNumber");
                            long timestamp = foodSnapshot.getLong("timestamp");

                            // Set order details
                            customerOrder.setOrderStatus(orderStatus);
                            customerOrder.setTableNumber(tableNumber);
                            customerOrder.setTimestamp(timestamp);
                            foodItems.put(foodName, quantity);
                        }
                        customerOrder.setItems(foodItems); // Set the food items in the order

                        // Update existing order or add new
                        updateOrderInList(customerOrder);
                    } else {
                        Toast.makeText(ManagerOrderStatusActivity.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateOrderInList(Customer_Order newCustomerOrder) {
        boolean orderExists = false;

        for (int i = 0; i < customerOrderList.size(); i++) {
            Customer_Order existingCustomerOrder = customerOrderList.get(i);

            // Check if the sessionId matches
            if (existingCustomerOrder.getSessionId().equals(newCustomerOrder.getSessionId())) {
                // Update the existing order
                customerOrderList.set(i, newCustomerOrder);
                orderExists = true;
                break;
            }
        }

        // If the order does not exist, add it to the list
        if (!orderExists) {
            customerOrderList.add(newCustomerOrder);
        }

        // Sort the orders by timestamp in descending order (most recent first)
        customerOrderList.sort((order1, order2) -> Long.compare(order2.getTimestamp(), order1.getTimestamp()));

        // Notify adapter about changes
        managerOrderAdapter.notifyDataSetChanged();
    }
}