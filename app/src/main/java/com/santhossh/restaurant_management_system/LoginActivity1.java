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

public class LoginActivity1 extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    ImageView btnHome;
    LoginAdapter loginAdapter; // Declare LoginAdapter
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        // Initialize views and LoginAdapter
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        btnHome = findViewById(R.id.btnHome);
        forgotPassword = findViewById(R.id.forgotPassword); // Forgot Password TextView
        loginAdapter = new LoginAdapter();

        // Home button listener
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity1.this, LoginSelectionActivity.class);
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
                    if (loginAdapter.authenticateLocal(email, pass)) {
                        String role = loginAdapter.getRole(email);
                        switch (role) {
                            case "Manager":
                                startActivity(new Intent(LoginActivity1.this, ManagerHomeActivity.class));
                                break;
                            case "Chef":
                                startActivity(new Intent(LoginActivity1.this, ManagerHomeActivity.class));
                                break;
                            case "Admin":
                                startActivity(new Intent(LoginActivity1.this, Homepage_Admin.class));
                                break;
                            case "Waiter":
                                startActivity(new Intent(LoginActivity1.this, ManagerHomeActivity.class));
                                break;
                            default:
                                Toast.makeText(LoginActivity1.this, "This role doesn't have login access!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity1.this, "Login Failed! Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity1.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forgot Password listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity1.this, "Contact Admin to Reset password", Toast.LENGTH_LONG).show();
            }
        });
    }
}
