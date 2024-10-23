package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class UpdateInventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryList;
    private BottomNavigationView bottomNavigationView;
    private Button btnUpdateInventory;

    // Firebase Database reference
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_inventory);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Inventory");

        // Initialize RecyclerView for inventory list
        recyclerView = findViewById(R.id.recycler_view_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(inventoryAdapter);

        // Initialize Firebase Database reference
        db = FirebaseFirestore.getInstance();

        // Load inventory items from Firebase
        loadInventoryItems();

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_update_inventory);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    startActivity(new Intent(UpdateInventoryActivity.this, ChefHomePage.class));
                    return true;
                } else if (itemId == R.id.action_view_orders) {
                    startActivity(new Intent(UpdateInventoryActivity.this, orderStatusActivityChef.class));
                    return true;
                } else if (itemId == R.id.action_update_inventory) {
                    return true;  // Already on Update Inventory
                }
                return false;
            }
        });

        // Set up button to update inventory
        btnUpdateInventory = findViewById(R.id.btn_update_inventory);
        btnUpdateInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle inventory update
                updateInventoryInFirebase();
                Toast.makeText(UpdateInventoryActivity.this, "Inventory updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInventoryItems() {
        // Access Firestore collection and document
        db.collection("inventory").document("06zyug9BpBxCz93NSmoB").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> stockData = (Map<String, Object>) documentSnapshot.get("Stock");
                        if (stockData != null) {
                            inventoryList.clear();
                            for (Map.Entry<String, Object> entry : stockData.entrySet()) {
                                String itemName = entry.getKey();
                                Long quantity = (Long) entry.getValue(); // Firestore stores integers as Long

                                if (itemName != null && quantity != null) {
                                    inventoryList.add(new InventoryItem(itemName, quantity.intValue()));
                                }
                            }
                            inventoryAdapter.notifyDataSetChanged(); // Notify the adapter about data changes
                        }
                    } else {
                        Log.d("Firestore", "No such document exists!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading data", e);
                    Toast.makeText(UpdateInventoryActivity.this, "Failed to load data from Firestore.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateInventoryInFirebase() {
        // Update Firestore with the new quantities
        Map<String, Object> updatedInventory = new HashMap<>();
        for (InventoryItem item : inventoryList) {
            updatedInventory.put(item.getName(), item.getQuantity());
        }

        // Reference to the specific document in the 'inventory' collection
        db.collection("inventory").document("06zyug9BpBxCz93NSmoB")
                .update("Stock", updatedInventory)  // Update only the 'Stock' field with the new values
                .addOnSuccessListener(aVoid -> Log.d("InventoryUpdate", "Inventory successfully updated in Firestore."))
                .addOnFailureListener(e -> Log.e("InventoryUpdate", "Failed to update inventory in Firestore: " + e.getMessage()));
    }
}
