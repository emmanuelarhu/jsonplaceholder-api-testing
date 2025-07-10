package com.emmanuelarhu.models;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;

/**
 * users model for JSONPlaceholder API
 *
 * @author Emmanuel Arhu
 */
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Add this line
public class users {
    private Integer id;
    private String name;
    private String username;
    private String email;

    // Default constructor
    public users() {}

    // Constructor
    public users(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("users{id=%d, name='%s', email='%s'}", id, name, email);
    }
}