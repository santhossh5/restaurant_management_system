package com.santhossh.restaurant_management_system.customer;

import java.util.Map;

public class Order {
    private Map<String, Integer> items;  // Map of food IDs to quantities
    private long timestamp;  // Timestamp for when the order was placed
    private String orderStatus;  // Status of the order (e.g., "Pending", "In Progress", "Completed")
    private String tableNumber;  // Table number for the order
    private String sessionId;  // Unique session ID for the order

    // Default constructor
    public Order() {
    }

    // Getters and Setters for all fields

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Override toString method to include sessionId

    @Override
    public String toString() {
        return "Order{" +
                "items=" + items +
                ", timestamp=" + timestamp +
                ", orderStatus='" + orderStatus + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
