package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MainActivity_laks extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Feedback_laks> feedbackLaksList;
    private FeedbackAdapter_laks feedbackAdapterLaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_laks);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        ListView listView = findViewById(R.id.listView);
        feedbackLaksList = new ArrayList<>();

        // Set adapter for feedback items using FeedbackAdapter
        feedbackAdapterLaks = new FeedbackAdapter_laks(this, feedbackLaksList);
        listView.setAdapter(feedbackAdapterLaks);

        // Fetch feedback data from Firestore
        fetchFeedback();

        // Button to navigate to InventoryActivity
//        Button inventoryButton = findViewById(R.id.inventoryButton);
//        inventoryButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity_laks.this, InventoryActivity_laks.class);
//            startActivity(intent); // Navigate to the InventoryActivity
//        });
//
//        // Button to navigate to AssignWaitersActivity
//        Button assignWaitersButton = findViewById(R.id.assignWaitersButton);
//        assignWaitersButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity_laks.this, AssignWaitersActivity_Laks.class);
//            startActivity(intent); // Navigate to the AssignWaitersActivity
//        });
    }

    private void fetchFeedback() {
        db.collection("feedback")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String feedback = document.getString("feedback");
                            float rating = document.getDouble("rating").floatValue();

                            // Add feedback to the list
                            feedbackLaksList.add(new Feedback_laks(name, feedback, feedback, rating));
                        }
                        // Notify the adapter that the data has changed
                        feedbackAdapterLaks.notifyDataSetChanged();
                    } else {
                        // Handle errors (e.g., logging or showing a message)
                    }
                });
    }
}
