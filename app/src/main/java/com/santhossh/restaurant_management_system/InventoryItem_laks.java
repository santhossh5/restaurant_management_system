package com.santhossh.restaurant_management_system;

public class InventoryItem_laks {
    private String name;
    private String stockStatus;
    private int quantity;
    private int imageResId;

    public InventoryItem_laks(String name, String stockStatus, int quantity, int imageResId) {
        this.name = name;
        this.stockStatus = stockStatus;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
