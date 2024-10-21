package com.santhossh.restaurant_management_system.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santhossh.restaurant_management_system.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    public List<Food> foodList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private BottomNavigationView bottomNavigationView;
    private Button confirmOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");

        recyclerView = findViewById(R.id.recycler_view_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(foodAdapter);

        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Fetch food items from Firebase
        fetchFoodItems();

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_menu);

        // Set a listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Navigate to the respective activity based on the selected item
                if (itemId == R.id.action_menu) {
                    return true; // Already in MenuActivity
                } else if (itemId == R.id.action_home) {
                    startActivity(new Intent(MenuActivity.this, CustomerHomePage.class));
                    return true;
                } else if (itemId == R.id.action_order_status) {
                    startActivity(new Intent(MenuActivity.this, OrderStatusActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Set up the Confirm Order button
        confirmOrderButton = findViewById(R.id.btn_confirm_order);
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderSummary();  // Show order summary when button is clicked
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Highlight the "Menu" button when returning to this activity
        bottomNavigationView.setSelectedItemId(R.id.action_menu);
    }

    // Fetch food items from Firestore
    private void fetchFoodItems() {
        db.collection("santhossh")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            // Get the 'menu' sub-collection from each document
                            db.collection("santhossh")
                                    .document(documentSnapshot.getId())
                                    .collection("menu")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(MenuActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if (value != null) {
                                                for (QueryDocumentSnapshot snapshot : value) {
                                                    Food food = snapshot.toObject(Food.class);
                                                    food.setId(snapshot.getId());
                                                    // Check if the food item is in stock
                                                    if (food.isInStock()) {
                                                        foodList.add(food);
                                                        //Log.d("MenuActivity", "Food added: " + food.getName() + ", Price: " + food.getPrice());
                                                    } else {
                                                        Log.d("MenuActivity", "Food not in stock: " + food.getName());
                                                    }
                                                }
                                            }

                                            if (foodList.isEmpty()) {
                                                Toast.makeText(MenuActivity.this, "No items available.", Toast.LENGTH_SHORT).show();
                                            }

                                            foodAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "Failed to load restaurants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Show Order Summary when the button is clicked
    protected void showOrderSummary() {
        Map<String, Integer> orderMap = foodAdapter.getOrderMap();
        if (orderMap.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a string to display in the dialog
        StringBuilder orderSummary = new StringBuilder();
        double totalPrice = 0.0; // Variable to hold total price

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String foodId = entry.getKey();
            int quantity = entry.getValue();

            // Find the food item by foodId and append to the summary
            for (Food food : foodList) {
                orderSummary.append(foodId).append(": ").append(quantity).append("\n");
                totalPrice += food.getPrice() * quantity; // Calculate total price
                break;
            }
        }

        // Append total price to the summary
        orderSummary.append("Total: ₹").append(totalPrice).append("\n");

        // Show the summary in an AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Order Summary")
                .setMessage(orderSummary.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    saveOrderToFirestore(orderMap);

                    // Reset the order map to clear counts after confirmation
                    foodAdapter.resetOrderMap(); // Call the method to reset the order map
                    foodAdapter.notifyDataSetChanged(); // Notify the adapter of the change
                })
                .setNegativeButton("Cancel", null) // Close dialog on cancel
                .show();
    }

    private void saveOrderToFirestore(Map<String, Integer> orderMap) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the session ID from the OrderManager
        String sessionId = OrderManager.getInstance().getSessionId();
        String tableNumber = OrderManager.getInstance().getCurrentOrder().getTableNumber();

        // Check if session ID is valid
        if (sessionId == null || sessionId.isEmpty()) {
            Toast.makeText(this, "Session ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new order object to hold the session-level data
        Order order = new Order();
        order.setTimestamp(System.currentTimeMillis()); // Set the current timestamp
        order.setOrderStatus("Pending"); // Default order status
        order.setSessionId(sessionId);
        order.setTableNumber(tableNumber); // Set table number

        // Once the order is successfully created, save the food items under the 'foods' sub-collection
        saveFoodItemsToFirestore(sessionId, orderMap, tableNumber);
    }

    private void saveFoodItemsToFirestore(String sessionId, Map<String, Integer> orderMap, String tableNumber) {
        // Get the current timestamp for the order
        long timestamp = System.currentTimeMillis();
        String orderStatus = "Pending";  // Default order status

        // Loop through the orderMap to save each food item
        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String foodId = entry.getKey();  // This is the food ID
            int quantity = entry.getValue();  // This is the quantity ordered
            double price = 0;  // This is the price of the food
            for(Food food : foodList)
            {
                if(food.getName().equals(foodId))
                {
                    price = food.getPrice();
                }
            }

            Map<String, Object> t_data = new HashMap<>();
            t_data.put("session_id",sessionId);
            db.collection("santhossh").document("oQNunJYjNaAQomaZ3COt").collection("orders").document(sessionId).set(t_data);

            // Create a map to store food data (name, quantity, table number, order status, and timestamp)
            Map<String, Object> foodData = new HashMap<>();
            foodData.put("name", foodId);
            foodData.put("quantity", quantity);
            foodData.put("tableNumber", tableNumber);
            foodData.put("orderStatus", orderStatus);
            foodData.put("timestamp", timestamp);
            foodData.put("price", price);
            final String foodname = foodId;
            // Use the food name as the document ID to avoid duplication, or use an auto-generated ID
            String foodDocId = foodId + "_" + System.currentTimeMillis(); // Optional: Unique document ID
            // If you want to use Firestore's auto-generated ID instead, you can call .add() instead of .document(foodDocId)

            // Add each food item to the 'foods' sub-collection under the session document
            db.collection("santhossh")
                        .document("oQNunJYjNaAQomaZ3COt")  // The restaurant document ID
                        .collection("orders")
                        .document(sessionId)  // The session ID as the document ID
                        .collection("foods")  // Sub-collection for food items
                        .document(foodDocId)  // Use the generated ID as the document ID
                        .set(foodData)  // Save the food data with the specified document ID
                        .addOnSuccessListener(aVoid -> {
                            Log.d("MenuActivity s", "Food item saved: " + foodname);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save food item: " + foodname, Toast.LENGTH_SHORT).show();
                        });
        }

        // Notify user that the order is complete
        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();  // Remove Firestore listener when activity stops
        }
    }
}
