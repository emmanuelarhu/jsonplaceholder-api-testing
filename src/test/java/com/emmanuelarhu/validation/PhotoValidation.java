package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.photos;
import static org.testng.Assert.*;

/**
 * Validation utilities for Photo API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class PhotoValidation {

    /**
     * Validate an array of photos
     */
    public static void validatePhotoArray(photos[] photos, int expectedCount) {
        assertNotNull(photos, "Photos array should not be null");
        assertEquals(photos.length, expectedCount, "Should have exactly " + expectedCount + " photos");

        for (photos photo : photos) {
            validateBasicPhotoFields(photo);
        }
    }

    /**
     * Validate a single photo by ID
     */
    public static void validateSinglePhoto(photos photo, int expectedId) {
        assertNotNull(photo, "Photo should not be null");
        assertEquals(photo.getId().intValue(), expectedId, "Photo ID should match expected");
        validateBasicPhotoFields(photo);
    }

    /**
     * Validate a created photo
     */
    public static void validateCreatedPhoto(photos createdPhoto, int expectedAlbumId, String expectedTitle, String expectedUrl, String expectedThumbnailUrl) {
        assertNotNull(createdPhoto, "Created photo should not be null");
        assertNotNull(createdPhoto.getId(), "Created photo should have an ID");
        assertEquals(createdPhoto.getAlbumId().intValue(), expectedAlbumId, "Created photo should have correct albumId");
        assertEquals(createdPhoto.getTitle(), expectedTitle, "Created photo should have correct title");
        assertEquals(createdPhoto.getUrl(), expectedUrl, "Created photo should have correct URL");
        assertEquals(createdPhoto.getThumbnailUrl(), expectedThumbnailUrl, "Created photo should have correct thumbnail URL");
    }

    /**
     * Validate photos filtered by album ID
     */
    public static void validatePhotosForAlbum(photos[] photos, int expectedAlbumId) {
        assertNotNull(photos, "Photos array should not be null");
        assertTrue(photos.length > 0, "Should have photos for album " + expectedAlbumId);

        for (photos photo : photos) {
            assertEquals(photo.getAlbumId().intValue(), expectedAlbumId,
                    "All photos should belong to album " + expectedAlbumId);
            validateBasicPhotoFields(photo);
        }
    }

    /**
     * Validate an updated photo
     */
    public static void validateUpdatedPhoto(photos updatedPhoto, int expectedId, int expectedAlbumId, String expectedTitle, String expectedUrl, String expectedThumbnailUrl) {
        assertNotNull(updatedPhoto, "Updated photo should not be null");
        assertEquals(updatedPhoto.getId().intValue(), expectedId, "Updated photo should have correct ID");
        assertEquals(updatedPhoto.getAlbumId().intValue(), expectedAlbumId, "Updated photo should have correct albumId");
        assertEquals(updatedPhoto.getTitle(), expectedTitle, "Updated photo should have correct title");
        assertEquals(updatedPhoto.getUrl(), expectedUrl, "Updated photo should have correct URL");
        assertEquals(updatedPhoto.getThumbnailUrl(), expectedThumbnailUrl, "Updated photo should have correct thumbnail URL");
    }

    /**
     * Validate basic photo fields
     */
    public static void validateBasicPhotoFields(photos photo) {
        assertNotNull(photo.getId(), "Photo should have an ID");
        assertNotNull(photo.getAlbumId(), "Photo should have an albumId");
        assertNotNull(photo.getTitle(), "Photo should have a title");
        assertNotNull(photo.getUrl(), "Photo should have a URL");
        assertNotNull(photo.getThumbnailUrl(), "Photo should have a thumbnail URL");

        assertFalse(photo.getTitle().isEmpty(), "Photo title should not be empty");
        assertFalse(photo.getUrl().isEmpty(), "Photo URL should not be empty");
        assertFalse(photo.getThumbnailUrl().isEmpty(), "Photo thumbnail URL should not be empty");

        // Validate albumId range (for JSONPlaceholder, valid albumIds are 1-100)
        assertTrue(photo.getAlbumId() >= 1 && photo.getAlbumId() <= 100,
                "Photo albumId should be between 1 and 100 for JSONPlaceholder");
    }

    /**
     * Validate URL format for photos
     */
    public static void validatePhotoUrl(String url) {
        if (url != null) {
            assertFalse(url.trim().isEmpty(), "Photo URL should not be empty");
            assertTrue(url.startsWith("http://") || url.startsWith("https://"),
                    "Photo URL should start with http:// or https://");
            assertTrue(url.length() > 10, "Photo URL should be at least 10 characters long");
        }
    }

    /**
     * Validate photo title
     */
    public static void validatePhotoTitle(String title) {
        if (title != null) {
            assertFalse(title.trim().isEmpty(), "Photo title should not be empty or only whitespace");
            assertTrue(title.length() >= 1 && title.length() <= 500,
                    "Photo title should be between 1 and 500 characters");
        }
    }
}