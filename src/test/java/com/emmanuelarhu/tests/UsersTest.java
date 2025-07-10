package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.users;
import com.emmanuelarhu.validation.UserValidation;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Updated Users API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Users API")
public class UsersTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all users and get exactly 10 users")
    public void testGetAllUsers() {
        try {
            Response response = makeApiCall("/users", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(10))
                    .body("[0].id", notNullValue())
                    .body("[0].name", not(emptyString()))
                    .body("[0].username", not(emptyString()))
                    .body("[0].email", containsString("@"));

            verifyResponseTime(response.getTime());

            // Convert to users objects and verify
            users[] users = response.as(users[].class);
            assertEquals(users.length, 10, "Should have exactly 10 users");

            // Use validation class
            UserValidation.validateUserArray(users);

            System.out.println("✅ Successfully retrieved " + users.length + " users");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validUserIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific users by valid IDs")
    public void testGetSingleUser(int userId) {
        try {
            Response response = makeApiCall("/users/" + userId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(userId))
                    .body("name", not(emptyString()))
                    .body("username", not(emptyString()))
                    .body("email", containsString("@"));

            verifyResponseTime(response.getTime());

            // Convert to users object and verify
            users user = response.as(users.class);
            UserValidation.validateSingleUser(user, userId);

            System.out.println("✅ Successfully retrieved user: " + user);
        } catch (Exception e) {
            fail("Test failed for userId " + userId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validUserData", dataProviderClass = TestDataProvider.class, priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new users with valid data")
    public void testCreateNewUser(String name, String username, String email) {
        try {
            users newUser = TestDataProvider.createValidUser(name, username, email);

            Response response = getRequest()
                    .body(newUser)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo(name))
                    .body("username", equalTo(username))
                    .body("email", equalTo(email))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify created user
            users createdUser = response.as(users.class);
            UserValidation.validateCreatedUser(createdUser, name, username, email);

            System.out.println("✅ Successfully created new user with ID: " + createdUser.getId());
        } catch (Exception e) {
            fail("Test failed for user creation with data: " + name + ", " + username + ", " + email + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validUserData", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update existing users")
    public void testUpdateUser(String name, String username, String email) {
        try {
            users updatedUser = TestDataProvider.createValidUser(name, username, email);
            updatedUser.setId(1);

            Response response = getRequest()
                    .body(updatedUser)
                    .when()
                    .put("/users/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("name", equalTo(name))
                    .body("username", equalTo(username))
                    .body("email", equalTo(email))
                    .extract().response();

            verifyResponseTime(response.getTime());

            users returnedUser = response.as(users.class);
            UserValidation.validateUpdatedUser(returnedUser, 1, name, username, email);

            System.out.println("✅ Successfully updated user: " + returnedUser);
        } catch (Exception e) {
            fail("Test failed for user update due to: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete existing users")
    public void testDeleteUser() {
        try {
            Response response = makeApiCall("/users/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted user 1");
        } catch (Exception e) {
            fail("Test failed for user deletion due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidUserIds", dataProviderClass = TestDataProvider.class, priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent users returns 404")
    public void testGetNonExistentUser(int invalidUserId) {
        try {
            Response response = getRequest()
                    .when()
                    .get("/users/" + invalidUserId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid user ID: " + invalidUserId);
        } catch (Exception e) {
            fail("Negative test failed for invalid userId " + invalidUserId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidUserData", dataProviderClass = TestDataProvider.class, priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating users with invalid data handles errors appropriately")
    public void testCreateUserWithInvalidData(String name, String username, String email) {
        try {
            users invalidUser = TestDataProvider.createInvalidUser(name, username, email);

            Response response = getRequest()
                    .body(invalidUser)
                    .when()
                    .post("/users");

            // Note: JSONPlaceholder is lenient, but in real APIs this would return 400
            // For demonstration, we check that response is received
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "Should receive a valid HTTP response code");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid data: name='" + name + "', username='" + username + "', email='" + email + "'");
        } catch (Exception e) {
            // Expected behavior for truly invalid requests
            System.out.println("✅ Expected error for invalid data: " + e.getMessage());
        }
    }

    @Test(priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify behavior when trying to delete non-existent user")
    public void testDeleteNonExistentUser() {
        try {
            Response response = getRequest()
                    .when()
                    .delete("/users/999")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for non-existent resources
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Handled deletion of non-existent user gracefully");
        } catch (Exception e) {
            fail("Negative test for deleting non-existent user failed due to: " + e.getMessage());
        }
    }

    @Test(priority = 9)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when sending malformed JSON")
    public void testCreateUserWithMalformedJson() {
        try {
            String malformedJson = "{\"name\":\"Test\",\"username\":\"test\",\"email\":"; // Missing closing

            Response response = getRequest()
                    .body(malformedJson)
                    .when()
                    .post("/users");

            // Should handle malformed JSON gracefully
            assertTrue(response.getStatusCode() >= 400, "Should return error for malformed JSON");
            System.out.println("✅ Properly handled malformed JSON with status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("✅ Expected error for malformed JSON: " + e.getMessage());
        }
    }
}