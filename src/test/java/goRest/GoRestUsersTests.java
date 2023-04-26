package goRest;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {
    int userID;
    Faker randomGenerator = new Faker();

    @Test
    public void createUser() {

        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}

        String randomFullname = randomGenerator.name().fullName();
        String randomEmail = randomGenerator.internet().emailAddress();

        userID =
                given()
                        .header("Authorization", "Bearer 4b97b6d4f186b3272628a611715896f3ab8577ae2ea6ccb07b24ca2ae90d60fc")
                        .contentType(ContentType.JSON) // gönderilecek data JSON
                        .body("{\"name\":\"" + randomFullname + "\", \"gender\":\"male\", \"email\":\"" + randomEmail + "\", \"status\":\"active\"}")
                        .log().uri()
                        .log().body()

                        .when()
                        .post("https://gorest.co.in/public/v2/users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createUser")
    public void getUserById() {

        given()
                .header("Authorization", "Bearer 4b97b6d4f186b3272628a611715896f3ab8577ae2ea6ccb07b24ca2ae90d60fc")

                .when()
                .get("https://gorest.co.in/public/v2/users/"+userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID))
        ;

    }

    @Test
    public void updateUser() {

    }

    @Test
    public void deleteUser() {

    }
}
