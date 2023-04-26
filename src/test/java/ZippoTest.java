import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import model.Location;
import model.Place;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {
    @Test
    public void test() {
        given().when().then();
    }

    @Test
    public void statusCodeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void contentTypeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void checkCountryInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("country", equalTo("United States"))
        ;
    }

    @Test
    public void checkstateInResponseBody() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California"))
        ;
    }

    @Test
    public void checkHasItemy() {
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .statusCode(200)
                .body("places.'place name'", hasItem("Dörtağaç Köyü"))
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void combiningTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
        ;
    }

    @Test
    public void pathParamTest() {
        given()

                .pathParam("country", "us")
                .pathParam("zipCode", 90210)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{country}/{zipCode}")

                .then()
                // .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest() {
        given()

                .param("page", 1)
                .log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                // .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest2() {

        for (int i = 1; i < 10; i++) {
            given()

                    .param("page", i)
                    .log().uri()

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    //.log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i))
            ;
        }
    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void Setup() {

        baseURI = "https://gorest.co.in/public/v1";

        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }


    @Test
    public void requestResponseSpecification() {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page", 1)
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
        ;
    }

    @Test
    public void extractingJsonPath() {
        String countryName =
                given()

                        .when().get("http://api.zippopotam.us/us/90210").

                        then().log().body().extract().path("country");

        System.out.println("countryName = " + countryName);
        Assert.assertEquals(countryName, "United States");
    }

    @Test
    public void extractingJsonPath2() {
        String placeName =
                given()

                        .when().get("http://api.zippopotam.us/us/90210")

                        .then().log().body().extract().path("places[0]['place name']");

        System.out.println("placeName = " + placeName);
        Assert.assertEquals(placeName, "Beverly Hills");
    }

    @Test
    public void extractingJsonPath3() {
        // https:gorest.co.inpublicv1users print the limit information in the return value.
        int limit =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users ")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        Assert.assertEquals(limit, 10);
    }

    @Test
    public void extractingJsonPath4() {

        // https://gorest.co.in/public/v1/users  print all the ids in the return value.

        List<Integer> IDs =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("data.id");

        System.out.println("IDs = " + IDs);
    }

    @Test
    public void extractingJsonPathResponsAll() {
        // https://gorest.co.in/public/v1/users  print some values in the return value.

        Response returningData =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        // .log().body()
                        .extract().response(); // returns all data returned.


        List<Integer> IDs = returningData.path("data.id");
        List<String> names = returningData.path("data.name");
        int limit = returningData.path("meta.pagination.limit");

        System.out.println("IDs = " + IDs);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

        // Assert.assertTrue(names.contains("Dakshayani Pandey"));
        // Assert.assertTrue(IDs.contains(1203767));
        Assert.assertEquals(limit, 10, "test result is wrong");
    }

    @Test
    public void extractJsonAll_POJO() {
        // POJO : JSON Object : locationObject
        Location locationObject =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        //.log().body()
                        .extract().body().as(Location.class);
        System.out.println("locationObject.getCountry() = " + locationObject.getCountry());

        for (Place place : locationObject.getPlaces()) {
            System.out.println("place = " + place);
        }
        System.out.println("locationObject.getPlaces() = " + locationObject.getPlaces());

        System.out.println(locationObject.getPlaces().get(0).getPlaceName());
        System.out.println(locationObject.getPlaces().get(0).getLatitude());

    }

    @Test
    public void extractPOJO() {
        // Print other information about "Dörtağaç Köyü" at the following endpoint(link).
        Location adana =
                given()

                        .when()
                        .get("http://api.zippopotam.us/tr/01000")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().body().as(Location.class);

        for (Place place : adana.getPlaces()) {
            if (place.getPlaceName().equalsIgnoreCase("Dörtağaç Köyü")) {
                System.out.println("place = " + place);
            }
        }
    }

}
