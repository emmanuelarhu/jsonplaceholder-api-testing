package com.emmanuelarhu.models;

/**
 * comments model for JSONPlaceholder API
 *
 * @author Emmanuel Arhu
 */
public class comments {
    private Integer id;
    private Integer postId;
    private String name;
    private String email;
    private String body;

    // Default constructor
    public comments() {}

    // Constructor
    public comments(Integer postId, String name, String email, String body) {
        this.postId = postId;
        this.name = name;
        this.email = email;
        this.body = body;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("comments{id=%d, postId=%d, name='%s'}", id, postId, name);
    }
}