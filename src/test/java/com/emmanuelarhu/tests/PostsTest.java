package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.posts;
import com.emmanuelarhu.models.comments;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple and focused tests for Posts API
 *
 * @author Emmanuel Arhu
 */
@Feature("Posts API")
public class PostsTest extends BaseTest {

    @Test
    @DisplayName("GET /posts - Should return all 100 posts")
    @Description("Verify that we can retrieve all posts and get exactly 100 posts")
    public void testGetAllPosts() {
        Response response = getRequest()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("$", hasSize(100))
                .body("[0].id", notNullValue())
                .body("[0].userId", notNullValue())
                .body("[0].title", not(emptyString()))
                .body("[0].body", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to Post objects and verify
        posts[] posts = response.as(posts[].class);
        assertEquals(100, posts.length, "Should have exactly 100 posts");

        posts firstPost = posts[0];
        assertNotNull(firstPost.getId(), "First post should have an ID");
        assertNotNull(firstPost.getUserId(), "First post should have a userId");
        assertFalse(firstPost.getTitle().isEmpty(), "First post should have a title");
        assertFalse(firstPost.getBody().isEmpty(), "First post should have a body");

        System.out.println("✅ Successfully retrieved " + posts.length + " posts");
    }

    @Test
    @DisplayName("GET /posts/1 - Should return specific post")
    @Description("Verify that we can retrieve a specific post by ID")
    public void testGetSinglePost() {
        Response response = getRequest()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", notNullValue())
                .body("title", not(emptyString()))
                .body("body", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to Post object and verify
        posts post = response.as(posts.class);
        assertEquals(1, post.getId(), "Post ID should be 1");
        assertNotNull(post.getUserId(), "Post should have a userId");
        assertFalse(post.getTitle().isEmpty(), "Post should have a title");
        assertFalse(post.getBody().isEmpty(), "Post should have a body");

        System.out.println("✅ Successfully retrieved post: " + post);
    }

    @Test
    @DisplayName("GET /posts/1/comments - Should return comments for post 1")
    @Description("Verify that we can retrieve comments for a specific post")
    public void testGetCommentsForPost() {
        Response response = getRequest()
                .when()
                .get("/posts/1/comments")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("postId", everyItem(equalTo(1)))
                .body("[0].id", notNullValue())
                .body("[0].name", not(emptyString()))
                .body("[0].email", containsString("@"))
                .body("[0].body", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to comments objects and verify
        comments[] comments = response.as(comments[].class);
        assertTrue(comments.length > 0, "Should have comments for post 1");

        for (comments comment : comments) {
            assertEquals(1, comment.getPostId(), "All comments should belong to post 1");
            assertNotNull(comment.getId(), "comments should have an ID");
            assertFalse(comment.getName().isEmpty(), "comments should have a name");
            assertTrue(comment.getEmail().contains("@"), "comments should have valid email");
            assertFalse(comment.getBody().isEmpty(), "comments should have a body");
        }

        System.out.println("✅ Successfully retrieved " + comments.length + " comments for post 1");
    }

    @Test
    @DisplayName("GET /comments?postId=1 - Should return comments filtered by post ID")
    @Description("Verify that we can filter comments by post ID using query parameter")
    public void testGetCommentsWithPostIdFilter() {
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

        // Convert to comments objects and verify
        comments[] comments = response.as(comments[].class);
        assertTrue(comments.length > 0, "Should have comments for post 1");

        for (comments comment : comments) {
            assertEquals(1, comment.getPostId(), "All comments should belong to post 1");
        }

        System.out.println("✅ Successfully filtered comments by postId=1, found " + comments.length + " comments");
    }

    @Test
    @DisplayName("POST /posts - Should create a new post")
    @Description("Verify that we can create a new post")
    public void testCreateNewPost() {
        posts newPost = new posts(1, "Test Post Title", "This is a test post body");

        Response response = getRequest()
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(1))
                .body("title", equalTo("Test Post Title"))
                .body("body", equalTo("This is a test post body"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to Post object and verify
        posts createdPost = response.as(posts.class);
        assertNotNull(createdPost.getId(), "Created post should have an ID");
        assertEquals(1, createdPost.getUserId(), "Created post should have correct userId");
        assertEquals("Test Post Title", createdPost.getTitle(), "Created post should have correct title");
        assertEquals("This is a test post body", createdPost.getBody(), "Created post should have correct body");

        System.out.println("✅ Successfully created new post with ID: " + createdPost.getId());
    }

    @Test
    @DisplayName("PUT /posts/1 - Should update existing post")
    @Description("Verify that we can completely update an existing post")
    public void testUpdatePost() {
        posts updatedPost = new posts(1, "Updated Post Title", "This is an updated post body");
        updatedPost.setId(1);

        Response response = getRequest()
                .body(updatedPost)
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", equalTo("Updated Post Title"))
                .body("body", equalTo("This is an updated post body"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to Post object and verify
        posts returnedPost = response.as(posts.class);
        assertEquals(1, returnedPost.getId(), "Updated post should have correct ID");
        assertEquals(1, returnedPost.getUserId(), "Updated post should have correct userId");
        assertEquals("Updated Post Title", returnedPost.getTitle(), "Updated post should have new title");
        assertEquals("This is an updated post body", returnedPost.getBody(), "Updated post should have new body");

        System.out.println("✅ Successfully updated post: " + returnedPost);
    }

    @Test
    @DisplayName("PATCH /posts/1 - Should partially update existing post")
    @Description("Verify that we can partially update an existing post")
    public void testPatchPost() {
        String patchBody = "{\"title\": \"Patched Post Title\"}";

        Response response = getRequest()
                .body(patchBody)
                .when()
                .patch("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Patched Post Title"))
                .body("userId", notNullValue())
                .body("body", notNullValue())
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to Post object and verify
        posts patchedPost = response.as(posts.class);
        assertEquals(1, patchedPost.getId(), "Patched post should have correct ID");
        assertEquals("Patched Post Title", patchedPost.getTitle(), "Patched post should have new title");
        assertNotNull(patchedPost.getUserId(), "Patched post should still have userId");
        assertNotNull(patchedPost.getBody(), "Patched post should still have body");

        System.out.println("✅ Successfully patched post title: " + patchedPost.getTitle());
    }

    @Test
    @DisplayName("DELETE /posts/1 - Should delete existing post")
    @Description("Verify that we can delete an existing post")
    public void testDeletePost() {
        Response response = getRequest()
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted post 1");
    }

    @Test
    @DisplayName("GET /posts/999 - Should return 404 for non-existent post")
    @Description("Verify that requesting a non-existent post returns 404")
    public void testGetNonExistentPost() {
        Response response = getRequest()
                .when()
                .get("/posts/999")
                .then()
                .statusCode(404)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Correctly returned 404 for non-existent post");
    }
}