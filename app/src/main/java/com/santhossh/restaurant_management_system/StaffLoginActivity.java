package com.santhossh.restaurant_management_system;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StaffLoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    ImageView btnHome;
    StaffLoginAdapter staffLoginAdapter; // Declare LoginAdapter
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_activity_login);

        // Initialize views and LoginAdapter
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        btnHome = findViewById(R.id.btnHome);
        forgotPassword = findViewById(R.id.forgotPassword); // Forgot Password TextView
        staffLoginAdapter = new StaffLoginAdapter();

        // Home button listener
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffLoginActivity.this, LoginSelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String pass = password.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()) {
                    // Perform local authentication
                    if (staffLoginAdapter.authenticateLocal(email, pass)) {
                        String role = staffLoginAdapter.getRole(email);
                        switch (role) {
                            case "Manager":
                                startActivity(new Intent(StaffLoginActivity.this, ManagerHomeActivity.class));
                                break;
                            case "Chef":
                                startActivity(new Intent(StaffLoginActivity.this, ChefHomePage.class));
                                break;
                            case "Admin":
                                startActivity(new Intent(StaffLoginActivity.this, Homepage_Admin.class));
                                break;
                            case "Waiter":
                                startActivity(new Intent(StaffLoginActivity.this, WaiterHomePageActivity.class));
                                break;
                            default:
                                Toast.makeText(StaffLoginActivity.this, "This role doesn't have login access!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StaffLoginActivity.this, "Login Failed! Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StaffLoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forgot Password listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StaffLoginActivity.this, "Contact Admin to Reset password", Toast.LENGTH_LONG).show();
            }
        });
    }
}
