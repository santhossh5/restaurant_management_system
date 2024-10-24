package com.santhossh.restaurant_management_system;

public class Feedback_laks {
    private String name;
    private String title;
    private String details;
    private float rating;

    public Feedback_laks(String name, String title, String details, float rating) {
        this.name = name;
        this.title = title;
        this.details = details;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public float getRating() {
        return rating;
    }
}
