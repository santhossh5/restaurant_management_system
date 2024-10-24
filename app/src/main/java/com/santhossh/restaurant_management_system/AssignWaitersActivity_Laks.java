package com.santhossh.restaurant_management_system;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignWaitersActivity_Laks extends AppCompatActivity {

    private ListView tableListView;
    private FirebaseFirestore db;
    private ArrayList<String> waiterList;
    private TableAdapter_laks tableAdapterLaks;
    private static final String TAG = "AssignWaitersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_waiters_laks);

        tableListView = findViewById(R.id.listViewTables);
        db = FirebaseFirestore.getInstance();
        waiterList = new ArrayList<>();

        // Fetch waiters and assignments from Firestore
        db.collection("waiter").document("vJxcaSKLuPnBQtVcbWTq").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Object> data = task.getResult().getData();
                if (data != null) {
                    Map<String, ArrayList<Long>> waiterAssignment = (Map<String, ArrayList<Long>>) data.get("waiter_assignment");

                    if (waiterAssignment != null) {
                        for (String waiter : waiterAssignment.keySet()) {
                            waiterList.add(waiter);  // Add waiter to list
                        }

                        // Set up table list with waiter assignments
                        setupTableListView(waiterAssignment);
                    }
                }
            } else {
                Log.e(TAG, "Error fetching waiters or assignments from Firestore: ", task.getException());
            }
        });

        // Save assignments when button is clicked
        Button saveButton = findViewById(R.id.saveAssignmentsButton);
        saveButton.setOnClickListener(v -> saveAssignmentsToFirestore());
    }

    private void setupTableListView(Map<String, ArrayList<Long>> waiterAssignments) {
        ArrayList<String> tableList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            tableList.add("Table " + i);
        }

        // Initialize adapter and pass waiter assignments
        tableAdapterLaks = new TableAdapter_laks(this, tableList, waiterList, waiterAssignments);
        tableListView.setAdapter(tableAdapterLaks);
    }

    private void saveAssignmentsToFirestore() {
        // Get waiter assignments from the adapter
        Map<String, ArrayList<Integer>> waiterAssignments = tableAdapterLaks.getWaiterAssignments();

        // Prepare the data to be updated in Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("waiter_assignment", waiterAssignments);

        // Update Firestore with the new waiter assignments
        db.collection("waiter")
                .document("vJxcaSKLuPnBQtVcbWTq")
                .update(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Waiter assignments successfully updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating waiter assignments", e));
    }
}
