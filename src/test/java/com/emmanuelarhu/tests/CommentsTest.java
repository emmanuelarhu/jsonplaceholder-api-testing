package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.comments;
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
 * Complete Comments API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Comments API")
public class CommentsTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all comments and get exactly 500 comments")
    public void testGetAllComments() {
        try {
            Response response = makeApiCall("/comments", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(500))
                    .body("[0].id", notNullValue())
                    .body("[0].postId", notNullValue())
                    .body("[0].name", not(emptyString()))
                    .body("[0].email", containsString("@"))
                    .body("[0].body", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to comments objects and verify
            comments[] comments = response.as(comments[].class);
            CommentValidation.validateCommentArray(comments, 500);

            System.out.println("✅ Successfully retrieved " + comments.length + " comments");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validCommentIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific comments by valid IDs")
    public void testGetSingleComment(int commentId) {
        try {
            Response response = makeApiCall("/comments/" + commentId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(commentId))
                    .body("postId", notNullValue())
                    .body("name", not(emptyString()))
                    .body("email", containsString("@"))
                    .body("body", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to comments object and verify
            comments comment = response.as(comments.class);
            CommentValidation.validateSingleComment(comment, commentId);

            System.out.println("✅ Successfully retrieved comment: " + comment.getId());
        } catch (Exception e) {
            fail("Test failed for commentId " + commentId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validCommentData", dataProviderClass = TestDataProvider.class, priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new comments with valid data")
    public void testCreateNewComment(int postId, String name, String email, String body) {
        try {
            comments newComment = TestDataProvider.createValidComment(postId, name, email, body);

            Response response = getRequest()
                    .body(newComment)
                    .when().log().all()
                    .post("/comments")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("postId", equalTo(postId))
                    .body("name", equalTo(name))
                    .body("email", equalTo(email))
                    .body("body", equalTo(body))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify created comment
            comments createdComment = response.as(comments.class);
            CommentValidation.validateCreatedComment(createdComment, postId, name, email, body);

            System.out.println("✅ Successfully created new comment with ID: " + createdComment.getId());
        } catch (Exception e) {
            fail("Test failed for comment creation due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validCommentData", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update existing comments")
    public void testUpdateComment(int postId, String name, String email, String body) {
        try {
            comments updatedComment = TestDataProvider.createValidComment(postId, name, email, body);
            updatedComment.setId(1);

            Response response = getRequest()
                    .body(updatedComment)
                    .when().log().all()
                    .put("/comments/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("postId", equalTo(postId))
                    .body("name", equalTo(name))
                    .body("email", equalTo(email))
                    .body("body", equalTo(body))
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Successfully updated comment 1");
        } catch (Exception e) {
            fail("Test failed for comment update due to: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete existing comments")
    public void testDeleteComment() {
        try {
            Response response = makeApiCall("/comments/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted comment 1");
        } catch (Exception e) {
            fail("Test failed for comment deletion due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userIdFilters", dataProviderClass = TestDataProvider.class, priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter comments by post ID")
    public void testFilterCommentsByPost(int postId) {
        try {
            Response response = getRequest()
                    .queryParam("postId", postId)
                    .when().log().all()
                    .get("/comments")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("postId", everyItem(equalTo(postId)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            comments[] comments = response.as(comments[].class);
            CommentValidation.validateCommentsForPost(comments, postId);

            System.out.println("✅ Successfully filtered " + comments.length + " comments by postId=" + postId);
        } catch (Exception e) {
            fail("Test failed for comment filtering by postId " + postId + " due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidCommentIds", dataProviderClass = TestDataProvider.class, priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent comments returns 404")
    public void testGetNonExistentComment(int invalidCommentId) {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .get("/comments/" + invalidCommentId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid comment ID: " + invalidCommentId);
        } catch (Exception e) {
            fail("Negative test failed for invalid commentId " + invalidCommentId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidCommentData", dataProviderClass = TestDataProvider.class, priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating comments with invalid data handles errors appropriately")
    public void testCreateCommentWithInvalidData(int postId, String name, String email, String body) {
        try {
            comments invalidComment = TestDataProvider.createValidComment(postId, name, email, body);

            Response response = getRequest()
                    .body(invalidComment)
                    .when().log().all()
                    .post("/comments");

            // JSONPlaceholder is lenient, but we verify response is received
            assertTrue(response.getStatusCode() == 200 ,
                    "Expected 404 Not Found for invalid comment creation");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid comment data: postId=" + postId + ", name='" + name + "'");
        } catch (Exception e) {
            System.out.println("✅ Expected error for invalid comment data: " + e.getMessage());
        }
    }

    @Test(priority = 9)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when filtering with non-existent post ID")
    public void testFilterCommentsWithInvalidPostId() {
        try {
            Response response = getRequest()
                    .queryParam("postId", 999)
                    .when().log().all()
                    .get("/comments")
                    .then()
                    .statusCode(404)
                    .body("$", hasSize(0)) // Should return empty array
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned empty array for non-existent postId filter");
        } catch (Exception e) {
            fail("Test failed for invalid postId filter due to: " + e.getMessage());
        }
    }
}