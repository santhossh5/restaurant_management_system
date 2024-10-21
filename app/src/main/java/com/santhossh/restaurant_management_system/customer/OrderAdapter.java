package com.santhossh.restaurant_management_system.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.santhossh.restaurant_management_system.R;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
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

        public void bind(Order order) {
            // Convert timestamp to a human-readable format (optional)
            String formattedTimestamp = java.text.DateFormat.getDateTimeInstance().format(order.getTimestamp());

            StringBuilder orderSummary = new StringBuilder();
            for(Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                String foodName = entry.getKey();
                int quantity = entry.getValue();
                orderSummary.append(foodName).append(": ").append(quantity).append("\n");
            }
            // Bind data to the views
            orderItems.setText(orderSummary);
            orderStatus.setText("Status: " + order.getOrderStatus());
            orderTimestamp.setText("Ordered on: " + formattedTimestamp);
        }
    }
}
