# JSONPlaceholder API Testing Framework

A simple and focused REST API testing framework built with **REST Assured** and **JUnit 5** for testing the JSONPlaceholder API.

## 🎯 What This Framework Tests

### **6 JSONPlaceholder Resources**
- **`/posts`** - 100 posts
- **`/comments`** - 500 comments
- **`/albums`** - 100 albums
- **`/photos`** - 5000 photos
- **`/todos`** - 200 todos
- **`/users`** - 10 users

### **HTTP Operations Tested**
- ✅ **GET** `/resource` - Get all items
- ✅ **GET** `/resource/1` - Get single item by ID
- ✅ **POST** `/resource` - Create new item
- ✅ **PUT** `/resource/1` - Update existing item
- ✅ **PATCH** `/resource/1` - Partially update item
- ✅ **DELETE** `/resource/1` - Delete item
- ✅ **Query Parameters** - Filter resources (e.g., `?postId=1`, `?userId=1`)

## 🛠️ Technology Stack

- **Java 11+**
- **Maven** - Build management
- **REST Assured 5.3.2** - API testing
- **JUnit 5.10.0** - Test framework
- **Allure 2.24.0** - Test reporting
- **Jackson** - JSON processing

## 📁 Simple Project Structure

```
jsonplaceholder-api-testing/
├── src/test/java/com/emmanuelarhu/
│   ├── base/
│   │   └── BaseTest.java           # Common test setup
│   ├── models/
│   │   ├── Post.java               # Post model
│   │   ├── User.java               # User model  
│   │   ├── Comment.java            # Comment model
│   │   ├── Album.java              # Album model
│   │   ├── Photo.java              # Photo model
│   │   └── Todo.java               # Todo model
│   └── tests/
│       ├── PostsTest.java          # Posts API tests
│       ├── UsersTest.java          # Users API tests
│       ├── CommentsTest.java       # Comments API tests
│       ├── AlbumsTest.java         # Albums API tests
│       ├── PhotosTest.java         # Photos API tests
│       └── TodosTest.java          # Todos API tests
├── pom.xml                         # Maven configuration
└── README.md                       # This documentation
```

## ⚡ Quick Start

### 1. **Prerequisites**
- Java 11+ installed
- Maven 3.6+ installed

### 2. **Clone and Run**
```bash
# Clone the repository
git clone <your-repo-url>
cd jsonplaceholder-api-testing

# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=PostsTest

# Generate Allure report
mvn allure:serve
```

## 🧪 Test Examples

### **Posts API Tests**
```java
@Test
@DisplayName("GET /posts - Should return all 100 posts")
public void testGetAllPosts() {
    Response response = getRequest()
        .when()
        .get("/posts")
        .then()
        .statusCode(200)
        .body("$", hasSize(100))
        .body("[0].id", notNullValue())
        .body("[0].title", not(emptyString()))
        .extract().response();
    
    // Verify response time
    verifyResponseTime(response.getTime());
    
    // Convert to objects and verify
    Post[] posts = response.as(Post[].class);
    assertEquals(100, posts.length, "Should have exactly 100 posts");
    
    System.out.println("✅ Successfully retrieved " + posts.length + " posts");
}
```

### **Comments Filtering**
```java
@Test
@DisplayName("GET /comments?postId=1 - Should filter comments by post")
public void testFilterCommentsByPost() {
    Response response = getRequest()
        .queryParam("postId", 1)
        .when()
        .get("/comments")
        .then()
        .statusCode(200)
        .body("postId", everyItem(equalTo(1)))
        .extract().response();
    
    Comment[] comments = response.as(Comment[].class);
    for (Comment comment : comments) {
        assertEquals(1, comment.getPostId(), "All comments should belong to post 1");
    }
}
```

## ✅ What Each Test Verifies

### **For Every Endpoint:**
1. **HTTP Status Codes** - Correct response codes (200, 201, 404)
2. **Response Structure** - Required fields are present and not empty
3. **Data Types** - Fields have correct data types
4. **Response Time** - Reasonable performance (under 5 seconds with warning)
5. **Object Conversion** - JSON can be converted to Java objects
6. **Business Logic** - Data makes sense (e.g., valid email formats, proper IDs)

### **Specific Validations:**
- **Posts**: Title and body are not empty, userId exists
- **Users**: Email contains "@", name and username are not empty
- **Comments**: Email format, postId links to valid post
- **Albums**: Title exists, userId links to valid user
- **Photos**: URLs are not empty, albumId links to valid album
- **Todos**: Completion status is boolean, userId links to valid user

## 📊 Test Output Examples

```bash
✅ Successfully retrieved 100 posts
✅ Response time: 245ms
✅ Successfully retrieved post: Post{id=1, userId=1, title='sunt aut facere...'}
✅ Successfully retrieved 5 comments for post 1
✅ Successfully created new post with ID: 101
✅ Successfully updated post: Post{id=1, userId=1, title='Updated Post Title'}
✅ Successfully deleted post 1
✅ Correctly returned 404 for non-existent post
```

## 🔧 Key Features

### **Simple and Focused**
- ✅ Clean, readable test code
- ✅ Clear test names and descriptions
- ✅ Simple assertions with helpful error messages
- ✅ Minimal setup required

### **Comprehensive Coverage**
- ✅ All 6 JSONPlaceholder endpoints
- ✅ All HTTP methods (GET, POST, PUT, PATCH, DELETE)
- ✅ Query parameter filtering
- ✅ Error handling (404 for non-existent resources)

### **Built-in Validation**
- ✅ HTTP status code verification
- ✅ Response structure validation
- ✅ Data type checking
- ✅ Performance monitoring
- ✅ Object conversion testing

### **Professional Reporting**
- ✅ Allure test reports with detailed results
- ✅ Response time tracking
- ✅ Clear test descriptions
- ✅ Request/response logging

## 🚀 Running Different Test Scenarios

```bash
# Run all tests
mvn clean test

# Run specific endpoint tests
mvn test -Dtest=PostsTest
mvn test -Dtest=UsersTest
mvn test -Dtest=CommentsTest

# Run with verbose output
mvn test -X

# Generate and serve Allure report
mvn allure:serve

# Generate static Allure report
mvn allure:report
```

## 📋 Test Plan Summary

### **Functional Testing**
- [x] **CRUD Operations** - Create, Read, Update, Delete for all endpoints
- [x] **Data Validation** - Verify response structure and data integrity
- [x] **Filtering** - Test query parameters and filtering capabilities
- [x] **Error Handling** - Test 404 responses for non-existent resources

### **Non-Functional Testing**
- [x] **Performance** - Monitor response times with warnings for slow responses
- [x] **Data Integrity** - Verify relationships between resources (e.g., comments→posts)
- [x] **JSON Processing** - Test object serialization/deserialization

## 📈 Allure Reports

The framework generates comprehensive Allure reports that include:
- ✅ **Test Results Overview** - Pass/fail statistics
- ✅ **Test Case Details** - Individual test execution details
- ✅ **Request/Response Data** - Full HTTP request and response logs
- ✅ **Performance Data** - Response time tracking
- ✅ **Error Analysis** - Detailed failure information

## 🎯 Success Criteria

**All tests pass when:**
- ✅ Correct number of resources returned (100 posts, 10 users, etc.)
- ✅ All required fields are present and not empty
- ✅ HTTP status codes are correct (200, 201, 404)
- ✅ Response times are under 5 seconds
- ✅ Data types match expectations
- ✅ Filtering works correctly
- ✅ CRUD operations complete successfully

## 👨‍💻 Author

**Emmanuel Arhu**  
*Quality Assurance Engineer & Developer*

- 🌐 Website: [emmanuelarhu.link](https://emmanuelarhu.link)
- 💼 LinkedIn: [linkedin.com/in/emmanuelarhu](https://www.linkedin.com/in/emmanuelarhu)
- 📧 Contact: [emmanuelarhu.link/contact](https://emmanuelarhu.link/contact)

---

This framework demonstrates clean, simple API testing practices while providing comprehensive coverage of the JSONPlaceholder API. It's designed to be easy to understand, maintain, and extend.