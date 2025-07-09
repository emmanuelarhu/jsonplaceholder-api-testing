package com.emmanuelarhu.models;

/**
 * albums model for JSONPlaceholder API
 *
 * @author Emmanuel Arhu
 */
public class albums {
    private Integer id;
    private Integer userId;
    private String title;

    // Default constructor
    public albums() {}

    // Constructor
    public albums(Integer userId, String title) {
        this.userId = userId;
        this.title = title;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("albums{id=%d, userId=%d, title='%s'}", id, userId, title);
    }
}