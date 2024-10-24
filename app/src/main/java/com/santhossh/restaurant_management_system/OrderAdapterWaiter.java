package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapterWaiter extends RecyclerView.Adapter<OrderAdapterWaiter.OrderViewHolder> {

    private Context context;
    private List<Order_Waiter> orderWaiterList;

    public OrderAdapterWaiter(WaiterHomePageActivity waiterHomePageActivity, List<Order_Waiter> orderWaiterList) {
        this.context = waiterHomePageActivity;
        this.orderWaiterList = orderWaiterList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the correct layout for each order item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_waiter, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order_Waiter orderWaiter = orderWaiterList.get(position);

        if (orderWaiter != null) {
            // Set table number
            holder.tableNumber.setText("Table Number: " + orderWaiter.getTableNumber());

            // Set order status
            holder.orderStatus.setText("Order Status: " + (orderWaiter.getOrderStatus() != null ? orderWaiter.getOrderStatus() : "Unknown"));

            // Set timestamp
            holder.timestamp.setText("Timestamp: " + formatTimestamp(orderWaiter.getTimestamp()));

            // Display items
            StringBuilder itemsStringBuilder = new StringBuilder();
            if (orderWaiter.getItems() != null) {
                for (Map.Entry<String, Integer> entry : orderWaiter.getItems().entrySet()) {
                    itemsStringBuilder.append(entry.getKey())
                            .append(", Quantity: ").append(entry.getValue()).append("\n");
                }
            }
            holder.items.setText("Items: \n" + itemsStringBuilder.toString());

            // Set checkbox state based on order status
            holder.checkboxReady.setChecked("Delivered".equals(orderWaiter.getOrderStatus()));

            // Clear previous listener to avoid multiple triggers
            holder.checkboxReady.setOnCheckedChangeListener(null);

            // Handle checkbox change
            holder.checkboxReady.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // If checkbox is selected, set status to "Delivered"
                    orderWaiter.setOrderStatus("Delivered");
                    updateOrderStatusInDatabase(orderWaiter);

                    // Remove the order from the list and notify the adapter
                    orderWaiterList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orderWaiterList.size());
                } else {
                    // If checkbox is unselected, revert status to "Ready" (if needed)
                    orderWaiter.setOrderStatus("Ready");
                    updateOrderStatusInDatabase(orderWaiter);
                }
            });
        } else {
            Log.e("OrderAdapterWaiter", "Order is null at position: " + position);
        }
    }



    private void updateOrderStatusInDatabase(Order_Waiter orderWaiter) {
        // Specify the document ID
        String documentId = "oQNunJYjNaAQomaZ3COt";

        // Update the order status in the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .document(orderWaiter.getSessionId())
                .collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("orderStatus", orderWaiter.getOrderStatus())
                                .addOnSuccessListener(aVoid -> Log.d("OrderAdapterWaiter", "Order status updated successfully"))
                                .addOnFailureListener(e -> Log.e("OrderAdapterWaiter", "Failed to update order status", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderAdapterWaiter", "Error fetching order for update: " + e.getMessage(), e));
    }

    public void fetchOrders() {
        String documentId = "oQNunJYjNaAQomaZ3COt";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch orders with status 'Ready'
        db.collection("santhossh")
                .document(documentId)
                .collection("orders")
                .whereEqualTo("orderStatus", "Ready") // Filter by status "Ready"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderWaiterList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order_Waiter orderWaiter = document.toObject(Order_Waiter.class);
                        orderWaiterList.add(orderWaiter);
                    }
                    notifyDataSetChanged(); // Notify RecyclerView to refresh data
                })
                .addOnFailureListener(e -> Log.e("OrderAdapterWaiter", "Failed to fetch orders: " + e.getMessage()));
    }



    @Override
    public int getItemCount() {
        return orderWaiterList.size();
    }

    // ViewHolder class
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, orderStatus, timestamp, items;
        CheckBox checkboxReady;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assign TextView objects from item_order_waiter.xml
            tableNumber = itemView.findViewById(R.id.tvTableNumber);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
            items = itemView.findViewById(R.id.tvItems);
            checkboxReady = itemView.findViewById(R.id.checkboxReady);
        }
    }

    // Helper method to format timestamp into human-readable date/time
    private String formatTimestamp(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}