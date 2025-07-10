package com.emmanuelarhu.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

import static io.restassured.RestAssured.given;

/**
 * Base test class with common setup for all API tests
 *
 * @author Emmanuel Arhu
 */
public class BaseTest {

    protected static String BASE_URL;
    private static final int DEFAULT_TIMEOUT = 10000; // 10 seconds

    @BeforeClass
    @Parameters({"baseUrl"})
    public void setup(@Optional("https://jsonplaceholder.typicode.com") String baseUrl) {
        BASE_URL = baseUrl;
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());

        // Set timeouts to handle network issues
        RestAssured.config = RestAssured.config()
                .httpClient(RestAssured.config().getHttpClientConfig()
                        .setParam("http.connection.timeout", DEFAULT_TIMEOUT)
                        .setParam("http.socket.timeout", DEFAULT_TIMEOUT));

        System.out.println("🔧 Test setup completed with Base URL: " + BASE_URL);
    }

    /**
     * Get a fresh REST Assured request with proper error handling
     */
    protected static RequestSpecification getRequest() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .log().ifValidationFails();
    }

    /**
     * Verify response time with better feedback
     */
    protected void verifyResponseTime(long responseTime) {
        if (responseTime > 5000) {
            System.out.println("⚠️ Warning: Response took " + responseTime + "ms (over 5 seconds)");
        } else {
            System.out.println("✅ Response time: " + responseTime + "ms");
        }
    }

    /**
     * Helper method to safely make API calls with retry logic
     */
    protected Response makeApiCall(String endpoint, String method) {
        int maxRetries = 3;  // ✅ Actually allow retries
        int retryCount = 0;

        while (retryCount <= maxRetries) {  // ✅ Fixed: <= instead of <
            try {
                System.out.println("🔄 Attempt " + (retryCount + 1) + "/" + (maxRetries + 1) +
                        " - " + method + " " + BASE_URL + endpoint);

                Response response;
                switch (method.toUpperCase()) {
                    case "GET":
                        response = getRequest().get(endpoint);
                        break;
                    case "POST":
                        response = getRequest().post(endpoint);
                        break;
                    case "PUT":
                        response = getRequest().put(endpoint);
                        break;
                    case "DELETE":
                        response = getRequest().delete(endpoint);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported method: " + method);
                }

                // ✅ Log response details for debugging
                System.out.println("📊 Response Status: " + response.getStatusCode());
                System.out.println("📊 Response Time: " + response.getTime() + "ms");
                System.out.println("📊 Response Body Length: " + response.getBody().asString().length());

                // ✅ Accept any non-server error status codes
                if (response.getStatusCode() < 500) {
                    return response;
                } else {
                    System.out.println("❌ Server error " + response.getStatusCode() + ", retrying...");
                }

            } catch (Exception e) {
                System.out.println("❌ Attempt " + (retryCount + 1) + " failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace(); // ✅ Print full stack trace for debugging
            }

            retryCount++;
            if (retryCount <= maxRetries) {  // ✅ Fixed condition
                try {
                    Thread.sleep(1000 * retryCount); // Exponential backoff
                    System.out.println("⏳ Waiting " + (1000 * retryCount) + "ms before retry...");
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        throw new RuntimeException("Failed to make API call to " + endpoint + " after " + (maxRetries + 1) + " attempts");
    }
}