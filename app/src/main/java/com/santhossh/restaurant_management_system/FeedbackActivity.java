package com.santhossh.restaurant_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText editTextName, editTextFeedback;
    private RatingBar ratingBar;
    private Button buttonSubmitFeedback;
    private FirebaseFirestore db;
    String sessionId = OrderManager.getInstance().getSessionId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        Toolbar toolbar = findViewById(R.id.toolbar);
        editTextName = findViewById(R.id.edit_text_name);
        editTextFeedback = findViewById(R.id.edit_text_feedback);
        ratingBar = findViewById(R.id.rating_bar);
        buttonSubmitFeedback = findViewById(R.id.button_submit_feedback);

        // Set back button behavior
        toolbar.setNavigationOnClickListener(v -> finish());

        // Submit Feedback Button click
        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String name = editTextName.getText().toString().trim();
        String feedback = editTextFeedback.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (name.isEmpty() || feedback.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save feedback to Firestore
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("name", name);
        feedbackData.put("feedback", feedback);
        feedbackData.put("rating", rating);
        feedbackData.put("sessionId", sessionId);

        db.collection("feedback")
                .add(feedbackData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after submission
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Error submitting feedback", Toast.LENGTH_SHORT).show();
                });
    }
}
