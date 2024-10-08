package com.santhossh.restaurant_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class OrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Status");

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Fetch orders for the current table number
        fetchOrders();

        // Initialize BottomNavigationView and set selected item
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_order_status);

        // Set navigation item listener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_menu) {
                    startActivity(new Intent(OrderStatusActivity.this, MenuActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_home) {
                    startActivity(new Intent(OrderStatusActivity.this, CustomerHomePage.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_order_status) {
                    // Already on this page
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();  // Remove Firestore listener when activity stops
        }
    }

    // Fetch orders for the current table number
    private void fetchOrders() {
        String tableNumber = TableManager.getInstance().getTableNumber();
        if (tableNumber == null || tableNumber.isEmpty()) {
            Toast.makeText(this, "Table number is not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        registration = db.collection("orders")
                .whereEqualTo("tableNumber", tableNumber)  // Fetch orders for the specific table number
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(OrderStatusActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        orderList.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot snapshot : value) {
                                Order order = snapshot.toObject(Order.class);
                                orderList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();  // Notify adapter about data changes
                    }
                });
    }
}
