package com.santhossh.restaurant_management_system;

import java.util.HashMap;

public class OrderManager {

    private static OrderManager instance;
    private Customer_Order currentCustomerOrder;
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
        currentCustomerOrder = new Customer_Order();
        currentCustomerOrder.setTableNumber(tableNumber);
        currentCustomerOrder.setSessionId(sessionId);
        currentCustomerOrder.setTimestamp(System.currentTimeMillis());
        currentCustomerOrder.setItems(new HashMap<>());
        currentCustomerOrder.setOrderStatus("Pending");
    }

    public String getSessionId() {
        return sessionId;
    }

    public Customer_Order getCurrentOrder() {
        return currentCustomerOrder;
    }

    public void clearOrder() {
        currentCustomerOrder = null;
        sessionId = null;
    }
}
