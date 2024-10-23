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

public class ManagerOrderAdapter extends RecyclerView.Adapter<ManagerOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Customer_Order> customerOrderList;

    public ManagerOrderAdapter(ManagerOrderStatusActivity managerOrderStatusActivity, List<Customer_Order> customerOrderList) {
        this.context = context;
        this.customerOrderList = customerOrderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Customer_Order customerOrder = customerOrderList.get(position);

        if (customerOrder != null) {
            holder.tableNumber.setText("Table Number: " + customerOrder.getTableNumber());
            holder.orderStatus.setText("Order Status: " + customerOrder.getOrderStatus());
            holder.timestamp.setText("Timestamp: " + formatTimestamp(customerOrder.getTimestamp()));

            // Display items (map of food IDs to quantities)
            StringBuilder itemsStringBuilder = new StringBuilder();
            for (Map.Entry<String, Integer> entry : customerOrder.getItems().entrySet()) {
                itemsStringBuilder.append("").append(entry.getKey())
                        .append(",\t\t\tQuantity: ").append(entry.getValue()).append("\n");
            }
            holder.items.setText("Items: \n" + itemsStringBuilder.toString());
            Log.d("OrderAdapter", "Binding order: " + customerOrder.getTableNumber());
        } else {
            Log.e("OrderAdapter", "Order is null at position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return customerOrderList.size();
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

