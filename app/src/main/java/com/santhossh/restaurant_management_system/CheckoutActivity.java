package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private TextView textTotalAmount;
    private RecyclerView recyclerOrderItems;
    private Button btnPlaceOrder;
    private List<BillItem> orderItemsList;
    private BillAdapter billAdapter;

    private FirebaseFirestore db;
    private String sessionId = OrderManager.getInstance().getSessionId(); // This should be passed or retrieved dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        textTotalAmount = findViewById(R.id.text_total_amount);
        recyclerOrderItems = findViewById(R.id.recycler_order_items);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        Button btnGiveFeedback = findViewById(R.id.btn_give_feedback);
        btnGiveFeedback.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        toolbar.setNavigationOnClickListener(v -> finish());
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        orderItemsList = new ArrayList<>();
        billAdapter = new BillAdapter(orderItemsList);
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderItems.setAdapter(billAdapter);

        // Fetch and display order items
        fetchOrderItems();

        // Place order button click listener
        btnPlaceOrder.setOnClickListener(view -> {
            Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
            // Handle checkout logic (e.g., update Firestore order status, etc.)
        });
    }

    private void fetchOrderItems() {
        // Get the restaurant document ID and session ID dynamically
        String restaurantDocId = "oQNunJYjNaAQomaZ3COt"; // Replace with the actual restaurant document ID
        CollectionReference orderCollection = db.collection("santhossh")
                .document(restaurantDocId)
                .collection("orders")
                .document(sessionId)
                .collection("foods");

        // Fetch the foods sub-collection for the session
        orderCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    Log.d("CheckoutActivity", "Number of documents: " + querySnapshot.size());
                    double totalAmount = 0.0;
                    orderItemsList.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String foodName = document.getString("name");
                        int quantity = document.getLong("quantity").intValue();
                        double price = document.getDouble("price").doubleValue();
                        double itemTotal = price * quantity;

                        // Add the item to the list
                        orderItemsList.add(new BillItem(foodName, quantity, price));

                        // Update the total amount
                        totalAmount += itemTotal;
                    }
                    Toast.makeText(CheckoutActivity.this, "Total Amount:"+totalAmount, Toast.LENGTH_SHORT).show();

                    // Update total amount in the UI
                    textTotalAmount.setText("Total: ₹" + String.format("%.2f", totalAmount));

                    // Notify the adapter about the data changes
                    billAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(CheckoutActivity.this, "Failed to fetch order items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
