package com.santhossh.restaurant_management_system.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.santhossh.restaurant_management_system.R;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private List<BillItem> orderItems;

    public BillAdapter(List<BillItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillItem item = orderItems.get(position);
        holder.foodNameTextView.setText(item.getFoodName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.priceTextView.setText("₹" + String.format("%.2f", item.getPrice()*item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView quantityTextView;
        TextView priceTextView;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.text_food_name); // Ensure this ID matches your layout
            quantityTextView = itemView.findViewById(R.id.text_quantity); // Ensure this ID matches your layout
            priceTextView = itemView.findViewById(R.id.text_price); // Ensure this ID matches your layout
        }
    }
}
