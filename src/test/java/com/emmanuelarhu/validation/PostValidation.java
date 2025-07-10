package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.posts;
import static org.testng.Assert.*;

/**
 * Validation utilities for Post API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class PostValidation {

    /**
     * Validate an array of posts
     */
    public static void validatePostArray(posts[] posts, int expectedCount) {
        assertNotNull(posts, "Posts array should not be null");
        assertEquals(posts.length, expectedCount, "Should have exactly " + expectedCount + " posts");

        for (posts post : posts) {
            validateBasicPostFields(post);
        }
    }

    /**
     * Validate a created post
     */
    public static void validateCreatedPost(posts createdPost, int expectedUserId, String expectedTitle, String expectedBody) {
        assertNotNull(createdPost, "Created post should not be null");
        assertNotNull(createdPost.getId(), "Created post should have an ID");
        assertEquals(createdPost.getUserId().intValue(), expectedUserId, "Created post should have correct userId");
        assertEquals(createdPost.getTitle(), expectedTitle, "Created post should have correct title");
        assertEquals(createdPost.getBody(), expectedBody, "Created post should have correct body");
    }

    /**
     * Validate basic post fields
     */
    public static void validateBasicPostFields(posts post) {
        assertNotNull(post.getId(), "Post should have an ID");
        assertNotNull(post.getUserId(), "Post should have a userId");
        assertNotNull(post.getTitle(), "Post should have a title");
        assertNotNull(post.getBody(), "Post should have a body");

        assertFalse(post.getTitle().isEmpty(), "Post title should not be empty");
        assertFalse(post.getBody().isEmpty(), "Post body should not be empty");

        // Validate userId range (for JSONPlaceholder, valid userIds are 1-10)
        assertTrue(post.getUserId() >= 1 && post.getUserId() <= 10,
                "Post userId should be between 1 and 10 for JSONPlaceholder");
    }
}