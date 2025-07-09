package com.emmanuelarhu.models;

/**
 * todos model for JSONPlaceholder API
 *
 * @author Emmanuel Arhu
 */
public class todos {
    private Integer id;
    private Integer userId;
    private String title;
    private Boolean completed;

    // Default constructor
    public todos() {}

    // Constructor
    public todos(Integer userId, String title, Boolean completed) {
        this.userId = userId;
        this.title = title;
        this.completed = completed;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return String.format("todos{id=%d, userId=%d, title='%s', completed=%s}", id, userId, title, completed);
    }
}