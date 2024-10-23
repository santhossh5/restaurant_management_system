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

public class orderAdapterChef extends RecyclerView.Adapter<orderAdapterChef.OrderViewHolder> {

    private Context context;
    private List<Customer_Order> orderList;

    public orderAdapterChef(orderStatusActivityChef orderStatusActivity1, List<Customer_Order> orderList) {
        this.context = orderStatusActivity1;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_chef, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Customer_Order order = orderList.get(position);

        if (order != null) {
            holder.tableNumber.setText("Table Number: " + order.getTableNumber());
            holder.orderStatus.setText("Order Status: " + order.getOrderStatus());
            holder.timestamp.setText("Timestamp: " + formatTimestamp(order.getTimestamp()));

            // Display items (map of food IDs to quantities)
            StringBuilder itemsStringBuilder = new StringBuilder();
            for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                itemsStringBuilder.append("").append(entry.getKey())
                        .append(",\t\t\tQuantity: ").append(entry.getValue()).append("\n");
            }
            holder.items.setText("Items: \n" + itemsStringBuilder.toString());

            // Set checkbox state based on order status
            holder.checkboxReady.setChecked("Ready".equals(order.getOrderStatus()));

            // Handle checkbox clicks to update order status and remove the order from the list
            holder.checkboxReady.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Update the status to "Ready" in the UI
                    holder.orderStatus.setText("Order Status: Ready");
                    order.setOrderStatus("Ready");

                    // Update the status in the database without deleting the order
                    updateOrderStatusInDatabase(order);

                    // Remove the order from the list and notify the adapter
                    removeOrder(position);
                } else {
                    // If unchecked, change back to "Pending" (if needed)
                    holder.orderStatus.setText("Order Status: Pending");
                    order.setOrderStatus("Pending");

                    // Update the status in the database without deleting the order
                    updateOrderStatusInDatabase(order);
                }
            });
        } else {
            Log.e("OrderAdapter", "Order is null at position: " + position);
        }
    }

    private void updateOrderStatusInDatabase(Customer_Order order) {
        // Update the order status in the database without deleting the order
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("santhossh")
                .document("oQNunJYjNaAQomaZ3COt")
                .collection("orders")
                .document(order.getSessionId()) // Use the session ID of the order
                .collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Update only the order status field
                        document.getReference().update("orderStatus", order.getOrderStatus())
                                .addOnSuccessListener(aVoid -> Log.d("OrderAdapter", "Order status updated successfully"))
                                .addOnFailureListener(e -> Log.e("OrderAdapter", "Failed to update order status", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderAdapter", "Error fetching order for update: " + e.getMessage(), e));
    }

    private void removeOrder(int position) {
        // Remove the order from the list
        orderList.remove(position);

        // Notify the adapter about the item removed
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, orderStatus, timestamp, items;
        CheckBox checkboxReady;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assign TextView objects from item_order.xml
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
