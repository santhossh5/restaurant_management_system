package com.santhossh.restaurant_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageEmployeeShiftsActivity extends AppCompatActivity {

    private Spinner employeeSpinner, shiftTypeSpinner;
    private Button confirmButton;

    // Firebase Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference employeesRef = db.collection("employees");  // "employees" collection
    private CollectionReference shiftsRef = db.collection("shifts");  // "shifts" collection

    private ArrayList<String> employeeNames = new ArrayList<>();  // To hold employee names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_employee_shifts);

        // Initialize views
        employeeSpinner = findViewById(R.id.employeeSpinner);
        shiftTypeSpinner = findViewById(R.id.shiftTypeSpinner);
        confirmButton = findViewById(R.id.confirmButton);

        // Set up shift types (you can customize this list)
        ArrayAdapter<CharSequence> shiftAdapter = ArrayAdapter.createFromResource(this,
                R.array.shift_types, android.R.layout.simple_spinner_item);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shiftTypeSpinner.setAdapter(shiftAdapter);

        // Fetch employee data from Firestore
        fetchEmployeeNames();

        // Set onClickListener for the "Confirm" button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShiftData();
            }
        });
    }

    // Fetch employee names from Firebase and populate the spinner
    private void fetchEmployeeNames() {
        employeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String employeeName = document.getString("name");
                    employeeNames.add(employeeName);  // Add employee names to the list
                }

                // Set the spinner with employee names
                ArrayAdapter<String> employeeAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, employeeNames);
                employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeSpinner.setAdapter(employeeAdapter);
            } else {
                Toast.makeText(ManageEmployeeShiftsActivity.this, "Error fetching employees.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save the shift data to Firestore when "Confirm" is clicked
    private void saveShiftData() {
        String selectedEmployee = employeeSpinner.getSelectedItem().toString();
        String selectedShift = shiftTypeSpinner.getSelectedItem().toString();

        if (selectedEmployee.isEmpty() || selectedShift.isEmpty()) {
            Toast.makeText(this, "Please select both employee and shift type.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the employee ID (assume the employee name is unique)
        employeesRef.whereEqualTo("name", selectedEmployee)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String employeeId = task.getResult().getDocuments().get(0).getId();

                        // Create a map to hold shift data
                        Map<String, Object> shiftData = new HashMap<>();
                        shiftData.put("employeeId", employeeId);
                        shiftData.put("shiftType", selectedShift);
                        shiftData.put("date", System.currentTimeMillis());  // Use current timestamp as shift date

                        // Add shift data to Firestore
                        shiftsRef.add(shiftData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(ManageEmployeeShiftsActivity.this, "Shift saved successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ManageEmployeeShiftsActivity.this, "Error saving shift.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Employee not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
