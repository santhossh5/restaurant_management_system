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

public class OrderStatusActivity1 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter1 orderAdapter1;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ImageView btnLogout; // Logout button

    private static final String TAG = "OrderStatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter1 = new OrderAdapter1(this, orderList);
        recyclerView.setAdapter(orderAdapter1);

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
        new AlertDialog.Builder(OrderStatusActivity1.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, proceed to logout
                        mAuth.signOut(); // Sign out the user
                        Intent intent = new Intent(OrderStatusActivity1.this, LoginActivity1.class);
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
        Log.d(TAG, "Fetching orders from document ID: " + documentId);

        // Fetch all session IDs from the 'orders' collection
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .get()
                .addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful() && orderTask.getResult() != null) {
                        Log.d(TAG, "Order snapshots: " + orderTask.getResult().getDocuments().toString());
                        Log.d(TAG, "Orders fetched successfully, count: " + orderTask.getResult().size());
                        orderList.clear(); // Clear previous orders
                        for (QueryDocumentSnapshot orderSnapshot : orderTask.getResult()) {
                            String sessionId = orderSnapshot.getId(); // Get the session ID
                            Log.d(TAG, "Fetching food items for session ID: " + sessionId);
                            fetchFoodItems(documentId, sessionId); // Fetch food items for each session ID
                        }
                    } else {
                        Log.e(TAG, "Failed to load orders.", orderTask.getException());
                        Toast.makeText(OrderStatusActivity1.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching orders: " + e.getMessage(), e);
                    Toast.makeText(OrderStatusActivity1.this, "Error fetching orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFoodItems(String documentId, String sessionId) {
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .document(sessionId)  // The session ID document
                .collection("foods")  // Sub-collection for food items
                .get()
                .addOnCompleteListener(foodTask -> {
                    if (foodTask.isSuccessful() && foodTask.getResult() != null) {
                        Order order = new Order();
                        order.setSessionId(sessionId); // Set the session ID for the order

                        Map<String, Integer> foodItems = new HashMap<>();
                        for (QueryDocumentSnapshot foodSnapshot : foodTask.getResult()) {
                            String foodName = foodSnapshot.getString("name");
                            int quantity = foodSnapshot.getLong("quantity").intValue();
                            String orderStatus = foodSnapshot.getString("orderStatus");
                            String tableNumber = foodSnapshot.getString("tableNumber");
                            long timestamp = foodSnapshot.getLong("timestamp");

                            order.setOrderStatus(orderStatus);
                            order.setTableNumber(tableNumber);
                            order.setTimestamp(timestamp);
                            foodItems.put(foodName, quantity);
                        }
                        order.setItems(foodItems); // Set the food items in the order

                        // Add the order to the orderList
                        orderList.add(order);
                        orderAdapter1.notifyDataSetChanged();  // Notify adapter about data changes
                    } else {
                        Toast.makeText(OrderStatusActivity1.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrderStatusActivity1.this, "Error fetching food items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
