package ma.atos.billing.invoice.billing_invoice.it;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
class InvoiceRestAssuredTest extends AbstractRestAssuredIT {
  //spring cache
    //yam cache redis
    @Test
    void shouldCreateInvoiceWithSeedData() {
        String body = """
                {
                  "reference": "INV-IT-001",
                  "dateInvoice": "2026-05-14",
                  "dateDue": "2026-06-14",
                  "montantHt": 100.0,
                  "montantTva": 20.0,
                  "montantTtc": 120.0,
                  "status": "EN_ATTENTE",
                  "modeReglement": "VIREMENT",
                  "description": "Facture integration test",
                  "customerId": 1001,
                  "creancierId": 2001,
                  "pointDeVenteId": 3002
                }
                """;

        given()
                .contentType(json())
                .body(body)
                .when()
                .post("/api/invoices")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("reference", equalTo("INV-IT-001"))
                .body("status", equalTo("EN_ATTENTE"))
                .body("modeReglement", equalTo("VIREMENT"))
                .body("customerId", equalTo(1001))
                .body("creancierId", equalTo(2001))
                .body("pointDeVenteId", equalTo(3002));
    }

    @Test
    void shouldSearchInvoicesByModeReglement() {
        given()
                .queryParam("modeReglement", "VIREMENT")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/invoices/search")
                .then()
                .statusCode(200)
                .body("content.modeReglement", everyItem(equalTo("VIREMENT")));
    }

    @Test
    void shouldReturnBadRequestWhenModeReglementIsInvalid() {
        given()
                .queryParam("modeReglement", "VIREMEN")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/invoices/search")
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }
}