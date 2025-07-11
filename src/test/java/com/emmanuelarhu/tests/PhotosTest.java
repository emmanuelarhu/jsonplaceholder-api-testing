package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.data.TestDataProvider;
import com.emmanuelarhu.models.photos;
import com.emmanuelarhu.validation.PhotoValidation;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Complete Photos API tests with TestNG and DataProvider
 *
 * @author Emmanuel Arhu
 */
@Feature("Photos API")
public class PhotosTest extends BaseTest {

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve all photos and get exactly 5000 photos")
    public void testGetAllPhotos() {
        try {
            Response response = makeApiCall("/photos", "GET");

            response.then()
                    .statusCode(200)
                    .body("$", hasSize(5000))
                    .body("[0].id", notNullValue())
                    .body("[0].albumId", notNullValue())
                    .body("[0].title", not(emptyString()))
                    .body("[0].url", not(emptyString()))
                    .body("[0].thumbnailUrl", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to photos objects and verify
            photos[] photos = response.as(photos[].class);
            PhotoValidation.validatePhotoArray(photos, 5000);

            System.out.println("✅ Successfully retrieved " + photos.length + " photos");
        } catch (Exception e) {
            fail("Test failed due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPhotoIds", dataProviderClass = TestDataProvider.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve specific photos by valid IDs")
    public void testGetSinglePhoto(int photoId) {
        try {
            Response response = makeApiCall("/photos/" + photoId, "GET");

            response.then()
                    .statusCode(200)
                    .body("id", equalTo(photoId))
                    .body("albumId", notNullValue())
                    .body("title", not(emptyString()))
                    .body("url", not(emptyString()))
                    .body("thumbnailUrl", not(emptyString()));

            verifyResponseTime(response.getTime());

            // Convert to photos object and verify
            photos photo = response.as(photos.class);
            PhotoValidation.validateSinglePhoto(photo, photoId);

            System.out.println("✅ Successfully retrieved photo: " + photo.getId());
        } catch (Exception e) {
            fail("Test failed for photoId " + photoId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPhotoData", dataProviderClass = TestDataProvider.class, priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can create new photos with valid data")
    public void testCreateNewPhoto(int albumId, String title, String url, String thumbnailUrl) {
        try {
            photos newPhoto = TestDataProvider.createValidPhoto(albumId, title, url, thumbnailUrl);

            Response response = getRequest()
                    .body(newPhoto)
                    .when().log().all()
                    .post("/photos")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("albumId", equalTo(albumId))
                    .body("title", equalTo(title))
                    .body("url", equalTo(url))
                    .body("thumbnailUrl", equalTo(thumbnailUrl))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify created photo
            photos createdPhoto = response.as(photos.class);
            PhotoValidation.validateCreatedPhoto(createdPhoto, albumId, title, url, thumbnailUrl);

            System.out.println("✅ Successfully created new photo with ID: " + createdPhoto.getId());
        } catch (Exception e) {
            fail("Test failed for photo creation due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "validPhotoData", dataProviderClass = TestDataProvider.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update existing photos")
    public void testUpdatePhoto(int albumId, String title, String url, String thumbnailUrl) {
        try {
            photos updatedPhoto = TestDataProvider.createValidPhoto(albumId, title, url, thumbnailUrl);
            updatedPhoto.setId(1);

            Response response = getRequest()
                    .body(updatedPhoto)
                    .when().log().all()
                    .put("/photos/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("albumId", equalTo(albumId))
                    .body("title", equalTo(title))
                    .body("url", equalTo(url))
                    .body("thumbnailUrl", equalTo(thumbnailUrl))
                    .extract().response();

            verifyResponseTime(response.getTime());

            // Verify updated photo
            photos returnedPhoto = response.as(photos.class);
            PhotoValidation.validateUpdatedPhoto(returnedPhoto, 1, albumId, title, url, thumbnailUrl);

            System.out.println("✅ Successfully updated photo 1");
        } catch (Exception e) {
            fail("Test failed for photo update due to: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can partially update photos using PATCH")
    public void testPatchPhoto() {
        try {
            String patchBody = "{\"title\": \"Patched Photo Title\"}";

            Response response = getRequest()
                    .body(patchBody)
                    .when().log().all()
                    .patch("/photos/1")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(1))
                    .body("title", equalTo("Patched Photo Title"))
                    .body("albumId", notNullValue())
                    .body("url", notNullValue())
                    .body("thumbnailUrl", notNullValue())
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Successfully patched photo title");
        } catch (Exception e) {
            fail("Test failed for photo patch due to: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete existing photos")
    public void testDeletePhoto() {
        try {
            Response response = makeApiCall("/photos/1", "DELETE");

            response.then().statusCode(200);
            verifyResponseTime(response.getTime());

            System.out.println("✅ Successfully deleted photo 1");
        } catch (Exception e) {
            fail("Test failed for photo deletion due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "userIdFilters", dataProviderClass = TestDataProvider.class, priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can filter photos by album ID")
    public void testFilterPhotosByAlbum(int albumId) {
        try {
            Response response = getRequest()
                    .queryParam("albumId", albumId)
                    .when().log().all()
                    .get("/photos")
                    .then()
                    .statusCode(200)
                    .body("$", not(empty()))
                    .body("albumId", everyItem(equalTo(albumId)))
                    .extract().response();

            verifyResponseTime(response.getTime());

            photos[] photos = response.as(photos[].class);
            PhotoValidation.validatePhotosForAlbum(photos, albumId);

            System.out.println("✅ Successfully filtered " + photos.length + " photos by albumId=" + albumId);
        } catch (Exception e) {
            fail("Test failed for photo filtering by albumId " + albumId + " due to: " + e.getMessage());
        }
    }

    // NEGATIVE TEST CASES
    @Test(dataProvider = "invalidPhotoIds", dataProviderClass = TestDataProvider.class, priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that requesting non-existent photos returns 404")
    public void testGetNonExistentPhoto(int invalidPhotoId) {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .get("/photos/" + invalidPhotoId)
                    .then()
                    .statusCode(404)
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned 404 for invalid photo ID: " + invalidPhotoId);
        } catch (Exception e) {
            fail("Negative test failed for invalid photoId " + invalidPhotoId + " due to: " + e.getMessage());
        }
    }

    @Test(dataProvider = "invalidPhotoData", dataProviderClass = TestDataProvider.class, priority = 9)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating photos with invalid data handles errors appropriately")
    public void testCreatePhotoWithInvalidData(int albumId, String title, String url, String thumbnailUrl) {
        try {
            photos invalidPhoto = TestDataProvider.createValidPhoto(albumId, title, url, thumbnailUrl);

            Response response = getRequest()
                    .body(invalidPhoto)
                    .when().log().all()
                    .post("/photos");

            // JSONPlaceholder is lenient, but we verify response is received
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "Should receive a valid HTTP response code");

            verifyResponseTime(response.getTime());
            System.out.println("🔍 Tested invalid photo data: albumId=" + albumId + ", title='" + title + "'");
        } catch (Exception e) {
            System.out.println("✅ Expected error for invalid photo data: " + e.getMessage());
        }
    }

    @Test(priority = 10)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when filtering with non-existent album ID")
    public void testFilterPhotosWithInvalidAlbumId() {
        try {
            Response response = getRequest()
                    .queryParam("albumId", 999)
                    .when().log().all()
                    .get("/photos")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(0)) // Should return empty array
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Correctly returned empty array for non-existent albumId filter");
        } catch (Exception e) {
            fail("Test failed for invalid albumId filter due to: " + e.getMessage());
        }
    }

    @Test(priority = 11)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify behavior when trying to delete non-existent photo")
    public void testDeleteNonExistentPhoto() {
        try {
            Response response = getRequest()
                    .when().log().all()
                    .delete("/photos/9999")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for non-existent resources
                    .extract().response();

            verifyResponseTime(response.getTime());
            System.out.println("✅ Handled deletion of non-existent photo gracefully");
        } catch (Exception e) {
            fail("Negative test for deleting non-existent photo failed due to: " + e.getMessage());
        }
    }

    @Test(priority = 12)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify API response when sending malformed photo JSON")
    public void testCreatePhotoWithMalformedJson() {
        try {
            String malformedJson = "{\"albumId\":1,\"title\":\"Test\",\"url\":"; // Missing closing

            Response response = getRequest()
                    .body(malformedJson)
                    .when().log().all()
                    .post("/photos");

            // Should handle malformed JSON gracefully
            assertTrue(response.getStatusCode() >= 400, "Should return error for malformed JSON");
            System.out.println("✅ Properly handled malformed JSON with status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("✅ Expected error for malformed JSON: " + e.getMessage());
        }
    }
}