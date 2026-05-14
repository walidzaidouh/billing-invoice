package ma.atos.billing.invoice.billing_invoice.controllers;

import jakarta.validation.Valid;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points-de-vente")
public class PointDeVenteController {

    private final PointDeventeService service;

    public PointDeVenteController(PointDeventeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PointDeVenteDto> create(@Valid @RequestBody PointDeVenteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointDeVenteDto> update(@PathVariable("id") Long id, @Valid @RequestBody PointDeVenteDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
