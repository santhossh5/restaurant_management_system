package com.santhossh.restaurant_management_system;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class WaiterHomePageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapterWaiter orderAdapter;
    private List<Order_Waiter> orderWaiterList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Firebase Authentication instance

    private static final String TAG = "WaiterHomePageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_waiter); // Assuming your layout file is 'activity_main_waiter.xml'

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.orderRecyclerView); // RecyclerView ID from 'activity_main_waiter.xml'
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderWaiterList = new ArrayList<>();
        orderAdapter = new OrderAdapterWaiter(this, orderWaiterList); // Assuming 'OrderAdapterWaiter' is your waiter-specific adapter
        recyclerView.setAdapter(orderAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Fetch orders for the waiter
        fetchOrders();
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
                        orderWaiterList.clear(); // Clear previous orders
                        for (QueryDocumentSnapshot orderSnapshot : orderTask.getResult()) {
                            String sessionId = orderSnapshot.getId(); // Get the session ID
                            fetchFoodItems(documentId, sessionId); // Fetch food items for each session ID
                        }
                    } else {
                        Log.e(TAG, "Failed to load orders.", orderTask.getException());
                        Toast.makeText(WaiterHomePageActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching orders: " + e.getMessage(), e);
                    Toast.makeText(WaiterHomePageActivity.this, "Error fetching orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFoodItems(String documentId, String sessionId) {
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .document(sessionId)
                .collection("foods")
                .whereEqualTo("orderStatus", "Ready") // Filter by status "Ready"
                .get()
                .addOnCompleteListener(foodTask -> {
                    if (foodTask.isSuccessful() && foodTask.getResult() != null) {
                        Order_Waiter orderWaiter = new Order_Waiter();
                        orderWaiter.setSessionId(sessionId);

                        Map<String, Integer> foodItems = new HashMap<>();
                        for (QueryDocumentSnapshot foodSnapshot : foodTask.getResult()) {
                            Log.d("WaiterHomePageActivity", foodSnapshot.getString("orderStatus") + sessionId);
                            String foodName = foodSnapshot.getString("name");
                            int quantity = foodSnapshot.getLong("quantity").intValue();
                            String orderStatus = foodSnapshot.getString("orderStatus");
                            String tableNumber = foodSnapshot.getString("tableNumber");
                            long timestamp = foodSnapshot.getLong("timestamp");

                            orderWaiter.setOrderStatus(orderStatus);
                            orderWaiter.setTableNumber(tableNumber);
                            orderWaiter.setTimestamp(timestamp);
                            foodItems.put(foodName, quantity);
                        }
                        orderWaiter.setItems(foodItems);

                        // Add the order to the orderList if it has a status of "Ready"
                        if ("Ready".equals(orderWaiter.getOrderStatus())) {
                            orderWaiterList.add(orderWaiter);
                            orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(WaiterHomePageActivity.this, "Failed to load food items.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WaiterHomePageActivity.this, "Error fetching food items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
