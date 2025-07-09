package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.photos;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for Photos API
 *
 * @author Emmanuel Arhu
 */
@Feature("Photos API")
public class PhotosTest extends BaseTest {

    @Test
    @DisplayName("GET /photos - Should return all 5000 photos")
    @Description("Verify that we can retrieve all photos and get exactly 5000 photos")
    public void testGetAllPhotos() {
        Response response = getRequest()
                .when()
                .get("/photos")
                .then()
                .statusCode(200)
                .body("$", hasSize(5000))
                .body("[0].id", notNullValue())
                .body("[0].albumId", notNullValue())
                .body("[0].title", not(emptyString()))
                .body("[0].url", not(emptyString()))
                .body("[0].thumbnailUrl", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to photos objects and verify
        photos[] photos = response.as(photos[].class);
        assertEquals(5000, photos.length, "Should have exactly 5000 photos");

        photos firstPhoto = photos[0];
        assertNotNull(firstPhoto.getId(), "photos should have an ID");
        assertNotNull(firstPhoto.getAlbumId(), "photos should have an albumId");
        assertFalse(firstPhoto.getTitle().isEmpty(), "photos should have a title");
        assertFalse(firstPhoto.getUrl().isEmpty(), "photos should have a URL");
        assertFalse(firstPhoto.getThumbnailUrl().isEmpty(), "photos should have a thumbnail URL");

        System.out.println("✅ Successfully retrieved " + photos.length + " photos");
    }

    @Test
    @DisplayName("GET /photos/1 - Should return specific photo")
    @Description("Verify that we can retrieve a specific photo by ID")
    public void testGetSinglePhoto() {
        Response response = getRequest()
                .when()
                .get("/photos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("albumId", notNullValue())
                .body("title", not(emptyString()))
                .body("url", not(emptyString()))
                .body("thumbnailUrl", not(emptyString()))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to photos object and verify
        photos photo = response.as(photos.class);
        assertEquals(1, photo.getId(), "photos ID should be 1");
        assertNotNull(photo.getAlbumId(), "photos should have an albumId");
        assertFalse(photo.getTitle().isEmpty(), "photos should have a title");
        assertFalse(photo.getUrl().isEmpty(), "photos should have a URL");

        System.out.println("✅ Successfully retrieved photo: " + photo.getId());
    }

    @Test
    @DisplayName("POST /photos - Should create a new photo")
    @Description("Verify that we can create a new photo")
    public void testCreateNewPhoto() {
        photos newPhoto = new photos(1, "Test photos", "https://test.com/photo.jpg", "https://test.com/thumb.jpg");

        Response response = getRequest()
                .body(newPhoto)
                .when()
                .post("/photos")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("albumId", equalTo(1))
                .body("title", equalTo("Test photos"))
                .body("url", equalTo("https://test.com/photo.jpg"))
                .body("thumbnailUrl", equalTo("https://test.com/thumb.jpg"))
                .extract().response();

        verifyResponseTime(response.getTime());

        // Convert to photos object and verify
        photos createdPhoto = response.as(photos.class);
        assertNotNull(createdPhoto.getId(), "Created photo should have an ID");
        assertEquals(1, createdPhoto.getAlbumId(), "Created photo should have correct albumId");
        assertEquals("Test photos", createdPhoto.getTitle(), "Created photo should have correct title");

        System.out.println("✅ Successfully created new photo with ID: " + createdPhoto.getId());
    }

    @Test
    @DisplayName("PUT /photos/1 - Should update existing photo")
    @Description("Verify that we can completely update an existing photo")
    public void testUpdatePhoto() {
        photos updatedPhoto = new photos(1, "Updated photos", "https://updated.com/photo.jpg", "https://updated.com/thumb.jpg");
        updatedPhoto.setId(1);

        Response response = getRequest()
                .body(updatedPhoto)
                .when()
                .put("/photos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("albumId", equalTo(1))
                .body("title", equalTo("Updated photos"))
                .body("url", equalTo("https://updated.com/photo.jpg"))
                .body("thumbnailUrl", equalTo("https://updated.com/thumb.jpg"))
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully updated photo 1");
    }

    @Test
    @DisplayName("DELETE /photos/1 - Should delete existing photo")
    @Description("Verify that we can delete an existing photo")
    public void testDeletePhoto() {
        Response response = getRequest()
                .when()
                .delete("/photos/1")
                .then()
                .statusCode(200)
                .extract().response();

        verifyResponseTime(response.getTime());

        System.out.println("✅ Successfully deleted photo 1");
    }

    @Test
    @DisplayName("GET /photos?albumId=1 - Should filter photos by album")
    @Description("Verify that we can filter photos by album ID")
    public void testFilterPhotosByAlbum() {
        Response response = getRequest()
                .queryParam("albumId", 1)
                .when()
                .get("/photos")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("albumId", everyItem(equalTo(1)))
                .extract().response();

        verifyResponseTime(response.getTime());

        photos[] photos = response.as(photos[].class);
        assertTrue(photos.length > 0, "Should have photos for album 1");

        for (photos photo : photos) {
            assertEquals(1, photo.getAlbumId(), "All photos should belong to album 1");
        }

        System.out.println("✅ Successfully filtered " + photos.length + " photos by albumId=1");
    }
}