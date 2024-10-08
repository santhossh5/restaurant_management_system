package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginSelectionActivity extends AppCompatActivity {

    private Button customerLoginButton;
    private Button workerLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);

        // Initialize buttons
        customerLoginButton = findViewById(R.id.customerLoginButton);

        workerLoginButton = findViewById(R.id.workerLoginButton);

        // Set onClick listener for customer login button
        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CustomerLoginActivity
                Intent intent = new Intent(LoginSelectionActivity.this, TableNumberActivity.class);
                startActivity(intent);
            }
        });

        // Set onClick listener for worker login button
        workerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WorkerLoginActivity
                Intent intent = new Intent(LoginSelectionActivity.this, WorkerLoginPage.class);
                startActivity(intent);
            }
        });
    }
}
