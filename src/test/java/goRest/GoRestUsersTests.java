package goRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {
    int userID;
    Faker randomGenerator = new Faker();
    RequestSpecification requestSpecification;

    @BeforeClass
    public void setup() {

        baseURI = "https://gorest.co.in/public/v2/users";
        // baseURI="https://gorest.co.in/public/v2/users/";

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer 4b97b6d4f186b3272628a611715896f3ab8577ae2ea6ccb07b24ca2ae90d60fc")
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test(enabled = false)
    public void createUserJson() {

        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}

        String randomFullname = randomGenerator.name().fullName();
        String randomEmail = randomGenerator.internet().emailAddress();

        userID =
                given()
                        .spec(requestSpecification)
                        .body("{\"name\":\"" + randomFullname + "\", \"gender\":\"male\", \"email\":\"" + randomEmail + "\", \"status\":\"active\"}")
                        .log().uri()
                        .log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }

    @Test
    public void createUserMap() {

        String randomFullname = randomGenerator.name().fullName();
        String randomEmail = randomGenerator.internet().emailAddress();

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", randomFullname);
        newUser.put("gender", "male");
        newUser.put("email", randomEmail);
        newUser.put("status", "active");

        userID =
                given()
                        .spec(requestSpecification)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(enabled = false)
    public void createUserClass() {

        String randomFullname = randomGenerator.name().fullName();
        String randomEmail = randomGenerator.internet().emailAddress();

        User newUser = new User();
        newUser.name = randomFullname;
        newUser.gender = "male";
        newUser.email = randomEmail;
        newUser.status = "active";

        userID =
                given()
                        .spec(requestSpecification)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createUserMap")
    public void getUserById() {

        given()
                .spec(requestSpecification)

                .when()
                .get("" + userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))
        ;

    }

    @Test(dependsOnMethods = "getUserById")
    public void updateUser() {

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", "kerem yigit");

        given()
                .spec(requestSpecification)
                .body(updateUser)

                .when()
                .put("" + userID)

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
                .body("name", equalTo("kerem yigit"))

        ;

    }

    //TODO
    @Test(dependsOnMethods = "updateUser")
    public void deleteUser() {

        given()
                .spec(requestSpecification)
                .when()
                .delete("" + userID)

                .then()
                .log().all()
                .statusCode(204)
        ;

    }

    //TODO
    @Test(dependsOnMethods = "deleteUser")
    public void deleteUserNegative() {

        given()
                .spec(requestSpecification)
                .when()
                .delete("" + userID)

                .then()
                .log().all()
                .statusCode(404)
        ;
    }

    // TODO : Weekend TODO:  You can use your previous posts and comments resources in GoRest.
    // TODO : Do your API Automation(Create,get,update,delete,deletenegatife)
}
