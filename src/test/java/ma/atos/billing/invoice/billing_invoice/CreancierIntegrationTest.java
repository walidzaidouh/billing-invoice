package ma.atos.billing.invoice.billing_invoice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.enums.TypeCreancier;
import ma.atos.billing.invoice.billing_invoice.repository.CreancierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// 1. Starts the full Spring Boot app on a random port to avoid conflicts
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 2. Tells Spring to use your Testcontainers Postgres DB instead of the one in application.yaml
@Import(TestcontainersConfiguration.class)
public class CreancierIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CreancierRepository creancierRepository;

    @BeforeEach
    void setUp() {
        // Tell REST Assured which port the app started on
        RestAssured.port = port;

        // Clean the database before EVERY test so they don't interfere with each other
        creancierRepository.deleteAll();
    }

    @Test
    void shouldCreateNewCreancier() {
        // Arrange: Prepare the DTO
        CreancierDto newCreancier = new CreancierDto();
        newCreancier.setNom("Maroc Telecom");
        newCreancier.setTypeCreancier(TypeCreancier.IAM);
        newCreancier.setIce("123456789");

        // Act & Assert using REST Assured
        given()
                .contentType(ContentType.JSON)
                .body(newCreancier)
                .when()
                .post("/api/creanciers")
                .then()
                .statusCode(200) // Expect HTTP 200 OK
                .body("nom", equalTo("Maroc Telecom"))
                .body("typeCreancier", equalTo("IAM"))
                .body("id", notNullValue()); // Verify the DB generated an ID
    }

    @Test
    void shouldGetAllCreanciersWithPagination() {
        // Arrange: Save a Creancier directly to the DB first
        CreancierDto newCreancier = new CreancierDto();
        newCreancier.setNom("Attijariwafa");
        newCreancier.setTypeCreancier(TypeCreancier.BANQUE);

        // Use REST Assured to save it
        given().contentType(ContentType.JSON).body(newCreancier).post("/api/creanciers");

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/creanciers?page=0&size=10")
                .then()
                .statusCode(200)
                // Because we used pagination, the data is inside the "content" array
                .body("content.size()", equalTo(1))
                .body("content[0].nom", equalTo("Attijariwafa"))
                .body("totalElements", equalTo(1));
    }

    @Test
    void shouldReturn404WhenCreancierNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/creanciers/99999") // ID that doesn't exist
                .then()
                .statusCode(404); // Verify our Error Handling works!
    }
}