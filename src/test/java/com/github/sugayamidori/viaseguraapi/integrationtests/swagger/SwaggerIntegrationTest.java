package com.github.sugayamidori.viaseguraapi.integrationtests.swagger;

import com.github.sugayamidori.viaseguraapi.config.TestConfig;
import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.ControllerIntegrationTest;
import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.PostgresTestContainer;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ControllerIntegrationTest
class SwaggerIntegrationTest implements PostgresTestContainer {

    @Test
    void shouldDisplaySwaggerUIPage() {
        var content = given()
                .basePath("/swagger-ui/index.html")
                .port(TestConfig.SERVER_PORT)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        assertTrue(content.contains("Swagger UI"));
    }
}
