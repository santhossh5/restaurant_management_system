package com.santhossh.restaurant_management_system;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity1 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private List<FoodItem> foodItemList;
    private Button btnConfirmChanges;
    private FirebaseFirestore db;
    private ImageView btnLogout; // Logout button

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        // Initialize Firestore, RecyclerView, and Logout button
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemList = new ArrayList<>();
        menuAdapter = new MenuAdapter(foodItemList, this::showConfirmChangesButton);
        recyclerView.setAdapter(menuAdapter);

        // Confirm changes button
        btnConfirmChanges = findViewById(R.id.btnConfirmChanges);

        // Logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Show confirmation dialog for logout
            showLogoutConfirmationDialog();
        });

        // Initially hide the Confirm Changes button
        btnConfirmChanges.setVisibility(View.GONE);

        // Fetch food items from Firebase
        fetchFoodItems();

        // Handle confirm button click
        btnConfirmChanges.setOnClickListener(v -> {
            Map<String, Boolean> selectedItems = menuAdapter.getSelectedItems();
            Map<String, Boolean> updates = new HashMap<>();

            // Create a map of updates for each item
            for (Map.Entry<String, Boolean> entry : selectedItems.entrySet()) {
                String itemId = entry.getKey();
                Boolean newStockStatus = entry.getValue();
                updates.put(itemId, newStockStatus);
            }

            // Update Firestore for each selected item
            for (Map.Entry<String, Boolean> entry : updates.entrySet()) {
                String itemId = entry.getKey();
                Boolean newStockStatus = entry.getValue();

                db.collection("food").document(itemId)
                        .update("inStock", newStockStatus)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
            }
            Toast.makeText(this, "Changes confirmed", Toast.LENGTH_SHORT).show();

            // Hide the button after confirming changes
            btnConfirmChanges.setVisibility(View.GONE);
        });
    }

    // Fetch menu items from Firestore
    private void fetchFoodItems() {
        db.collection("food")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            FoodItem item = document.toObject(FoodItem.class);
                            if (item != null) {
                                item.setId(document.getId());
                                foodItemList.add(item);
                            }
                        }
                        menuAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Show the Confirm Changes button when a checkbox is clicked
    private void showConfirmChangesButton() {
        btnConfirmChanges.setVisibility(View.VISIBLE);
    }

    // Function to show the logout confirmation dialog
    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog
        new AlertDialog.Builder(MenuActivity1.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, proceed to logout
                        FirebaseAuth.getInstance().signOut(); // Sign out the user
                        Intent intent = new Intent(MenuActivity1.this, LoginActivity1.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                        startActivity(intent); // Redirect to LoginActivity1
                        finish(); // Close current activity
                    }
                })
                .setNegativeButton("No", null) // If user selects "No", just dismiss the dialog
                .show();
    }
}
