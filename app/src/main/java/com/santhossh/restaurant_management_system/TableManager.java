package com.santhossh.restaurant_management_system;

public class TableManager {
    private static TableManager instance; // Single instance
    private String tableNumber; // Variable to hold the table number

    // Private constructor to prevent instantiation from outside
    private TableManager() {
        // Initialize the table number if necessary
        tableNumber = ""; // Default value
    }

    // Method to get the single instance of the class
    public static synchronized TableManager getInstance() {
        if (instance == null) {
            instance = new TableManager(); // Create the instance if it doesn't exist
        }
        return instance; // Return the single instance
    }

    // Getter for table number
    public String getTableNumber() {
        return tableNumber;
    }

    // Setter for table number
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber; // Set the table number
    }
}
