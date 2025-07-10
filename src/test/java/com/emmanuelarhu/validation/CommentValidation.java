package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.comments;
import static org.testng.Assert.*;

/**
 * Validation utilities for Comment API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class CommentValidation {

    /**
     * Validate an array of comments
     */
    public static void validateCommentArray(comments[] comments, int expectedCount) {
        assertNotNull(comments, "Comments array should not be null");
        assertEquals(comments.length, expectedCount, "Should have exactly " + expectedCount + " comments");

        for (comments comment : comments) {
            validateBasicCommentFields(comment);
        }
    }

    /**
     * Validate a single comment by ID
     */
    public static void validateSingleComment(comments comment, int expectedId) {
        assertNotNull(comment, "Comment should not be null");
        assertEquals(comment.getId().intValue(), expectedId, "Comment ID should match expected");
        validateBasicCommentFields(comment);
    }

    /**
     * Validate a created comment
     */
    public static void validateCreatedComment(comments createdComment, int expectedPostId, String expectedName, String expectedEmail, String expectedBody) {
        assertNotNull(createdComment, "Created comment should not be null");
        assertNotNull(createdComment.getId(), "Created comment should have an ID");
        assertEquals(createdComment.getPostId().intValue(), expectedPostId, "Created comment should have correct postId");
        assertEquals(createdComment.getName(), expectedName, "Created comment should have correct name");
        assertEquals(createdComment.getEmail(), expectedEmail, "Created comment should have correct email");
        assertEquals(createdComment.getBody(), expectedBody, "Created comment should have correct body");
    }

    /**
     * Validate comments filtered by post ID
     */
    public static void validateCommentsForPost(comments[] comments, int expectedPostId) {
        assertNotNull(comments, "Comments array should not be null");
        assertTrue(comments.length > 0, "Should have comments for post " + expectedPostId);

        for (comments comment : comments) {
            assertEquals(comment.getPostId().intValue(), expectedPostId,
                    "All comments should belong to post " + expectedPostId);
            validateBasicCommentFields(comment);
        }
    }

    /**
     * Validate basic comment fields
     */
    private static void validateBasicCommentFields(comments comment) {
        assertNotNull(comment.getId(), "Comment should have an ID");
        assertNotNull(comment.getPostId(), "Comment should have a postId");
        assertNotNull(comment.getName(), "Comment should have a name");
        assertNotNull(comment.getEmail(), "Comment should have an email");
        assertNotNull(comment.getBody(), "Comment should have a body");

        assertFalse(comment.getName().isEmpty(), "Comment name should not be empty");
        assertFalse(comment.getBody().isEmpty(), "Comment body should not be empty");
        assertTrue(comment.getEmail().contains("@"), "Comment should have valid email format");

        // Validate postId range (for JSONPlaceholder, valid postIds are 1-100)
        assertTrue(comment.getPostId() >= 1 && comment.getPostId() <= 100,
                "Comment postId should be between 1 and 100 for JSONPlaceholder");
    }

    /**
     * Validate email format more strictly for comments
     */
    public static void validateCommentEmailFormat(String email) {
        if (email != null) {
            assertTrue(email.contains("@"), "Email should contain @ symbol");
            assertTrue(email.contains("."), "Email should contain domain extension");
            assertFalse(email.startsWith("@"), "Email should not start with @");
            assertFalse(email.endsWith("@"), "Email should not end with @");
            assertTrue(email.length() > 5, "Email should be at least 6 characters long");
        }
    }
}