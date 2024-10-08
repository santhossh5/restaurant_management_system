package com.santhossh.restaurant_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private List<OrderItem> orderItemList;

    public CheckoutAdapter(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_item, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);
        holder.foodNameTextView.setText(orderItem.getName());
        holder.quantityTextView.setText("Qty: " + orderItem.getQuantity());
        holder.priceTextView.setText("$" + String.format("%.2f", orderItem.getPrice()));
        holder.totalTextView.setText("Total: $" + String.format("%.2f", orderItem.getTotal()));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView totalTextView;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.text_food_name);
            quantityTextView = itemView.findViewById(R.id.text_quantity);
            priceTextView = itemView.findViewById(R.id.text_price);
            totalTextView = itemView.findViewById(R.id.text_total);
        }
    }
}
