package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.todos;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for Todos API
 *
 * @author Emmanuel Arhu
 */
@Feature("Todos API")
public class TodosTest extends BaseTest {

    @Test
    @DisplayName("GET /todos - Should return all 200 todos")
    @Description("Verify that we can retrieve all todos and get exactly 200 todos")
    public void testGetAllTodos() {
        Response response = getRequest()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("$", hasSize(200))
                .body("[0].id", notNullValue())
                .body("[0].userId", notNullValue())
                .body("[0].title", not(emptyString()))
                .body("[0].completed", anyOf(equalTo(true), equalTo(false)))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to todos objects and verify
        todos[] todos = response.as(todos[].class);
        assertEquals(200, todos.length, "Should have exactly 200 todos");

        todos firstTodo = todos[0];
        assertNotNull(firstTodo.getId(), "todos should have an ID");
        assertNotNull(firstTodo.getUserId(), "todos should have a userId");
        assertFalse(firstTodo.getTitle().isEmpty(), "todos should have a title");
        assertNotNull(firstTodo.getCompleted(), "todos should have a completed status");

        System.out.println("✅ Successfully retrieved " + todos.length + " todos");
    }

    @Test
    @DisplayName("GET /todos/1 - Should return specific todo")
    @Description("Verify that we can retrieve a specific todo by ID")
    public void testGetSingleTodo() {
        Response response = getRequest()
                .when()
                .get("/todos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", notNullValue())
                .body("title", not(emptyString()))
                .body("completed", anyOf(equalTo(true), equalTo(false)))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to todos object and verify
        todos todo = response.as(todos.class);
        assertEquals(1, todo.getId(), "todos ID should be 1");
        assertNotNull(todo.getUserId(), "todos should have a userId");
        assertFalse(todo.getTitle().isEmpty(), "todos should have a title");
        assertNotNull(todo.getCompleted(), "todos should have completion status");

        System.out.println("✅ Successfully retrieved todo: " + todo.getId() + " (completed: " + todo.getCompleted() + ")");
    }

    @Test
    @DisplayName("POST /todos - Should create a new todo")
    @Description("Verify that we can create a new todo")
    public void testCreateNewTodo() {
        todos newTodo = new todos(1, "Test todos", false);

        Response response = getRequest()
                .body(newTodo)
                .when()
                .post("/todos")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(1))
                .body("title", equalTo("Test todos"))
                .body("completed", equalTo(false))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to todos object and verify
        todos createdTodo = response.as(todos.class);
        assertNotNull(createdTodo.getId(), "Created todo should have an ID");
        assertEquals(1, createdTodo.getUserId(), "Created todo should have correct userId");
        assertEquals("Test todos", createdTodo.getTitle(), "Created todo should have correct title");
        assertEquals(false, createdTodo.getCompleted(), "Created todo should have correct completion status");

        System.out.println("✅ Successfully created new todo with ID: " + createdTodo.getId());
    }

    @Test
    @DisplayName("PUT /todos/1 - Should update existing todo")
    @Description("Verify that we can completely update an existing todo")
    public void testUpdateTodo() {
        todos updatedTodo = new todos(1, "Updated todos", true);
        updatedTodo.setId(1);

        Response response = getRequest()
                .body(updatedTodo)
                .when()
                .put("/todos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", equalTo("Updated todos"))
                .body("completed", equalTo(true))
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully updated todo 1");
    }

    @Test
    @DisplayName("PATCH /todos/1 - Should partially update todo")
    @Description("Verify that we can partially update a todo (toggle completion)")
    public void testPatchTodo() {
        String patchBody = "{\"completed\": true}";

        Response response = getRequest()
                .body(patchBody)
                .when()
                .patch("/todos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("completed", equalTo(true))
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully patched todo 1 completion status");
    }

    @Test
    @DisplayName("DELETE /todos/1 - Should delete existing todo")
    @Description("Verify that we can delete an existing todo")
    public void testDeleteTodo() {
        Response response = getRequest()
                .when()
                .delete("/todos/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted todo 1");
    }

    @Test
    @DisplayName("GET /todos?completed=true - Should filter completed todos")
    @Description("Verify that we can filter todos by completion status")
    public void testFilterCompletedTodos() {
        Response response = getRequest()
                .queryParam("completed", true)
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("completed", everyItem(equalTo(true)))
                .extract().response();

        verifyResponseTime(response.getTime());

        todos[] completedTodos = response.as(todos[].class);
        assertTrue(completedTodos.length > 0, "Should have completed todos");

        for (todos todo : completedTodos) {
            assertTrue(todo.getCompleted(), "All todos should be completed");
        }

        System.out.println("✅ Successfully filtered " + completedTodos.length + " completed todos");
    }

    @Test
    @DisplayName("GET /todos?userId=1 - Should filter todos by user")
    @Description("Verify that we can filter todos by user ID")
    public void testFilterTodosByUser() {
        Response response = getRequest()
                .queryParam("userId", 1)
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("userId", everyItem(equalTo(1)))
                .extract().response();

        verifyResponseTime(response.getTime());

        todos[] userTodos = response.as(todos[].class);
        assertTrue(userTodos.length > 0, "Should have todos for user 1");

        for (todos todo : userTodos) {
            assertEquals(1, todo.getUserId(), "All todos should belong to user 1");
        }

        System.out.println("✅ Successfully filtered " + userTodos.length + " todos by userId=1");
    }
}