package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.albums;
import com.emmanuelarhu.validation.AlbumValidation;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Complete Albums API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Albums API")
public class AlbumsTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all albums and get exactly 100 albums")
    public void testGetAllAlbums() {
        try {
            Response response = makeApiCall("/albums", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(100))
                    .body("[0].id", notNullValue())
                    .body("[0].userId", notNullValue())
                    .body("[0].title", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to albums objects and verify
            albums[] albums = response.as(albums[].class);
            AlbumValidation.validateAlbumArray(albums, 100);

            System.out.println("✅ Successfully retrieved " + albums.length + " albums");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validAlbumIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific albums by valid IDs")
    public void testGetSingleAlbum(int albumId) {
        try {
            Response response = makeApiCall("/albums/" + albumId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(albumId))
                    .body("userId", notNullValue())
                    .body("title", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to albums object and verify
            albums album = response.as(albums.class);
            AlbumValidation.validateSingleAlbum(album, albumId);

            System.out.println("✅ Successfully retrieved album: " + album.getId());
        } catch (Exception e) {
            fail("Test failed for albumId " + albumId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validAlbumData", dataProviderClass = TestDataProvider.class, priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new albums with valid data")
    public void testCreateNewAlbum(int userId, String title) {
        try {
            albums newAlbum = TestDataProvider.createValidAlbum(userId, title);

            Response response = getRequest()
                    .body(newAlbum)
                    .when().log().all()
                    .post("/albums")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify created album
            albums createdAlbum = response.as(albums.class);
            AlbumValidation.validateCreatedAlbum(createdAlbum, userId, title);

            System.out.println("✅ Successfully created new album with ID: " + createdAlbum.getId());
        } catch (Exception e) {
            fail("Test failed for album creation due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validAlbumData", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update existing albums")
    public void testUpdateAlbum(int userId, String title) {
        try {
            albums updatedAlbum = TestDataProvider.createValidAlbum(userId, title);
            updatedAlbum.setId(1);

            Response response = getRequest()
                    .body(updatedAlbum)
                    .when().log().all()
                    .put("/albums/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("userId", equalTo(userId))
                    .body("title", equalTo(title))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify updated album
            albums returnedAlbum = response.as(albums.class);
            AlbumValidation.validateUpdatedAlbum(returnedAlbum, 1, userId, title);

            System.out.println("✅ Successfully updated album 1");
        } catch (Exception e) {
            fail("Test failed for album update due to: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can partially update albums using PATCH")
    public void testPatchAlbum() {
        try {
            String patchBody = "{\"title\": \"Patched Album Title\"}";

            Response response = getRequest()
                    .body(patchBody)
                    .when().log().all()
                    .patch("/albums/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("title", equalTo("Patched Album Title"))
                    .body("userId", notNullValue())
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Successfully patched album title");
        } catch (Exception e) {
            fail("Test failed for album patch due to: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete existing albums")
    public void testDeleteAlbum() {
        try {
            Response response = makeApiCall("/albums/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted album 1");
        } catch (Exception e) {
            fail("Test failed for album deletion due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userIdFilters", dataProviderClass = TestDataProvider.class, priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter albums by user ID")
    public void testFilterAlbumsByUser(int userId) {
        try {
            Response response = getRequest()
                    .queryParam("userId", userId)
                    .when().log().all()
                    .get("/albums")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("userId", everyItem(equalTo(userId)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            albums[] albums = response.as(albums[].class);
            AlbumValidation.validateAlbumsForUser(albums, userId);

            System.out.println("✅ Successfully filtered " + albums.length + " albums by userId=" + userId);
        } catch (Exception e) {
            fail("Test failed for album filtering by userId " + userId + " due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidAlbumIds", dataProviderClass = TestDataProvider.class, priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent albums returns 404")
    public void testGetNonExistentAlbum(int invalidAlbumId) {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .get("/albums/" + invalidAlbumId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid album ID: " + invalidAlbumId);
        } catch (Exception e) {
            fail("Negative test failed for invalid albumId " + invalidAlbumId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidAlbumData", dataProviderClass = TestDataProvider.class, priority = 9)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating albums with invalid data handles errors appropriately")
    public void testCreateAlbumWithInvalidData(int userId, String title) {
        try {
            albums invalidAlbum = TestDataProvider.createValidAlbum(userId, title);

            Response response = getRequest()
                    .body(invalidAlbum)
                    .when().log().all()
                    .post("/albums");

            // JSONPlaceholder is lenient, but we verify response is received
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "Should receive a valid HTTP response code");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid album data: userId=" + userId + ", title='" + title + "'");
        } catch (Exception e) {
            System.out.println("✅ Expected error for invalid album data: " + e.getMessage());
        }
    }

    @Test(priority = 10)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when filtering with non-existent user ID")
    public void testFilterAlbumsWithInvalidUserId() {
        try {
            Response response = getRequest()
                    .queryParam("userId", 999)
                    .when().log().all()
                    .get("/albums")
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

    @Test(priority = 11)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify behavior when trying to delete non-existent album")
    public void testDeleteNonExistentAlbum() {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .delete("/albums/999")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for non-existent resources
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Handled deletion of non-existent album gracefully");
        } catch (Exception e) {
            fail("Negative test for deleting non-existent album failed due to: " + e.getMessage());
        }
    }
}