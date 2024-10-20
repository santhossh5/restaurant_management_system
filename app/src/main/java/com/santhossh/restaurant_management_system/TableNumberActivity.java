package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TableNumberActivity extends AppCompatActivity {

    private EditText editTextTableNumber;
    private Button buttonSubmitTableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_number);

        editTextTableNumber = findViewById(R.id.edit_text_table_number);
        buttonSubmitTableNumber = findViewById(R.id.button_submit_table_number);

        buttonSubmitTableNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableNumber = editTextTableNumber.getText().toString().trim();

                if (!tableNumber.isEmpty()) {
                    TableManager.getInstance().setTableNumber(tableNumber);

                    Intent intent = new Intent(TableNumberActivity.this, CustomerHomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Optional: finish this activity to remove it from back stack
                } else {
                    Toast.makeText(TableNumberActivity.this, "Please enter a table number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
