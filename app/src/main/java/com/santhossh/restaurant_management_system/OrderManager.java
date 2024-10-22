package com.santhossh.restaurant_management_system;

import java.util.HashMap;

public class OrderManager {

    private static OrderManager instance;
    private Order currentOrder;
    private String sessionId;

    private OrderManager() {
        // Private constructor to prevent direct instantiation
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    // Method to create a new order and associate it with a session ID
    public void createNewOrder(String tableNumber) {
        // Generate a new session ID
        sessionId = java.util.UUID.randomUUID().toString();
        currentOrder = new Order();
        currentOrder.setTableNumber(tableNumber);
        currentOrder.setSessionId(sessionId);
        currentOrder.setTimestamp(System.currentTimeMillis());
        currentOrder.setItems(new HashMap<>());
        currentOrder.setOrderStatus("Pending");
    }

    public String getSessionId() {
        return sessionId;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void clearOrder() {
        currentOrder = null;
        sessionId = null;
    }
}
