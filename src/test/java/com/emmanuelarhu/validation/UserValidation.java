package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.users;
import static org.testng.Assert.*;

/**
 * Validation utilities for User API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class UserValidation {

    /**
     * Validate an array of users
     */
    public static void validateUserArray(users[] users) {
        assertNotNull(users, "Users array should not be null");
        assertTrue(users.length > 0, "Users array should not be empty");

        for (users user : users) {
            validateBasicUserFields(user);
        }
    }

    /**
     * Validate a single user by ID
     */
    public static void validateSingleUser(users user, int expectedId) {
        assertNotNull(user, "User should not be null");
        assertEquals(user.getId().intValue(), expectedId, "User ID should match expected");
        validateBasicUserFields(user);
    }

    /**
     * Validate a created user
     */
    public static void validateCreatedUser(users createdUser, String expectedName, String expectedUsername, String expectedEmail) {
        assertNotNull(createdUser, "Created user should not be null");
        assertNotNull(createdUser.getId(), "Created user should have an ID");
        assertEquals(createdUser.getName(), expectedName, "Created user should have correct name");
        assertEquals(createdUser.getUsername(), expectedUsername, "Created user should have correct username");
        assertEquals(createdUser.getEmail(), expectedEmail, "Created user should have correct email");
    }

    /**
     * Validate an updated user
     */
    public static void validateUpdatedUser(users updatedUser, int expectedId, String expectedName, String expectedUsername, String expectedEmail) {
        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals(updatedUser.getId().intValue(), expectedId, "Updated user should have correct ID");
        assertEquals(updatedUser.getName(), expectedName, "Updated user should have correct name");
        assertEquals(updatedUser.getUsername(), expectedUsername, "Updated user should have correct username");
        assertEquals(updatedUser.getEmail(), expectedEmail, "Updated user should have correct email");
    }

    /**
     * Validate basic user fields
     */
    private static void validateBasicUserFields(users user) {
        assertNotNull(user.getId(), "User should have an ID");
        assertNotNull(user.getName(), "User should have a name");
        assertNotNull(user.getUsername(), "User should have a username");
        assertNotNull(user.getEmail(), "User should have an email");

        assertFalse(user.getName().isEmpty(), "User name should not be empty");
        assertFalse(user.getUsername().isEmpty(), "User username should not be empty");
        assertTrue(user.getEmail().contains("@"), "User should have valid email format");
    }

    /**
     * Validate email format more strictly
     */
    public static void validateEmailFormat(String email) {
        if (email != null) {
            assertTrue(email.contains("@"), "Email should contain @ symbol");
            assertTrue(email.contains("."), "Email should contain domain extension");
            assertFalse(email.startsWith("@"), "Email should not start with @");
            assertFalse(email.endsWith("@"), "Email should not end with @");
        }
    }

    /**
     * Validate user ID range (for JSONPlaceholder, valid IDs are 1-10)
     */
    public static void validateUserIdRange(Integer userId) {
        if (userId != null) {
            assertTrue(userId >= 1 && userId <= 10, "User ID should be between 1 and 10 for JSONPlaceholder");
        }
    }
}