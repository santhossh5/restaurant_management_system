package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter1 extends RecyclerView.Adapter<OrderAdapter1.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter1(OrderStatusActivity1 orderStatusActivity1, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

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
            Log.d("OrderAdapter", "Binding order: " + order.getTableNumber());
        } else {
            Log.e("OrderAdapter", "Order is null at position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, orderStatus, timestamp, items;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assign TextView objects from item_order.xml
            tableNumber = itemView.findViewById(R.id.tvTableNumber);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
            items = itemView.findViewById(R.id.tvItems);
        }
    }

    // Helper method to format timestamp into human-readable date/time
    private String formatTimestamp(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}

