package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<OrderItem> orderItemList; // List to hold ordered items
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private TextView totalAmountTextView;
    private BottomNavigationView bottomNavigationView;
    private double totalAmount = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");

        recyclerView = findViewById(R.id.recycler_view_checkout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalAmountTextView = findViewById(R.id.text_total_amount);
        orderItemList = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(orderItemList);
        recyclerView.setAdapter(checkoutAdapter);

        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Fetch ordered items from Firestore
        fetchOrderedItems();

        // Initialize BottomNavigationView and set the selected item
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_order_status);

        // Set navigation item listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_menu) {
                startActivity(new Intent(CheckoutActivity.this, MenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.action_home) {
                startActivity(new Intent(CheckoutActivity.this, CustomerHomePage.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.action_order_status) {
                startActivity(new Intent(CheckoutActivity.this, OrderStatusActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    // Fetch ordered items from Firestore based on table number
    private void fetchOrderedItems() {
        String tableNumber = TableManager.getInstance().getTableNumber();
        if (tableNumber == null || tableNumber.isEmpty()) {
            Toast.makeText(this, "Table number is not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        registration = db.collection("orders")
                .whereEqualTo("tableNumber", tableNumber)  // Fetch orders for the specific table number
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(CheckoutActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        orderItemList.clear();
                        totalAmount = 0.00;
                        if (value != null) {
                            for (QueryDocumentSnapshot snapshot : value) {
                                Order order = snapshot.toObject(Order.class);
                                Map<String, Integer> items = order.getItems();

                                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                                    String foodId = entry.getKey();
                                    int quantity = entry.getValue();

                                    // Fetch the food item and its price
                                    fetchFoodItem(foodId, quantity);
                                }
                            }
                        }
                    }
                });
    }

    // Fetch food details for each item in the order
    private void fetchFoodItem(String foodId, int quantity) {
        db.collection("food")
                .document(foodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Food food = documentSnapshot.toObject(Food.class);
                    if (food != null) {
                        double itemTotal = food.getPrice() * quantity;
                        totalAmount += itemTotal;

                        // Add ordered item to the list
                        orderItemList.add(new OrderItem(food.getName(), quantity, food.getPrice(), itemTotal));
                        checkoutAdapter.notifyDataSetChanged();

                        // Update total amount text view
                        totalAmountTextView.setText("Total Amount: $" + String.format("%.2f", totalAmount));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CheckoutActivity.this, "Failed to load food item.", Toast.LENGTH_SHORT).show();
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
