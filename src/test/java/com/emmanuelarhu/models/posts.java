package com.emmanuelarhu.models;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;

/**
 * Simple posts model for JSONPlaceholder
 */
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Add this line
public class posts {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    // Default constructor
    public posts() {}

    // Constructor for creating new posts
    public posts(Integer userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return String.format("posts{id=%d, userId=%d, title='%s'}", id, userId, title);
    }
}