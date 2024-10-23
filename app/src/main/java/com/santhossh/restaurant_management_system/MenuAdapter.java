package com.santhossh.restaurant_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<FoodItem> foodItemList;
    private Map<String, Boolean> selectedItems = new HashMap<>();
    private OnCheckboxChangeListener onCheckboxChangeListener;

    // Interface for checkbox state change listener
    public interface OnCheckboxChangeListener {
        void onCheckboxChange();
    }

    public MenuAdapter(List<FoodItem> foodItemList, OnCheckboxChangeListener onCheckboxChangeListener) {
        this.foodItemList = foodItemList;
        this.onCheckboxChangeListener = onCheckboxChangeListener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.tvItemName.setText(foodItem.getName());
        holder.tvItemDescription.setText(foodItem.getDescription());
        holder.tvItemPrice.setText(String.format("₹%.2f", foodItem.getPrice()));
        holder.checkBoxInStock.setChecked(foodItem.isInStock());

        // Load image using Glide library
        Glide.with(holder.itemView.getContext())
                .load(foodItem.getImageUrl()) // Image URL from the FoodItem object
                .placeholder(R.drawable.ic_food_placeholder) // Placeholder while loading
                .into(holder.foodImageView); // Target ImageView

        // Ensure the checkbox reflects the selected items state
        holder.checkBoxInStock.setOnCheckedChangeListener(null); // Clear previous listener
        holder.checkBoxInStock.setChecked(selectedItems.getOrDefault(foodItem.getId(), foodItem.isInStock()));

        // Update selected items map based on checkbox state and notify listener
        holder.checkBoxInStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.put(foodItem.getId(), isChecked);
            onCheckboxChangeListener.onCheckboxChange(); // Notify that a checkbox was clicked
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    // Expose selected items and their stock status
    public Map<String, Boolean> getSelectedItems() {
        return selectedItems;
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDescription, tvItemPrice;
        CheckBox checkBoxInStock;
        ImageView foodImageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            checkBoxInStock = itemView.findViewById(R.id.checkBoxInStock);
            foodImageView = itemView.findViewById(R.id.foodImageView); // Reference to ImageView for food image
        }
    }
}
