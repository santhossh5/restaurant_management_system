package com.santhossh.restaurant_management_system;


import java.util.HashMap;
import java.util.Map;

public class Order_Waiter {
    private String sessionId;  // Unique identifier for the order
    private String tableNumber; // Table number associated with the order
    private String orderStatus; // Current status of the order (e.g., "Pending", "Ready")
    private long timestamp;     // Timestamp of when the order was created
    private Map<String, Integer> items; // Map of food item IDs and their quantities

    public Order_Waiter() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order_Waiter(String sessionId, String tableNumber, String orderStatus, long timestamp, Map<String, Integer> items) {
        this.sessionId = sessionId;
        this.tableNumber = tableNumber;
        this.orderStatus = orderStatus;
        this.timestamp = timestamp;
        this.items = items != null ? items : new HashMap<>();
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}

