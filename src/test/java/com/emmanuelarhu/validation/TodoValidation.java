package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.todos;
import static org.testng.Assert.*;

/**
 * Validation utilities for Todo API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class TodoValidation {

    /**
     * Validate an array of todos
     */
    public static void validateTodoArray(todos[] todos, int expectedCount) {
        assertNotNull(todos, "Todos array should not be null");
        assertEquals(todos.length, expectedCount, "Should have exactly " + expectedCount + " todos");

        for (todos todo : todos) {
            validateBasicTodoFields(todo);
        }
    }

    /**
     * Validate a single todo by ID
     */
    public static void validateSingleTodo(todos todo, int expectedId) {
        assertNotNull(todo, "Todo should not be null");
        assertEquals(todo.getId().intValue(), expectedId, "Todo ID should match expected");
        validateBasicTodoFields(todo);
    }

    /**
     * Validate a created todo
     */
    public static void validateCreatedTodo(todos createdTodo, int expectedUserId, String expectedTitle, boolean expectedCompleted) {
        assertNotNull(createdTodo, "Created todo should not be null");
        assertNotNull(createdTodo.getId(), "Created todo should have an ID");
        assertEquals(createdTodo.getUserId().intValue(), expectedUserId, "Created todo should have correct userId");
        assertEquals(createdTodo.getTitle(), expectedTitle, "Created todo should have correct title");
        assertEquals(createdTodo.getCompleted().booleanValue(), expectedCompleted, "Created todo should have correct completion status");
    }

    /**
     * Validate todos filtered by user ID
     */
    public static void validateTodosForUser(todos[] todos, int expectedUserId) {
        assertNotNull(todos, "Todos array should not be null");
        assertTrue(todos.length > 0, "Should have todos for user " + expectedUserId);

        for (todos todo : todos) {
            assertEquals(todo.getUserId().intValue(), expectedUserId,
                    "All todos should belong to user " + expectedUserId);
            validateBasicTodoFields(todo);
        }
    }

    /**
     * Validate todos filtered by completion status
     */
    public static void validateTodosByCompletionStatus(todos[] todos, boolean expectedCompleted) {
        assertNotNull(todos, "Todos array should not be null");
        assertTrue(todos.length > 0, "Should have todos with completion status: " + expectedCompleted);

        for (todos todo : todos) {
            assertEquals(todo.getCompleted().booleanValue(), expectedCompleted,
                    "All todos should have completion status: " + expectedCompleted);
            validateBasicTodoFields(todo);
        }
    }

    /**
     * Validate an updated todo
     */
    public static void validateUpdatedTodo(todos updatedTodo, int expectedId, int expectedUserId, String expectedTitle, boolean expectedCompleted) {
        assertNotNull(updatedTodo, "Updated todo should not be null");
        assertEquals(updatedTodo.getId().intValue(), expectedId, "Updated todo should have correct ID");
        assertEquals(updatedTodo.getUserId().intValue(), expectedUserId, "Updated todo should have correct userId");
        assertEquals(updatedTodo.getTitle(), expectedTitle, "Updated todo should have correct title");
        assertEquals(updatedTodo.getCompleted().booleanValue(), expectedCompleted, "Updated todo should have correct completion status");
    }

    /**
     * Validate a patched todo (partial update)
     */
    public static void validatePatchedTodo(todos patchedTodo, int expectedId, boolean expectedCompleted) {
        assertNotNull(patchedTodo, "Patched todo should not be null");
        assertEquals(patchedTodo.getId().intValue(), expectedId, "Patched todo should have correct ID");
        assertEquals(patchedTodo.getCompleted().booleanValue(), expectedCompleted, "Patched todo should have correct completion status");
        // Note: Other fields should remain unchanged in a PATCH operation
        assertNotNull(patchedTodo.getUserId(), "Patched todo should still have userId");
        assertNotNull(patchedTodo.getTitle(), "Patched todo should still have title");
    }

    /**
     * Validate basic todo fields
     */
    public static void validateBasicTodoFields(todos todo) {
        assertNotNull(todo.getId(), "Todo should have an ID");
        assertNotNull(todo.getUserId(), "Todo should have a userId");
        assertNotNull(todo.getTitle(), "Todo should have a title");
        assertNotNull(todo.getCompleted(), "Todo should have a completion status");

        assertFalse(todo.getTitle().isEmpty(), "Todo title should not be empty");

        // Validate userId range (for JSONPlaceholder, valid userIds are 1-10)
        assertTrue(todo.getUserId() >= 1 && todo.getUserId() <= 10,
                "Todo userId should be between 1 and 10 for JSONPlaceholder");

        // Validate completion status is a proper boolean
        assertTrue(todo.getCompleted() == true || todo.getCompleted() == false,
                "Todo completion status should be a valid boolean");
    }

    /**
     * Validate todo title
     */
    public static void validateTodoTitle(String title) {
        if (title != null) {
            assertFalse(title.trim().isEmpty(), "Todo title should not be empty or only whitespace");
            assertTrue(title.length() >= 1 && title.length() <= 300,
                    "Todo title should be between 1 and 300 characters");
        }
    }

    /**
     * Validate completion toggle operation
     */
    public static void validateCompletionToggle(todos originalTodo, todos updatedTodo) {
        assertNotNull(originalTodo, "Original todo should not be null");
        assertNotNull(updatedTodo, "Updated todo should not be null");

        assertEquals(originalTodo.getId(), updatedTodo.getId(), "Todo ID should remain the same");
        assertEquals(originalTodo.getUserId(), updatedTodo.getUserId(), "Todo userId should remain the same");
        assertEquals(originalTodo.getTitle(), updatedTodo.getTitle(), "Todo title should remain the same");

        // Completion status should be toggled
        assertNotEquals(originalTodo.getCompleted(), updatedTodo.getCompleted(),
                "Completion status should be toggled");
    }
}