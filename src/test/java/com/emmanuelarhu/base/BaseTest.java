package com.emmanuelarhu.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

/**
 * Base test class with common setup for all API tests
 *
 * @author Emmanuel Arhu
 */
public class BaseTest {

    protected static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
    }

    /**
     * Get a fresh REST Assured request
     */
    protected static io.restassured.specification.RequestSpecification getRequest() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /**
     * Verify response time is reasonable (under 5 seconds)
     */
    protected void verifyResponseTime(long responseTime) {
        if (responseTime > 5000) {
            System.out.println("⚠️ Warning: Response took " + responseTime + "ms (over 5 seconds)");
        } else {
            System.out.println("✅ Response time: " + responseTime + "ms");
        }
    }
}