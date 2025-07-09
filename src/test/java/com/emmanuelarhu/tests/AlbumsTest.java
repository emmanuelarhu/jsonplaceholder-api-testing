package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.albums;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for Albums API
 *
 * @author Emmanuel Arhu
 */
@Feature("Albums API")
public class AlbumsTest extends BaseTest {

    @Test
    @DisplayName("GET /albums - Should return all 100 albums")
    @Description("Verify that we can retrieve all albums and get exactly 100 albums")
    public void testGetAllAlbums() {
        Response response = getRequest()
                .when()
                .get("/albums")
                .then()
                .statusCode(200)
                .body("$", hasSize(100))
                .body("[0].id", notNullValue())
                .body("[0].userId", notNullValue())
                .body("[0].title", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to albums objects and verify
        albums[] albums = response.as(albums[].class);
        assertEquals(100, albums.length, "Should have exactly 100 albums");

        albums firstAlbum = albums[0];
        assertNotNull(firstAlbum.getId(), "albums should have an ID");
        assertNotNull(firstAlbum.getUserId(), "albums should have a userId");
        assertFalse(firstAlbum.getTitle().isEmpty(), "albums should have a title");

        System.out.println("✅ Successfully retrieved " + albums.length + " albums");
    }

    @Test
    @DisplayName("GET /albums/1 - Should return specific album")
    @Description("Verify that we can retrieve a specific album by ID")
    public void testGetSingleAlbum() {
        Response response = getRequest()
                .when()
                .get("/albums/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", notNullValue())
                .body("title", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to albums object and verify
        albums album = response.as(albums.class);
        assertEquals(1, album.getId(), "albums ID should be 1");
        assertNotNull(album.getUserId(), "albums should have a userId");
        assertFalse(album.getTitle().isEmpty(), "albums should have a title");

        System.out.println("✅ Successfully retrieved album: " + album.getId());
    }

    @Test
    @DisplayName("POST /albums - Should create a new album")
    @Description("Verify that we can create a new album")
    public void testCreateNewAlbum() {
        albums newAlbum = new albums(1, "Test albums");

        Response response = getRequest()
                .body(newAlbum)
                .when()
                .post("/albums")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(1))
                .body("title", equalTo("Test albums"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to albums object and verify
        albums createdAlbum = response.as(albums.class);
        assertNotNull(createdAlbum.getId(), "Created album should have an ID");
        assertEquals(1, createdAlbum.getUserId(), "Created album should have correct userId");
        assertEquals("Test albums", createdAlbum.getTitle(), "Created album should have correct title");

        System.out.println("✅ Successfully created new album with ID: " + createdAlbum.getId());
    }

    @Test
    @DisplayName("PUT /albums/1 - Should update existing album")
    @Description("Verify that we can completely update an existing album")
    public void testUpdateAlbum() {
        albums updatedAlbum = new albums(1, "Updated albums");
        updatedAlbum.setId(1);

        Response response = getRequest()
                .body(updatedAlbum)
                .when()
                .put("/albums/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", equalTo("Updated albums"))
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully updated album 1");
    }

    @Test
    @DisplayName("DELETE /albums/1 - Should delete existing album")
    @Description("Verify that we can delete an existing album")
    public void testDeleteAlbum() {
        Response response = getRequest()
                .when()
                .delete("/albums/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted album 1");
    }

    @Test
    @DisplayName("GET /albums?userId=1 - Should filter albums by user")
    @Description("Verify that we can filter albums by user ID")
    public void testFilterAlbumsByUser() {
        Response response = getRequest()
                .queryParam("userId", 1)
                .when()
                .get("/albums")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("userId", everyItem(equalTo(1)))
                .extract().response();


        verifyResponseTime(response.getTime());

        albums[] albums = response.as(albums[].class);
        assertTrue(albums.length > 0, "Should have albums for user 1");

        for (albums album : albums) {
            assertEquals(1, album.getUserId(), "All albums should belong to user 1");
        }

        System.out.println("✅ Successfully filtered " + albums.length + " albums by userId=1");
    }
}