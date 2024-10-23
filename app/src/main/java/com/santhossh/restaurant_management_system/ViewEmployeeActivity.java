package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewEmployeeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout employeeLayout;
    private ImageButton addEmployeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_employee);  // Make sure this points to your layout file

        db = FirebaseFirestore.getInstance();
        employeeLayout = findViewById(R.id.employeeLayout);  // Assuming you have a parent LinearLayout in your XML
        addEmployeeButton = findViewById(R.id.add_employee_button); // "+" button in toolbar

        // Set up click listener for "+" button
        addEmployeeButton.setOnClickListener(v -> {
            // Navigate to Add Employee page
            Intent intent = new Intent(ViewEmployeeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        fetchEmployeesFromDatabase();
    }

    private void fetchEmployeesFromDatabase() {
        db.collection("employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null) {
                            for (QueryDocumentSnapshot document : documents) {
                                String name = document.getString("name");
                                String phone = document.getString("phone");
                                String role = document.getString("role");
                                String age = document.getString("age");  // Assuming you have an age field

                                // Dynamically add employees to the UI
                                addEmployeeToUI(name, phone, role, age);
                            }
                        }
                    } else {
                        Toast.makeText(ViewEmployeeActivity.this, "Error fetching employees", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEmployeeToUI(String name, String phone, String role, String age) {
        // Inflate your employee layout
        LinearLayout employeeView = (LinearLayout) getLayoutInflater().inflate(R.layout.employee_item, null);

        // Set the placeholder text with the fetched data
        TextView nameText = employeeView.findViewById(R.id.employee_name);
        TextView phoneText = employeeView.findViewById(R.id.employee_phone);
        TextView roleText = employeeView.findViewById(R.id.employee_role);

        // Set the dynamic data
        nameText.setText(name != null ? name : "Name not available");
        phoneText.setText(phone != null ? phone : "Phone not available");
        roleText.setText(role != null ? role : "Role not available");

        // Add the employee view to the parent layout
        employeeLayout.addView(employeeView);
    }
}
