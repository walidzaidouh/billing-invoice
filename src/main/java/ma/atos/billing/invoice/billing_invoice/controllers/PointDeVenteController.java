package ma.atos.billing.invoice.billing_invoice.controllers;


import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping(path = "api/point-de-vente")
public class PointDeVenteController {
    private  final PointDeventeService pointDeventeService;

    public PointDeVenteController(PointDeventeService pointDeventeService){
        this.pointDeventeService=pointDeventeService;
    }

    @GetMapping(path = "/get-by-id/{id}")
    private ResponseEntity<PointDeVenteDto> getById(@PathVariable long id){

        PointDeVenteDto pointDeVenteDto = pointDeventeService.getPointDeVenteById(id);

        return  ResponseEntity.ok(pointDeVenteDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PointDeVenteDto>> searchCreanciers(
            PointDeVenteSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page) {

        return ResponseEntity.ok(pointDeventeService.searchPointDeVente(criteria, page, 10));
    }
}
