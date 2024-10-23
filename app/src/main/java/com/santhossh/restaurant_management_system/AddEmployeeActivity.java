package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        Log.d("AddEmployeeActivity", "Firestore initialized");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("PasswordHashingError", "Error hashing password", e);
            return password;  // Fallback to plain password
        }
    }

    public void addEmployee(String name, String phone, String email, String address, String pan, String role, String password, Context context) {
        String hashedPassword = hashPassword(password);

        // Log the hashed password and employee data for debugging
        Log.d("AddEmployeeActivity", "Hashed Password: " + hashedPassword);
        Log.d("AddEmployeeActivity", "Employee Data: " + name + ", " + phone + ", " + email + ", " + address + ", " + pan + ", " + role);

        // Create a map to store employee data
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", name);
        employee.put("phone", phone);
        employee.put("email", email);
        employee.put("address", address);
        employee.put("pan", pan);
        employee.put("role", role);
        employee.put("password", hashedPassword);

        // Add employee to Firestore
        db.collection("employees")
                .add(employee)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddEmployeeActivity", "Employee added successfully: " + documentReference.getId());
                    Toast.makeText(context, name + " has been added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error adding employee: " + e.getMessage(), e);
                    Toast.makeText(context, "Error adding employee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
