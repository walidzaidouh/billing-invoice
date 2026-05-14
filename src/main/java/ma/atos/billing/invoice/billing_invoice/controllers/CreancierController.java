package ma.atos.billing.invoice.billing_invoice.controllers;

import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.services.CreancierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/creanciers")
public class CreancierController {

    private final CreancierService creancierService;

    public CreancierController(CreancierService creancierService) {
        this.creancierService = creancierService;
    }

    @PostMapping
    public ResponseEntity<CreancierDto> createCreancier(@Valid @RequestBody CreancierDto creancierDto) {
        return ResponseEntity.ok(creancierService.createCreancier(creancierDto));
    }

    @GetMapping
    public ResponseEntity<List<CreancierDto>> getAllCreanciers() {
        return ResponseEntity.ok(creancierService.getAllCreanciers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreancierDto> getCreancierById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(creancierService.getCreancierById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreancierDto> updateCreancier(@PathVariable Long id, @Valid @RequestBody CreancierDto creancierDto) {
        try {
            return ResponseEntity.ok(creancierService.updateCreancier(id, creancierDto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreancier(@PathVariable Long id) {
        creancierService.deleteCreancier(id);
        return ResponseEntity.noContent().build();
    }
}