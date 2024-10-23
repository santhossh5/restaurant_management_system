package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer_OrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Customer_OrderAdapter customerOrderAdapter;
    private List<Customer_Order> customerOrderList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_customer);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Status");

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerOrderList = new ArrayList<>();
        customerOrderAdapter = new Customer_OrderAdapter(customerOrderList);
        recyclerView.setAdapter(customerOrderAdapter);

        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Fetch orders for the current table number
        fetchOrders();

        // Initialize BottomNavigationView and set selected item
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_order_status);

        // Set navigation item listener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_menu) {
                    startActivity(new Intent(Customer_OrderStatusActivity.this, Customer_MenuActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_home) {
                    startActivity(new Intent(Customer_OrderStatusActivity.this, CustomerHomePage.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_order_status) {
                    // Already on this page
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();  // Remove Firestore listener when activity stops
        }
    }

    // Fetch orders for the current table number
    private void fetchOrders() {
        // Get the session ID from the OrderManager
        String sessionId = OrderManager.getInstance().getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            Toast.makeText(this, "Session ID is not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the first document from the 'santhossh' collection (assumed to be the restaurant document)
        db.collection("santhossh")
                .limit(1)  // Get only the first document
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document's ID (the restaurant document ID)
                        String documentId = task.getResult().getDocuments().get(0).getId();

                        // Now, fetch the specific sessionId document from the 'orders' collection
                        db.collection("santhossh")
                                .document(documentId)
                                .collection("orders")
                                .document(sessionId)  // The session ID document
                                .collection("foods")  // Sub-collection for food items
                                .get()
                                .addOnCompleteListener(foodTask -> {
                                    if (foodTask.isSuccessful()) {
                                        customerOrderList.clear(); // Clear previous orders
                                        for (QueryDocumentSnapshot foodSnapshot : foodTask.getResult()) {
                                            // Create a new Order object from the food data
                                            Customer_Order customerOrder = new Customer_Order();
                                            customerOrder.setSessionId(sessionId);
                                            customerOrder.setOrderStatus(foodSnapshot.getString("orderStatus"));
                                            customerOrder.setTableNumber(foodSnapshot.getString("tableNumber"));
                                            customerOrder.setTimestamp(foodSnapshot.getLong("timestamp"));

                                            // Create a map for food items (name to quantity)
                                            Map<String, Integer> foodItems = new HashMap<>();
                                            String foodName = foodSnapshot.getString("name");
                                            int quantity = foodSnapshot.getLong("quantity").intValue();
                                            foodItems.put(foodName, quantity);
                                            customerOrder.setItems(foodItems); // Set the food items in the order

                                            // Add the order to the orderList
                                            customerOrderList.add(customerOrder);
                                        }
                                        customerOrderAdapter.notifyDataSetChanged();  // Notify adapter about data changes
                                    } else {
                                        Toast.makeText(Customer_OrderStatusActivity.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to retrieve document ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve document ID. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }



}
