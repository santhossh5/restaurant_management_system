package com.santhossh.restaurant_management_system;

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
                                                        Log.d("MenuActivity", "Food added: " + food.getName() + ", Price: " + food.getPrice());
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
        // Create a new order object
        Order order = new Order();
        Map<String, Integer> orderMapCopy = new HashMap<>(orderMap);
        order.setItems(orderMapCopy);  // Set the items map
        order.setTimestamp(System.currentTimeMillis());  // Add a timestamp for the order
        order.setOrderStatus("Pending");

        // Add table number to the order
        String tableNumber = TableManager.getInstance().getTableNumber();
        if (tableNumber != null && !tableNumber.isEmpty()) {
            order.setTableNumber(tableNumber);
        } else {
            Toast.makeText(this, "Table number is not set!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the first document from the 'santhossh' collection and get its ID
        db.collection("santhossh")
                .limit(1)  // Get only the first document
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document's ID
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        // Save the order to the 'orders' sub-collection of the first document
                        db.collection("santhossh")
                                .document(documentId)
                                .collection("orders")
                                .add(order)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this, "Failed to retrieve document ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve document ID. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();  // Remove Firestore listener when activity stops
        }
    }
}
