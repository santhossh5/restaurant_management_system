package com.santhossh.restaurant_management_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class orderStatusActivityChef extends AppCompatActivity {

    private RecyclerView recyclerView;
    private orderAdapterChef orderAdapter1;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ImageView btnLogout; // Logout button

    private static final String TAG = "OrderStatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter1 = new orderAdapterChef(this, orderList);
        recyclerView.setAdapter(orderAdapter1);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Fetch orders from Firebase
        fetchOrders();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight Home as the default selection
        bottomNavigationView.setSelectedItemId(R.id.action_view_orders);

        // Set a listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Get the item ID

                if (itemId == R.id.action_view_orders) {
                    return true;
                } else if (itemId == R.id.action_home) {
                    startActivity(new Intent(orderStatusActivityChef.this, ChefHomePage.class));
                    return true;
                } else if (itemId == R.id.action_update_inventory) {
                    startActivity(new Intent(orderStatusActivityChef.this, UpdateInventoryActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
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
                        Log.d(TAG, "Orders fetched successfully, count: " + orderTask.getResult().size());
                        orderList.clear(); // Clear previous orders
                        for (QueryDocumentSnapshot orderSnapshot : orderTask.getResult()) {
                            String sessionId = orderSnapshot.getId(); // Get the session ID
                            fetchFoodItems(documentId, sessionId); // Fetch food items for each session ID
                        }
                    } else {
                        Log.e(TAG, "Failed to load orders.", orderTask.getException());
                        Toast.makeText(orderStatusActivityChef.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching orders: " + e.getMessage(), e);
                    Toast.makeText(orderStatusActivityChef.this, "Error fetching orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFoodItems(String documentId, String sessionId) {
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .document(sessionId)
                .collection("foods")
                .get()
                .addOnCompleteListener(foodTask -> {
                    if (foodTask.isSuccessful() && foodTask.getResult() != null) {
                        Order order = new Order();
                        order.setSessionId(sessionId);

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
                        order.setItems(foodItems);

                        // Add the order to the orderList
                        orderList.add(order);
                        orderAdapter1.notifyDataSetChanged();
                    } else {
                        Toast.makeText(orderStatusActivityChef.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(orderStatusActivityChef.this, "Error fetching food items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
