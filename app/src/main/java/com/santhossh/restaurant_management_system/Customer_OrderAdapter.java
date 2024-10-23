package com.santhossh.restaurant_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class Customer_OrderAdapter extends RecyclerView.Adapter<Customer_OrderAdapter.OrderViewHolder> {

    private final List<Customer_Order> customerOrderList;

    public Customer_OrderAdapter(List<Customer_Order> customerOrderList) {
        this.customerOrderList = customerOrderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Customer_Order customerOrder = customerOrderList.get(position);
        holder.bind(customerOrder);
    }

    @Override
    public int getItemCount() {
        return customerOrderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderItems;
        private final TextView orderStatus;
        private final TextView orderTimestamp;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItems = itemView.findViewById(R.id.text_order_items);
            orderStatus = itemView.findViewById(R.id.text_order_status);
            orderTimestamp = itemView.findViewById(R.id.text_order_timestamp);
        }

        public void bind(Customer_Order customerOrder) {
            // Convert timestamp to a human-readable format (optional)
            String formattedTimestamp = java.text.DateFormat.getDateTimeInstance().format(customerOrder.getTimestamp());

            StringBuilder orderSummary = new StringBuilder();
            for(Map.Entry<String, Integer> entry : customerOrder.getItems().entrySet()) {
                String foodName = entry.getKey();
                int quantity = entry.getValue();
                orderSummary.append(foodName).append(": ").append(quantity).append("\n");
            }
            // Bind data to the views
            orderItems.setText(orderSummary);
            orderStatus.setText("Status: " + customerOrder.getOrderStatus());
            orderTimestamp.setText("Ordered on: " + formattedTimestamp);
        }
    }
}
