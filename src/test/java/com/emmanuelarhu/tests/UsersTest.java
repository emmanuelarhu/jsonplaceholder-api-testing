package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.users;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for Users API
 *
 * @author Emmanuel Arhu
 */
@Feature("Users API")
public class UsersTest extends BaseTest {

    @Test
    @DisplayName("GET /users - Should return all 10 users")
    @Description("Verify that we can retrieve all users and get exactly 10 users")
    public void testGetAllUsers() {
        Response response = getRequest()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(10))
                .body("[0].id", notNullValue())
                .body("[0].name", not(emptyString()))
                .body("[0].username", not(emptyString()))
                .body("[0].email", containsString("@"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to users objects and verify
        users[] users = response.as(users[].class);
        assertEquals(10, users.length, "Should have exactly 10 users");

        for (users user : users) {
            assertNotNull(user.getId(), "users should have an ID");
            assertFalse(user.getName().isEmpty(), "users should have a name");
            assertFalse(user.getUsername().isEmpty(), "users should have a username");
            assertTrue(user.getEmail().contains("@"), "users should have valid email");
        }

        System.out.println("✅ Successfully retrieved " + users.length + " users");
    }

    @Test
    @DisplayName("GET /users/1 - Should return specific user")
    @Description("Verify that we can retrieve a specific user by ID")
    public void testGetSingleUser() {
        Response response = getRequest()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", not(emptyString()))
                .body("username", not(emptyString()))
                .body("email", containsString("@"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to users object and verify
        users user = response.as(users.class);
        assertEquals(1, user.getId(), "users ID should be 1");
        assertFalse(user.getName().isEmpty(), "users should have a name");
        assertFalse(user.getUsername().isEmpty(), "users should have a username");
        assertTrue(user.getEmail().contains("@"), "users should have valid email");

        System.out.println("✅ Successfully retrieved user: " + user);
    }

    @Test
    @DisplayName("POST /users - Should create a new user successfully with id 11")
    @Description("Verify that we can create a new user")
    public void testCreateNewUser() {
        users newUser = new users();
        newUser.setName("Emmanuel Arhu");
        newUser.setUsername("Example");
        newUser.setEmail("emmanuel.arhu@amalitechtraining.org");

        Response response = getRequest()
                .body(newUser)
                .when()
                .post("/users")
                .then().log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Emmanuel Arhu"))
                .body("username", equalTo("Example"))
                .body("email", equalTo("emmanuel.arhu@amalitechtraining.org"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to users object and verify
        users createdUser = response.as(users.class);
        assertNotNull(createdUser.getId(), "Created user should have an ID");
        assertEquals("Emmanuel Arhu", createdUser.getName(), "Created user should have correct name");
        assertEquals("Example", createdUser.getUsername(), "Created user should have correct username");
        assertEquals("emmanuel.arhu@amalitechtraining.org", createdUser.getEmail(), "Created user should have correct email");

        System.out.println("✅ Successfully created new user with ID: " + createdUser.getId());
    }

    @Test
    @DisplayName("PUT /users/1 - Should update existing user")
    @Description("Verify that we can completely update an existing user")
    public void testUpdateUser() {
        users updatedUser = new users();
        updatedUser.setId(1);
        updatedUser.setName("Emmanuel Arhu");
        updatedUser.setUsername("Example");
        updatedUser.setEmail("emmanuel.arhu@amalitechtraining.org");


        Response response = getRequest()
                .body(updatedUser)
                .when()
                .put("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Emmanuel Arhu"))
                .body("username", equalTo("Example"))
                .body("email", equalTo("emmanuel.arhu@amalitechtraining.org"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to users object and verify
        users returnedUser = response.as(users.class);
        assertEquals(1, returnedUser.getId(), "Updated user should have correct ID");
        assertEquals("Emmanuel Arhu", returnedUser.getName(), "Updated user should have new name");
        assertEquals("Example", returnedUser.getUsername(), "Updated user should have new username");
        assertEquals("emmanuel.arhu@amalitechtraining.org", returnedUser.getEmail(), "Updated user should have new email");

        System.out.println("✅ Successfully updated user: " + returnedUser);
    }

    @Test
    @DisplayName("DELETE /users/1 - Should delete existing user")
    @Description("Verify that we can delete an existing user")
    public void testDeleteUser() {
        Response response = getRequest()
                .when()
                .delete("/users/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted user 1");
    }

    @Test
    @DisplayName("GET /users/999 - Should return 404 for non-existent user")
    @Description("Verify that requesting a non-existent user returns 404")
    public void testGetNonExistentUser() {
        Response response = getRequest()
                .when()
                .get("/users/999")
                .then()
                .statusCode(404)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Correctly returned 404 for non-existent user");
    }
}