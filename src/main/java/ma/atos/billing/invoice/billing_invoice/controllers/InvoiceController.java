package ma.atos.billing.invoice.billing_invoice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.dtos.PageDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.enums.ModeReglement;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Invoices", description = "Gestion des factures")
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> create(@Valid @RequestBody InvoiceDto dto) {
        InvoiceDto created = invoiceService.createInvoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> update(@PathVariable Long id, @Valid @RequestBody InvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Recherche des factures")
    @GetMapping("/search")
    public ResponseEntity<PageDto<InvoiceDto>> searchGet(@Valid InvoiceSearchCriteria criteria) {
        InvoiceSearchCriteria safeCriteria = criteria != null ? criteria : new InvoiceSearchCriteria();
        Pageable pageable = safeCriteria.toPageable();

        Page<InvoiceDto> result = invoiceService.searchByCriteria(safeCriteria, pageable);

        PageDto<InvoiceDto> response = new PageDto<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);


    }

    @GetMapping("/errors/test-illegal")
    public void testIllegalArgument() {

        throw new IllegalArgumentException(
                "Illegal argument test"
        );
    }
}