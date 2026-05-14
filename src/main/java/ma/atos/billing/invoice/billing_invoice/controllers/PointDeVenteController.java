package ma.atos.billing.invoice.billing_invoice.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/points-de-vente")
@RequiredArgsConstructor
public class PointDeVenteController {




}
