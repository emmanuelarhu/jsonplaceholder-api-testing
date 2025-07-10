package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.posts;
import com.emmanuelarhu.models.comments;
import com.emmanuelarhu.validation.PostValidation;
import com.emmanuelarhu.validation.CommentValidation;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Complete Posts API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Posts API")
public class PostsTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all posts and get exactly 100 posts")
    public void testGetAllPosts() {
        try {
            Response response = makeApiCall("/posts", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(100))
                    .body("[0].id", notNullValue())
                    .body("[0].userId", notNullValue())
                    .body("[0].title", not(emptyString()))
                    .body("[0].body", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to Post objects and verify
            posts[] posts = response.as(posts[].class);
            PostValidation.validatePostArray(posts, 100);

            System.out.println("✅ Successfully retrieved " + posts.length + " posts");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPostIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific posts by valid IDs")
    public void testGetSinglePost(int postId) {
        try {
            Response response = makeApiCall("/posts/" + postId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(postId))
                    .body("userId", notNullValue())
                    .body("title", not(emptyString()))
                    .body("body", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to Post object and verify
            posts post = response.as(posts.class);
            assertEquals(post.getId().intValue(), postId, "Post ID should match expected");
            PostValidation.validateBasicPostFields(post);

            System.out.println("✅ Successfully retrieved post: " + post);
        } catch (Exception e) {
            fail("Test failed for postId " + postId + " due to: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can retrieve comments for a specific post")
    public void testGetCommentsForPost() {
        try {
            Response response = makeApiCall("/posts/1/comments", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("postId", everyItem(equalTo(1)))
                    .body("[0].id", notNullValue())
                    .body("[0].name", not(emptyString()))
                    .body("[0].email", containsString("@"))
                    .body("[0].body", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to comments objects and verify
            comments[] comments = response.as(comments[].class);
            CommentValidation.validateCommentsForPost(comments, 1);

            System.out.println("✅ Successfully retrieved " + comments.length + " comments for post 1");
        } catch (Exception e) {
            fail("Test failed for getting comments for post due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userIdFilters", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter comments by post ID using query parameter")
    public void testGetCommentsWithPostIdFilter(int postId) {
        try {
            Response response = getRequest()
                    .queryParam("postId", postId)
                    .when()
                    .get("/comments")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("postId", everyItem(equalTo(postId)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Convert to comments objects and verify
            comments[] comments = response.as(comments[].class);
            CommentValidation.validateCommentsForPost(comments, postId);

            System.out.println("✅ Successfully filtered comments by postId=" + postId + ", found " + comments.length + " comments");
        } catch (Exception e) {
            fail("Test failed for comment filtering by postId " + postId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPostData", dataProviderClass = TestDataProvider.class, priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new posts with valid data")
    public void testCreateNewPost(int userId, String title, String body) {
        try {
            posts newPost = TestDataProvider.createValidPost(userId, title, body);

            Response response = getRequest()
                    .body(newPost)
                    .when()
                    .post("/posts")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .body("body", equalTo(body))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Convert to Post object and verify
            posts createdPost = response.as(posts.class);
            PostValidation.validateCreatedPost(createdPost, userId, title, body);

            System.out.println("✅ Successfully created new post with ID: " + createdPost.getId());
        } catch (Exception e) {
            fail("Test failed for post creation due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPostData", dataProviderClass = TestDataProvider.class, priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can completely update an existing post")
    public void testUpdatePost(int userId, String title, String body) {
        try {
            posts updatedPost = TestDataProvider.createValidPost(userId, title, body);
            updatedPost.setId(1);

            Response response = getRequest()
                    .body(updatedPost)
                    .when()
                    .put("/posts/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .body("body", equalTo(body))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Convert to Post object and verify
            posts returnedPost = response.as(posts.class);
            assertEquals(returnedPost.getId().intValue(), 1, "Updated post should have correct ID");
            assertEquals(returnedPost.getUserId().intValue(), userId, "Updated post should have correct userId");
            assertEquals(returnedPost.getTitle(), title, "Updated post should have new title");
            assertEquals(returnedPost.getBody(), body, "Updated post should have new body");

            System.out.println("✅ Successfully updated post: " + returnedPost);
        } catch (Exception e) {
            fail("Test failed for post update due to: " + e.getMessage());
        }
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can partially update an existing post")
    public void testPatchPost() {
        try {
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
            assertEquals(patchedPost.getId().intValue(), 1, "Patched post should have correct ID");
            assertEquals(patchedPost.getTitle(), "Patched Post Title", "Patched post should have new title");
            assertNotNull(patchedPost.getUserId(), "Patched post should still have userId");
            assertNotNull(patchedPost.getBody(), "Patched post should still have body");

            System.out.println("✅ Successfully patched post title: " + patchedPost.getTitle());
        } catch (Exception e) {
            fail("Test failed for post patch due to: " + e.getMessage());
        }
    }

    @Test(priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete an existing post")
    public void testDeletePost() {
        try {
            Response response = makeApiCall("/posts/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted post 1");
        } catch (Exception e) {
            fail("Test failed for post deletion due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidPostIds", dataProviderClass = TestDataProvider.class, priority = 9)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent posts returns 404")
    public void testGetNonExistentPost(int invalidPostId) {
        try {
            Response response = getRequest()
                    .when()
                    .get("/posts/" + invalidPostId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid post ID: " + invalidPostId);
        } catch (Exception e) {
            fail("Negative test failed for invalid postId " + invalidPostId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidPostData", dataProviderClass = TestDataProvider.class, priority = 10)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating posts with invalid data handles errors appropriately")
    public void testCreatePostWithInvalidData(int userId, String title, String body) {
        try {
            posts invalidPost = TestDataProvider.createValidPost(userId, title, body);

            Response response = getRequest()
                    .body(invalidPost)
                    .when()
                    .post("/posts");

            // JSONPlaceholder is lenient, but we verify response is received
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "Should receive a valid HTTP response code");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid post data: userId=" + userId + ", title='" + title + "'");
        } catch (Exception e) {
            System.out.println("✅ Expected error for invalid post data: " + e.getMessage());
        }
    }

    @Test(priority = 11)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify behavior when trying to delete non-existent post")
    public void testDeleteNonExistentPost() {
        try {
            Response response = getRequest()
                    .when()
                    .delete("/posts/999")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for non-existent resources
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Handled deletion of non-existent post gracefully");
        } catch (Exception e) {
            fail("Negative test for deleting non-existent post failed due to: " + e.getMessage());
        }
    }

    @Test(priority = 12)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when sending malformed post JSON")
    public void testCreatePostWithMalformedJson() {
        try {
            String malformedJson = "{\"userId\":1,\"title\":\"Test\",\"body\":"; // Missing closing

            Response response = getRequest()
                    .body(malformedJson)
                    .when()
                    .post("/posts");

            // Should handle malformed JSON gracefully
            assertTrue(response.getStatusCode() >= 400, "Should return error for malformed JSON");
            System.out.println("✅ Properly handled malformed JSON with status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("✅ Expected error for malformed JSON: " + e.getMessage());
        }
    }
}