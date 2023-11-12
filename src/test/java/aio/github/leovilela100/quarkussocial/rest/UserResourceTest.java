package aio.github.leovilela100.quarkussocial.rest;

import aio.github.leovilela100.quarkussocial.domain.model.User;
import aio.github.leovilela100.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {
    @Test
    @DisplayName("should create an user successfully")
    public void createUserTest() {

        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(18);

        System.out.println(user);

        Response response;

        response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users")
                .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }
}