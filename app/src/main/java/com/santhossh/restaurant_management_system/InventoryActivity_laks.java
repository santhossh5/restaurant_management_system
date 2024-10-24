package com.santhossh.restaurant_management_system;

import android.os.Bundle;
import android.widget.ListView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;

public class InventoryActivity_laks extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<InventoryItem_laks> inventoryList;
    private InventoryAdapter_laks inventoryAdapterLaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_laks);  // Create activity_inventory.xml if not already

        // Create ListView for inventory
        ListView inventoryListView = findViewById(R.id.inventoryListView);

        // Initialize inventory list
        inventoryList = new ArrayList<>();
        inventoryAdapterLaks = new InventoryAdapter_laks(this, inventoryList);
        inventoryListView.setAdapter(inventoryAdapterLaks);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch inventory data from Firestore
        fetchInventoryData();
    }

    private void fetchInventoryData() {
        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null) {
                            for (DocumentSnapshot document : documents) {
                                Map<String, Object> stock = (Map<String, Object>) document.get("Stock");

                                if (stock != null) {
                                    for (Map.Entry<String, Object> entry : stock.entrySet()) {
                                        String itemName = entry.getKey();
                                        int itemCount = ((Long) entry.getValue()).intValue();

                                        // Determine stock status based on item count
                                        String stockStatus = getStockStatus(itemCount);

                                        // Add to the inventory list
                                        inventoryList.add(new InventoryItem_laks(itemName, stockStatus, itemCount, R.drawable.food)); // Replace 'default_image' with actual drawable resources for different items
                                    }
                                }
                            }
                            inventoryAdapterLaks.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private String getStockStatus(int count) {
        if (count == 0) {
            return "Out of Stock";
        } else if (count < 4) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
}
