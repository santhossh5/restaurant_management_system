package com.santhossh.restaurant_management_system;

public class Food {

    private String id;          // Unique identifier for the food item
    private String name;        // Name of the food item
    private String description; // Description of the food item
    private double price;       // Price of the food item
    private String imageUrl;    // URL for the food item's image
    private boolean inStock; // New field for stock status

    // Empty constructor (required for Firebase)
    public Food() {}

    // Constructor with fields
    public Food(String id, String name, String description, double price,String imageUrl, boolean inStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
    }

    // Getters and setters for the fields
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInStock() { // Getter for the inStock field
        return inStock;
    }
}
