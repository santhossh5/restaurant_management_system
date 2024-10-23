package com.santhossh.restaurant_management_system;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class StaffLoginAdapter {

    private Map<String, Employee> employeeData = new HashMap<>();
    private FirebaseFirestore db;

    public StaffLoginAdapter() {
        db = FirebaseFirestore.getInstance();
        fetchAllEmployees();
    }

    // Fetch all employees from Firebase and store them in local data
    private void fetchAllEmployees() {
        db.collection("employees")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle any errors here
                        return;
                    }

                    if (value != null) {
                        employeeData.clear(); // Clear the local map to prevent duplicates
                        for (QueryDocumentSnapshot document : value) {
                            Employee employee = document.toObject(Employee.class);
                            employeeData.put(employee.getEmail(), employee); // Store by email (username)
                        }
                    }
                });
    }

    // Perform local authentication
    public boolean authenticateLocal(String email, String password) {
        if (employeeData.containsKey(email)) {
            Employee employee = employeeData.get(email);
            return employee.getPassword().equals(password);
        }
        return false;
    }

    // Get the role for the authenticated user
    public String getRole(String email) {
        if (employeeData.containsKey(email)) {
            return employeeData.get(email).getRole();
        }
        return null;
    }

    // Employee model class
    // Employee model class
    public static class Employee {
        private String name, email, password, address, phone, role, pan;

        // Getters and setters for Firebase serialization
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getPan() { return pan; }
        public void setPan(String pan) { this.pan = pan; }
    }

}