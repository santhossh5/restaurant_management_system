package com.santhossh.restaurant_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    // Map to store the selected quantities for each food item
    private Map<String, Integer> orderMap = new HashMap<>();

    public FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.textFoodName.setText(food.getName());
        holder.textFoodDescription.setText(food.getDescription());
        holder.textFoodPrice.setText("₹" + food.getPrice());
        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(food.getImageUrl()) // Load image from Firebase Storage URL
                .into(holder.foodImageView); // ImageView where the image will be loaded


        // Set initial quantity to 0 (or from the order map if already selected)
        holder.textQuantity.setText(String.valueOf(orderMap.getOrDefault(food.getName(), 0)));

        // Increment button logic
        holder.incrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.textQuantity.getText().toString());
            currentQuantity++;
            holder.textQuantity.setText(String.valueOf(currentQuantity));
            orderMap.put(food.getName(), currentQuantity); // Update the quantity in the order map
        });

        // Decrement button logic
        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.textQuantity.getText().toString());
            if (currentQuantity > 0) {
                currentQuantity--;
                holder.textQuantity.setText(String.valueOf(currentQuantity));
                if (currentQuantity == 0) {
                    orderMap.remove(food.getName()); // Remove from order map if quantity is 0
                } else {
                    orderMap.put(food.getName(), currentQuantity); // Update the quantity in the order map
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Method to reset the order map
    public void resetOrderMap() {
        orderMap.clear(); // Clear the order map
    }

    // This method will return the selected items and their quantities
    public Map<String, Integer> getOrderMap() {
        return orderMap;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView textFoodName;
        TextView textFoodDescription;
        TextView textFoodPrice;
        TextView textQuantity;
        Button incrementButton;
        Button decrementButton;
        public ImageView foodImageView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.text_food_name);
            textFoodDescription = itemView.findViewById(R.id.text_food_description);
            textFoodPrice = itemView.findViewById(R.id.text_food_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
            foodImageView = itemView.findViewById(R.id.image_food); // ImageView to display food image
        }
    }
}
