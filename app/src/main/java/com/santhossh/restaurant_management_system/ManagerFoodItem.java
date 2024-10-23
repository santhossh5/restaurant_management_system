package com.santhossh.restaurant_management_system;

public class ManagerFoodItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private boolean inStock;
    private String imageUrl;
    // New field for image URL


    public ManagerFoodItem() {
        // Empty constructor for Firebase
    }

    public ManagerFoodItem(String id, String name, String description, double price, boolean inStock, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.inStock = inStock;
        this.imageUrl = imageUrl; // Initialize image URL
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getImageUrl() {
        return imageUrl; // Getter for image URL
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl; // Setter for image URL
    }
}
