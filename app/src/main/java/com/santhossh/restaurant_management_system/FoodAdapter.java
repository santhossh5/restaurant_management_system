package com.santhossh.restaurant_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.textFoodPrice.setText("$" + food.getPrice());

        // Set initial quantity to 0 (or from the order map if already selected)
        holder.textQuantity.setText(String.valueOf(orderMap.getOrDefault(food.getId(), 0)));

        // Increment button logic
        holder.incrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.textQuantity.getText().toString());
            currentQuantity++;
            holder.textQuantity.setText(String.valueOf(currentQuantity));
            orderMap.put(food.getId(), currentQuantity); // Update the quantity in the order map
        });

        // Decrement button logic
        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.textQuantity.getText().toString());
            if (currentQuantity > 0) {
                currentQuantity--;
                holder.textQuantity.setText(String.valueOf(currentQuantity));
                if (currentQuantity == 0) {
                    orderMap.remove(food.getId()); // Remove from order map if quantity is 0
                } else {
                    orderMap.put(food.getId(), currentQuantity); // Update the quantity in the order map
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
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

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.text_food_name);
            textFoodDescription = itemView.findViewById(R.id.text_food_description);
            textFoodPrice = itemView.findViewById(R.id.text_food_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
        }
    }
}
