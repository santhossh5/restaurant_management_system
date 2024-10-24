package com.santhossh.restaurant_management_system;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class FeedbackActivity_laks extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Feedback_laks> feedbackLaksList;
    private FeedbackAdapter_laks adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_laks);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        ListView listView = findViewById(R.id.listView);
        feedbackLaksList = new ArrayList<>();

        // Initialize adapter
        adapter = new FeedbackAdapter_laks(this, feedbackLaksList);
        listView.setAdapter(adapter);

        // Fetch feedback from Firestore
        fetchFeedback();
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
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                });
    }
}
