package com.santhossh.restaurant_management_system;

public class OrderItem {
    private String name;
    private int quantity;
    private double price;
    private double total;

    public OrderItem(String name, int quantity, double price, double total) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }
}
