package com.emmanuelarhu.models;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;

/**
 * photos model for JSONPlaceholder API
 *
 * @author Emmanuel Arhu
 */
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Add this line
public class photos {
    private Integer id;
    private Integer albumId;
    private String title;
    private String url;
    private String thumbnailUrl;

    // Default constructor
    public photos() {}

    // Constructor
    public photos(Integer albumId, String title, String url, String thumbnailUrl) {
        this.albumId = albumId;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return String.format("photos{id=%d, albumId=%d, title='%s'}", id, albumId, title);
    }
}