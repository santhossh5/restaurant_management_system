package com.santhossh.restaurant_management_system.customer;

public class BillItem {
    private String foodName;
    private int quantity;
    private double price;

    public BillItem(String foodName, int quantity, double price) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // Total price for this item (quantity * price)
    public double getTotalPrice() {
        return price * quantity;
    }
}
