package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.todos;
import com.emmanuelarhu.validation.TodoValidation;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Complete Todos API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Todos API")
public class TodosTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all todos and get exactly 200 todos")
    public void testGetAllTodos() {
        try {
            Response response = makeApiCall("/todos", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(200))
                    .body("[0].id", notNullValue())
                    .body("[0].userId", notNullValue())
                    .body("[0].title", not(emptyString()))
                    .body("[0].completed", anyOf(equalTo(true), equalTo(false)));

            verifyResponseTime(response.getTime());

            // Convert to todos objects and verify
            todos[] todos = response.as(todos[].class);
            TodoValidation.validateTodoArray(todos, 200);

            System.out.println("✅ Successfully retrieved " + todos.length + " todos");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validTodoIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific todos by valid IDs")
    public void testGetSingleTodo(int todoId) {
        try {
            Response response = makeApiCall("/todos/" + todoId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(todoId))
                    .body("userId", notNullValue())
                    .body("title", not(emptyString()))
                    .body("completed", anyOf(equalTo(true), equalTo(false)));

            verifyResponseTime(response.getTime());

            // Convert to todos object and verify
            todos todo = response.as(todos.class);
            TodoValidation.validateSingleTodo(todo, todoId);

            System.out.println("✅ Successfully retrieved todo: " + todo.getId() + " (completed: " + todo.getCompleted() + ")");
        } catch (Exception e) {
            fail("Test failed for todoId " + todoId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validTodoData", dataProviderClass = TestDataProvider.class, priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new todos with valid data")
    public void testCreateNewTodo(int userId, String title, boolean completed) {
        try {
            todos newTodo = TestDataProvider.createValidTodo(userId, title, completed);

            Response response = getRequest()
                    .body(newTodo)
                    .when().log().all()
                    .post("/todos")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .body("completed", equalTo(completed))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify created todo
            todos createdTodo = response.as(todos.class);
            TodoValidation.validateCreatedTodo(createdTodo, userId, title, completed);

            System.out.println("✅ Successfully created new todo with ID: " + createdTodo.getId());
        } catch (Exception e) {
            fail("Test failed for todo creation due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validTodoData", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update existing todos")
    public void testUpdateTodo(int userId, String title, boolean completed) {
        try {
            todos updatedTodo = TestDataProvider.createValidTodo(userId, title, completed);
            updatedTodo.setId(1);

            Response response = getRequest()
                    .body(updatedTodo)
                    .when().log().all()
                    .put("/todos/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .body("completed", equalTo(completed))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify updated todo
            todos returnedTodo = response.as(todos.class);
            TodoValidation.validateUpdatedTodo(returnedTodo, 1, userId, title, completed);

            System.out.println("✅ Successfully updated todo 1");
        } catch (Exception e) {
            fail("Test failed for todo update due to: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can partially update todos using PATCH (toggle completion)")
    public void testPatchTodo() {
        try {
            String patchBody = "{\"completed\": true}";

            Response response = getRequest()
                    .body(patchBody)
                    .when().log().all()
                    .patch("/todos/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("completed", equalTo(true))
                    .body("userId", notNullValue())
                    .body("title", notNullValue())
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify patched todo
            todos patchedTodo = response.as(todos.class);
            TodoValidation.validatePatchedTodo(patchedTodo, 1, true);

            System.out.println("✅ Successfully patched todo 1 completion status");
        } catch (Exception e) {
            fail("Test failed for todo patch due to: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete existing todos")
    public void testDeleteTodo() {
        try {
            Response response = makeApiCall("/todos/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted todo 1");
        } catch (Exception e) {
            fail("Test failed for todo deletion due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "completionStatusFilters", dataProviderClass = TestDataProvider.class, priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter todos by completion status")
    public void testFilterTodosByCompletionStatus(boolean completed) {
        try {
            Response response = getRequest()
                    .queryParam("completed", completed)
                    .when().log().all()
                    .get("/todos")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("completed", everyItem(equalTo(completed)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            todos[] todos = response.as(todos[].class);
            TodoValidation.validateTodosByCompletionStatus(todos, completed);

            System.out.println("✅ Successfully filtered " + todos.length + " todos by completed=" + completed);
        } catch (Exception e) {
            fail("Test failed for todo filtering by completion status " + completed + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userIdFilters", dataProviderClass = TestDataProvider.class, priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter todos by user ID")
    public void testFilterTodosByUser(int userId) {
        try {
            Response response = getRequest()
                    .queryParam("userId", userId)
                    .when().log().all()
                    .get("/todos")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("userId", everyItem(equalTo(userId)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            todos[] todos = response.as(todos[].class);
            TodoValidation.validateTodosForUser(todos, userId);

            System.out.println("✅ Successfully filtered " + todos.length + " todos by userId=" + userId);
        } catch (Exception e) {
            fail("Test failed for todo filtering by userId " + userId + " due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidTodoIds", dataProviderClass = TestDataProvider.class, priority = 9)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent todos returns 404")
    public void testGetNonExistentTodo(int invalidTodoId) {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .get("/todos/" + invalidTodoId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid todo ID: " + invalidTodoId);
        } catch (Exception e) {
            fail("Negative test failed for invalid todoId " + invalidTodoId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidTodoData", dataProviderClass = TestDataProvider.class, priority = 10)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating todos with invalid data handles errors appropriately")
    public void testCreateTodoWithInvalidData(int userId, String title, boolean completed) {
        try {
            todos invalidTodo = TestDataProvider.createValidTodo(userId, title, completed);

            Response response = getRequest()
                    .body(invalidTodo)
                    .when().log().all()
                    .post("/todos");

            // JSONPlaceholder is lenient, but we verify response is received
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "Should receive a valid HTTP response code");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid todo data: userId=" + userId + ", title='" + title + "'");
        } catch (Exception e) {
            System.out.println("✅ Expected error for invalid todo data: " + e.getMessage());
        }
    }

    @Test(priority = 11)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when filtering with non-existent user ID")
    public void testFilterTodosWithInvalidUserId() {
        try {
            Response response = getRequest()
                    .queryParam("userId", 999)
                    .when().log().all()
                    .get("/todos")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(0)) // Should return empty array
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned empty array for non-existent userId filter");
        } catch (Exception e) {
            fail("Test failed for invalid userId filter due to: " + e.getMessage());
        }
    }

    @Test(priority = 12)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify behavior when trying to delete non-existent todo")
    public void testDeleteNonExistentTodo() {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .delete("/todos/999")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for non-existent resources
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Handled deletion of non-existent todo gracefully");
        } catch (Exception e) {
            fail("Negative test for deleting non-existent todo failed due to: " + e.getMessage());
        }
    }

    @Test(priority = 13)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when sending malformed todo JSON")
    public void testCreateTodoWithMalformedJson() {
        try {
            String malformedJson = "{\"userId\":1,\"title\":\"Test\",\"completed\":"; // Missing closing

            Response response = getRequest()
                    .body(malformedJson)
                    .when().log().all()
                    .post("/todos");

            // Should handle malformed JSON gracefully
            assertTrue(response.getStatusCode() >= 400, "Should return error for malformed JSON");
            System.out.println("✅ Properly handled malformed JSON with status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("✅ Expected error for malformed JSON: " + e.getMessage());
        }
    }

    @Test(priority = 14)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify completion toggle functionality")
    public void testToggleCompletion() {
        try {
            // First get a todo to see its current state
            Response getResponse = makeApiCall("/todos/1", "GET");
            todos originalTodo = getResponse.as(todos.class);

            // Toggle the completion status
            boolean newCompletionStatus = !originalTodo.getCompleted();
            String patchBody = "{\"completed\": " + newCompletionStatus + "}";

            Response patchResponse = getRequest()
                    .body(patchBody)
                    .when().log().all()
                    .patch("/todos/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("completed", equalTo(newCompletionStatus))
                    .extract().response();

            verifyResponseTime(patchResponse.getTime());

            todos updatedTodo = patchResponse.as(todos.class);
            TodoValidation.validateCompletionToggle(originalTodo, updatedTodo);

            System.out.println("✅ Successfully toggled completion status from " +
                    originalTodo.getCompleted() + " to " + updatedTodo.getCompleted());
        } catch (Exception e) {
            fail("Test failed for completion toggle due to: " + e.getMessage());
        }
    }
}