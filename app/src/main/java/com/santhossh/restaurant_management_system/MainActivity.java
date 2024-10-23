package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, emailEditText, addressEditText, panEditText, roleEditText, passwordEditText;
    private Button addEmployeeButton, cancelButton, captureButton;
    private AddEmployeeActivity addEmployeeActivityHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        panEditText = findViewById(R.id.panEditText);
        roleEditText = findViewById(R.id.roleEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addEmployeeButton = findViewById(R.id.addEmployeeButton);
        cancelButton = findViewById(R.id.cancelButton);
        captureButton = findViewById(R.id.captureButton);

        addEmployeeActivityHandler = new AddEmployeeActivity();

        // Set OnClickListener for Add Employee button
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String pan = panEditText.getText().toString().trim();
                String role = roleEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Log input data for debugging
                Log.d("MainActivity", "Name: " + name);
                Log.d("MainActivity", "Phone: " + phone);
                Log.d("MainActivity", "Email: " + email);
                Log.d("MainActivity", "Address: " + address);
                Log.d("MainActivity", "Pan: " + pan);
                Log.d("MainActivity", "Role: " + role);
                Log.d("MainActivity", "Password: " + password);

                if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() ||
                        pan.isEmpty() || role.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call the method to add employee
                addEmployeeActivityHandler.addEmployee(name, phone, email, address, pan, role, password, MainActivity.this);

                // Redirect to ViewEmployeeActivity after adding employee
                Intent intent = new Intent(MainActivity.this, ViewEmployeeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set OnClickListener for Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();  // Reset form fields
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Capture Image functionality is disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        addressEditText.setText("");
        panEditText.setText("");
        roleEditText.setText("");
        passwordEditText.setText("");
    }
}
