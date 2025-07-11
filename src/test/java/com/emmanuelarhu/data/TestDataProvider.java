package com.emmanuelarhu.data;

import com.emmanuelarhu.models.*;
import org.testng.annotations.DataProvider;

/**
 * Complete test data provider for all API resources
 *
 * @author Emmanuel Arhu
 */
public class TestDataProvider {

    // ===== USER DATA PROVIDERS =====
    @DataProvider(name = "validUserData")
    public Object[][] validUserData() {
        return new Object[][]{
                {"Emmanuel Arhu", "emmanuelarhu", "emmanuel.arhu@amalitechtraining.org"},
                {"John Smith", "johnsmith", "john.smith@example.com"},
                {"Jane Doe", "janedoe", "jane.doe@example.com"}
        };
    }

    @DataProvider(name = "invalidUserData")
    public Object[][] invalidUserData() {
        return new Object[][]{
                {"", "testuser", "test@example.com"},           // Empty name
                {"Test User", "", "test@example.com"},          // Empty username
                {"Test User", "testuser", ""},                  // Empty email
                {"Test User", "testuser", "invalid-email"},     // Invalid email format
                {null, "testuser", "test@example.com"},         // Null name
                {"Test User", null, "test@example.com"},        // Null username
                {"Test User", "testuser", null}                 // Null email
        };
    }

    @DataProvider(name = "validUserIds")
    public Object[][] validUserIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}
        };
    }

    @DataProvider(name = "invalidUserIds")
    public Object[][] invalidUserIds() {
        return new Object[][]{
                {0}, {-1}, {999}, {11}, {100}
        };
    }

    // ===== POST DATA PROVIDERS =====
    @DataProvider(name = "validPostData")
    public Object[][] validPostData() {
        return new Object[][]{
                {1, "Test Post Title", "This is a test post body"},
                {2, "Another Test Post", "Another test post content"},
                {1, "Sample Post", "Sample post content for testing"}
        };
    }

    @DataProvider(name = "invalidPostData")
    public Object[][] invalidPostData() {
        return new Object[][]{
                {0, "00", "52"},              // Invalid userId
                {-1, "Test Post", "Test content"},             // Negative userId
                {999, "Test Post", "Test content"},            // Non-existent userId
                {1, "", "Test content"},                        // Empty title
                {1, "Test Post", ""},                          // Empty body
                {1, null, "Test content"},                     // Null title
                {1, "Test Post", null}                         // Null body
        };
    }

    @DataProvider(name = "validPostIds")
    public Object[][] validPostIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {10}, {25}, {50}, {75}, {100}
        };
    }

    @DataProvider(name = "invalidPostIds")
    public Object[][] invalidPostIds() {
        return new Object[][]{
                {0}, {-1}, {101}, {999}, {500}
        };
    }

    // ===== COMMENT DATA PROVIDERS =====
    @DataProvider(name = "validCommentData")
    public Object[][] validCommentData() {
        return new Object[][]{
                {1, "Test Comment", "test@example.com", "This is a test comment body"},
                {2, "Another Comment", "another@example.com", "Another comment content"},
                {1, "Sample Comment", "sample@example.com", "Sample comment for testing"}
        };
    }

    @DataProvider(name = "invalidCommentData")
    public Object[][] invalidCommentData() {
        return new Object[][]{
                {0, "Test Comment", "test@example.com", "Test body"},      // Invalid postId
                {-1, "Test Comment", "test@example.com", "Test body"},     // Negative postId
                {999, "Test Comment", "test@example.com", "Test body"},    // Non-existent postId
                {1, "", "test@example.com", "Test body"},                  // Empty name
                {1, "Test Comment", "", "Test body"},                      // Empty email
                {1, "Test Comment", "invalid-email", "Test body"},         // Invalid email
                {1, "Test Comment", "test@example.com", ""},               // Empty body
                {1, null, "test@example.com", "Test body"},                // Null name
                {1, "Test Comment", null, "Test body"},                    // Null email
                {1, "Test Comment", "test@example.com", null}              // Null body
        };
    }

    @DataProvider(name = "validCommentIds")
    public Object[][] validCommentIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {10}, {50}, {100}, {250}, {500}
        };
    }

    @DataProvider(name = "invalidCommentIds")
    public Object[][] invalidCommentIds() {
        return new Object[][]{
                {0}, {-1}, {501}, {999}, {1000}
        };
    }

    // ===== ALBUM DATA PROVIDERS =====
    @DataProvider(name = "validAlbumData")
    public Object[][] validAlbumData() {
        return new Object[][]{
                {1, "Test Album Title"},
                {2, "Another Album"},
                {1, "Sample Album"}
        };
    }

    @DataProvider(name = "invalidAlbumData")
    public Object[][] invalidAlbumData() {
        return new Object[][]{
                {0, "Test Album"},          // Invalid userId
                {-1, "Test Album"},         // Negative userId
                {999, "Test Album"},        // Non-existent userId
                {1, ""},                    // Empty title
                {1, null}                   // Null title
        };
    }

    @DataProvider(name = "validAlbumIds")
    public Object[][] validAlbumIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {10}, {25}, {50}, {75}, {100}
        };
    }

    @DataProvider(name = "invalidAlbumIds")
    public Object[][] invalidAlbumIds() {
        return new Object[][]{
                {0}, {-1}, {101}, {999}, {500}
        };
    }

    // ===== PHOTO DATA PROVIDERS =====
    @DataProvider(name = "validPhotoData")
    public Object[][] validPhotoData() {
        return new Object[][]{
                {1, "Test Photo", "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},
                {2, "Another Photo", "https://example.com/image.png", "https://example.com/thumbnail.png"},
                {1, "Sample Photo", "https://sample.com/pic.gif", "https://sample.com/small.gif"}
        };
    }

    @DataProvider(name = "invalidPhotoData")
    public Object[][] invalidPhotoData() {
        return new Object[][]{
                {0, "Test Photo", "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},     // Invalid albumId
                {-1, "Test Photo", "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},    // Negative albumId
                {999, "Test Photo", "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},   // Non-existent albumId
                {1, "", "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},               // Empty title
                {1, "Test Photo", "", "https://test.com/thumb.jpg"},                               // Empty URL
                {1, "Test Photo", "https://test.com/photo.jpg", ""},                               // Empty thumbnail
                {1, null, "https://test.com/photo.jpg", "https://test.com/thumb.jpg"},             // Null title
                {1, "Test Photo", null, "https://test.com/thumb.jpg"},                             // Null URL
                {1, "Test Photo", "https://test.com/photo.jpg", null}                              // Null thumbnail
        };
    }

    @DataProvider(name = "validPhotoIds")
    public Object[][] validPhotoIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {10}, {100}, {500}, {1000}, {2500}, {5000}
        };
    }

    @DataProvider(name = "invalidPhotoIds")
    public Object[][] invalidPhotoIds() {
        return new Object[][]{
                {0}, {-1}, {5001}, {9999}, {10000}
        };
    }

    // ===== TODO DATA PROVIDERS =====
    @DataProvider(name = "validTodoData")
    public Object[][] validTodoData() {
        return new Object[][]{
                {1, "Test Todo", false},
                {2, "Another Todo", true},
                {1, "Sample Todo", false}
        };
    }

    @DataProvider(name = "invalidTodoData")
    public Object[][] invalidTodoData() {
        return new Object[][]{
                {0, "Test Todo", "GTP2025"},        // Invalid userId
                {-1, "Test Todo", 2025},       // Negative userId
                {999, "Test Todo", false},      // Non-existent userId
                {1, "", "security-apitesting"},                 // Empty title
                {1, null, false}                // Null title
        };
    }

    @DataProvider(name = "validTodoIds")
    public Object[][] validTodoIds() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}, {10}, {50}, {100}, {150}, {200}
        };
    }

    @DataProvider(name = "invalidTodoIds")
    public Object[][] invalidTodoIds() {
        return new Object[][]{
                {0}, {-1}, {201}, {999}, {500}
        };
    }

    // ===== FILTER DATA PROVIDERS =====
    @DataProvider(name = "userIdFilters")
    public Object[][] userIdFilters() {
        return new Object[][]{
                {1}, {2}, {3}, {4}, {5}
        };
    }

    @DataProvider(name = "completionStatusFilters")
    public Object[][] completionStatusFilters() {
        return new Object[][]{
                {true}, {false}
        };
    }

    // ===== HELPER METHODS =====
    public static users createValidUser(String name, String username, String email) {
        users user = new users();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    public static users createInvalidUser(String name, String username, String email) {
        users user = new users();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    public static posts createValidPost(int userId, String title, String body) {
        return new posts(userId, title, body);
    }

    public static comments createValidComment(int postId, String name, String email, String body) {
        return new comments(postId, name, email, body);
    }

    public static albums createValidAlbum(int userId, String title) {
        return new albums(userId, title);
    }

    public static photos createValidPhoto(int albumId, String title, String url, String thumbnailUrl) {
        return new photos(albumId, title, url, thumbnailUrl);
    }

    public static todos createValidTodo(int userId, String title, boolean completed) {
        return new todos(userId, title, completed);
    }
}