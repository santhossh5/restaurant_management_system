package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class InventoryAdapter_laks extends BaseAdapter {

    private Context context;
    private ArrayList<InventoryItem_laks> inventoryList;
    private FirebaseFirestore db;

    public InventoryAdapter_laks(Context context, ArrayList<InventoryItem_laks> inventoryList) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
    }

    @Override
    public int getCount() {
        return inventoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_inventory_laks, parent, false);
        }

        InventoryItem_laks currentItem = (InventoryItem_laks) getItem(position);

        // Set product image
        ImageView productImage = convertView.findViewById(R.id.productImage);
        productImage.setImageResource(currentItem.getImageResId());

        // Set product name
        TextView productName = convertView.findViewById(R.id.productName);
        productName.setText(currentItem.getName());

        // Set stock status
        TextView stockStatus = convertView.findViewById(R.id.stockStatus);
        stockStatus.setText(currentItem.getStockStatus());

        // Set product quantity
        TextView productQuantity = convertView.findViewById(R.id.productQuantity);
        productQuantity.setText(String.valueOf(currentItem.getQuantity()));

        // Set up quantity controls
        Button decreaseQuantity = convertView.findViewById(R.id.decreaseQuantity);
        Button increaseQuantity = convertView.findViewById(R.id.increaseQuantity);

        // Decrease quantity button action
        decreaseQuantity.setOnClickListener(v -> {
            int quantity = currentItem.getQuantity();
            if (quantity > 0) {
                currentItem.setQuantity(quantity - 1);
                productQuantity.setText(String.valueOf(currentItem.getQuantity()));

                // Update stock status after decreasing quantity
                currentItem.setStockStatus(getStockStatus(currentItem.getQuantity()));
                stockStatus.setText(currentItem.getStockStatus());

                // Update quantity in Firestore
                updateQuantityInFirestore(currentItem.getName(), currentItem.getQuantity());
            }
        });

        // Increase quantity button action
        increaseQuantity.setOnClickListener(v -> {
            int quantity = currentItem.getQuantity();
            currentItem.setQuantity(quantity + 1);
            productQuantity.setText(String.valueOf(currentItem.getQuantity()));

            // Update stock status after increasing quantity
            currentItem.setStockStatus(getStockStatus(currentItem.getQuantity()));
            stockStatus.setText(currentItem.getStockStatus());

            // Update quantity in Firestore
            updateQuantityInFirestore(currentItem.getName(), currentItem.getQuantity());
        });

        return convertView;
    }

    // Helper method to determine stock status based on quantity
    private String getStockStatus(int quantity) {
        if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity < 4) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    // Function to update quantity in Firestore
    private void updateQuantityInFirestore(String itemName, int newQuantity) {
        db.collection("inventory")
                .document("06zyug9BpBxCz93NSmoB") // Your correct document ID
                .update("Stock." + itemName, newQuantity)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Log.d("Firestore", "Quantity updated successfully for " + itemName);
                })
                .addOnFailureListener(e -> {
                    // Update failed
                    Log.e("Firestore", "Error updating quantity for " + itemName, e);
                });
    }
}
