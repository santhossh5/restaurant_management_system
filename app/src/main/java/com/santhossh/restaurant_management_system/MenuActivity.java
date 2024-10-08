package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private List<Food> foodList;
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
                    //Toast.makeText(MenuActivity.this, "Order Status Selected", Toast.LENGTH_SHORT).show();
                    //showOrderSummary();
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
        registration = db.collection("food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MenuActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        foodList.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot snapshot : value) {
                                Food food = snapshot.toObject(Food.class);
                                food.setId(snapshot.getId());
                                foodList.add(food);
                            }
                        }
                        foodAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Show Order Summary when the Floating Action Button is clicked
    protected void showOrderSummary() {
        Map<String, Integer> orderMap = foodAdapter.getOrderMap();
        if (orderMap.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a string to display in the dialog
        StringBuilder orderSummary = new StringBuilder();
        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String foodId = entry.getKey();
            int quantity = entry.getValue();

            // Find the food item by foodId and append to the summary
            for (Food food : foodList) {
                if (food.getId().equals(foodId)) {
                    orderSummary.append(food.getName()).append(": ").append(quantity).append("\n");
                    break;
                }
            }
        }

        // Show the summary in an AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Order Summary")
                .setMessage(orderSummary.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    // Call the method to save the order to Firestore
                    saveOrderToFirestore(orderMap);

                })
                .setNegativeButton("Cancel", null) // Close dialog on cancel
                .show();
    }

    private void saveOrderToFirestore(Map<String, Integer> orderMap) {
        // Create a new order object
        Order order = new Order();
        order.setItems(orderMap);  // Set the items map
        order.setTimestamp(System.currentTimeMillis());  // Add a timestamp for the order
        order.setOrderStatus("Pending");

        // Add table number to the order
        String tableNumber = TableManager.getInstance().getTableNumber();
        if (tableNumber != null && !tableNumber.isEmpty()) {
            order.setTableNumber(tableNumber);  // Assuming you add tableNumber field to the Order class
        } else {
            Toast.makeText(this, "Table number is not set!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")  // Collection name where orders will be stored
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
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
