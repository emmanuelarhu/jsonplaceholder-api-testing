package com.emmanuelarhu.validation;

import com.emmanuelarhu.models.albums;
import static org.testng.Assert.*;

/**
 * Validation utilities for Album API responses
 * Separates validation logic from test execution
 *
 * @author Emmanuel Arhu
 */
public class AlbumValidation {

    /**
     * Validate an array of albums
     */
    public static void validateAlbumArray(albums[] albums, int expectedCount) {
        assertNotNull(albums, "Albums array should not be null");
        assertEquals(albums.length, expectedCount, "Should have exactly " + expectedCount + " albums");

        for (albums album : albums) {
            validateBasicAlbumFields(album);
        }
    }

    /**
     * Validate a single album by ID
     */
    public static void validateSingleAlbum(albums album, int expectedId) {
        assertNotNull(album, "Album should not be null");
        assertEquals(album.getId().intValue(), expectedId, "Album ID should match expected");
        validateBasicAlbumFields(album);
    }

    /**
     * Validate a created album
     */
    public static void validateCreatedAlbum(albums createdAlbum, int expectedUserId, String expectedTitle) {
        assertNotNull(createdAlbum, "Created album should not be null");
        assertNotNull(createdAlbum.getId(), "Created album should have an ID");
        assertEquals(createdAlbum.getUserId().intValue(), expectedUserId, "Created album should have correct userId");
        assertEquals(createdAlbum.getTitle(), expectedTitle, "Created album should have correct title");
    }

    /**
     * Validate albums filtered by user ID
     */
    public static void validateAlbumsForUser(albums[] albums, int expectedUserId) {
        assertNotNull(albums, "Albums array should not be null");
        assertTrue(albums.length > 0, "Should have albums for user " + expectedUserId);

        for (albums album : albums) {
            assertEquals(album.getUserId().intValue(), expectedUserId,
                    "All albums should belong to user " + expectedUserId);
            validateBasicAlbumFields(album);
        }
    }

    /**
     * Validate an updated album
     */
    public static void validateUpdatedAlbum(albums updatedAlbum, int expectedId, int expectedUserId, String expectedTitle) {
        assertNotNull(updatedAlbum, "Updated album should not be null");
        assertEquals(updatedAlbum.getId().intValue(), expectedId, "Updated album should have correct ID");
        assertEquals(updatedAlbum.getUserId().intValue(), expectedUserId, "Updated album should have correct userId");
        assertEquals(updatedAlbum.getTitle(), expectedTitle, "Updated album should have correct title");
    }

    /**
     * Validate basic album fields
     */
    public static void validateBasicAlbumFields(albums album) {
        assertNotNull(album.getId(), "Album should have an ID");
        assertNotNull(album.getUserId(), "Album should have a userId");
        assertNotNull(album.getTitle(), "Album should have a title");

        assertFalse(album.getTitle().isEmpty(), "Album title should not be empty");

        // Validate userId range (for JSONPlaceholder, valid userIds are 1-10)
        assertTrue(album.getUserId() >= 1 && album.getUserId() <= 10,
                "Album userId should be between 1 and 10 for JSONPlaceholder");
    }

    /**
     * Validate album title length and content
     */
    public static void validateAlbumTitle(String title) {
        if (title != null) {
            assertFalse(title.trim().isEmpty(), "Album title should not be empty or only whitespace");
            assertTrue(title.length() >= 1 && title.length() <= 200,
                    "Album title should be between 1 and 200 characters");
        }
    }
}