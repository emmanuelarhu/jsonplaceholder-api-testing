package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.comments;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for Comments API
 *
 * @author Emmanuel Arhu
 */
@Feature("Comments API")
public class CommentsTest extends BaseTest {

    @Test
    @DisplayName("GET /comments - Should return all 500 comments")
    @Description("Verify that we can retrieve all comments and get exactly 500 comments")
    public void testGetAllComments() {
        Response response = getRequest()
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .body("$", hasSize(500))
                .body("[0].id", notNullValue())
                .body("[0].postId", notNullValue())
                .body("[0].name", not(emptyString()))
                .body("[0].email", containsString("@"))
                .body("[0].body", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to comments objects and verify
        comments[] comments = response.as(comments[].class);
        assertEquals(500, comments.length, "Should have exactly 500 comments");

        comments firstComment = comments[0];
        assertNotNull(firstComment.getId(), "comments should have an ID");
        assertNotNull(firstComment.getPostId(), "comments should have a postId");
        assertFalse(firstComment.getName().isEmpty(), "comments should have a name");
        assertTrue(firstComment.getEmail().contains("@"), "comments should have valid email");
        assertFalse(firstComment.getBody().isEmpty(), "comments should have a body");

        System.out.println("✅ Successfully retrieved " + comments.length + " comments");
    }

    @Test
    @DisplayName("GET /comments/1 - Should return specific comment")
    @Description("Verify that we can retrieve a specific comment by ID")
    public void testGetSingleComment() {
        Response response = getRequest()
                .when()
                .get("/comments/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("postId", notNullValue())
                .body("name", not(emptyString()))
                .body("email", containsString("@"))
                .body("body", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to comments object and verify
        comments comment = response.as(comments.class);
        assertEquals(1, comment.getId(), "comments ID should be 1");
        assertNotNull(comment.getPostId(), "comments should have a postId");
        assertFalse(comment.getName().isEmpty(), "comments should have a name");
        assertTrue(comment.getEmail().contains("@"), "comments should have valid email");

        System.out.println("✅ Successfully retrieved comment: " + comment.getId());
    }

    @Test
    @DisplayName("POST /comments - Should create a new comment")
    @Description("Verify that we can create a new comment")
    public void testCreateNewComment() {
        comments newComment = new comments(1, "Test comments", "test@example.com", "This is a test comment");

        Response response = getRequest()
                .body(newComment)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("postId", equalTo(1))
                .body("name", equalTo("Test comments"))
                .body("email", equalTo("test@example.com"))
                .body("body", equalTo("This is a test comment"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to comments object and verify
        comments createdComment = response.as(comments.class);
        assertNotNull(createdComment.getId(), "Created comment should have an ID");
        assertEquals(1, createdComment.getPostId(), "Created comment should have correct postId");
        assertEquals("Test comments", createdComment.getName(), "Created comment should have correct name");
        assertEquals("test@example.com", createdComment.getEmail(), "Created comment should have correct email");

        System.out.println("✅ Successfully created new comment with ID: " + createdComment.getId());
    }

    @Test
    @DisplayName("PUT /comments/1 - Should update existing comment")
    @Description("Verify that we can completely update an existing comment")
    public void testUpdateComment() {
        comments updatedComment = new comments(1, "Updated comments", "updated@example.com", "This is an updated comment");
        updatedComment.setId(1);

        Response response = getRequest()
                .body(updatedComment)
                .when()
                .put("/comments/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("postId", equalTo(1))
                .body("name", equalTo("Updated comments"))
                .body("email", equalTo("updated@example.com"))
                .body("body", equalTo("This is an updated comment"))
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully updated comment 1");
    }

    @Test
    @DisplayName("DELETE /comments/1 - Should delete existing comment")
    @Description("Verify that we can delete an existing comment")
    public void testDeleteComment() {
        Response response = getRequest()
                .when()
                .delete("/comments/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted comment 1");
    }

    @Test
    @DisplayName("GET /comments?postId=1 - Should filter comments by post")
    @Description("Verify that we can filter comments by post ID")
    public void testFilterCommentsByPost() {
        Response response = getRequest()
                .queryParam("postId", 1)
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("postId", everyItem(equalTo(1)))
                .extract().response();

        verifyResponseTime(response.getTime());

        comments[] comments = response.as(comments[].class);
        assertTrue(comments.length > 0, "Should have comments for post 1");

        for (comments comment : comments) {
            assertEquals(1, comment.getPostId(), "All comments should belong to post 1");
        }

        System.out.println("✅ Successfully filtered " + comments.length + " comments by postId=1");
    }
}