package ma.atos.billing.invoice.billing_invoice.controllers;

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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/search")
    public ResponseEntity<PageDto<InvoiceDto>> searchGet(InvoiceSearchCriteria criteria) {
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

    @GetMapping("/reference/{reference}")
    public ResponseEntity<InvoiceDto> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(invoiceService.findByReference(reference));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            Object rejectedValue = error.getRejectedValue();

            if ("modeReglement".equals(fieldName)) {
                errors.put(
                        fieldName,
                        "La valeur '" + rejectedValue + "' est invalide. Valeurs autorisées: "
                                + Arrays.toString(ModeReglement.values())
                );
            } else {
                errors.put(fieldName, error.getDefaultMessage());
            }
        }

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Paramètres de recherche invalides");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();

        String fieldName = ex.getName();
        Object invalidValue = ex.getValue();

        if ("modeReglement".equals(fieldName)) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "La valeur '" + invalidValue + "' est invalide pour modeReglement");
            response.put("valeursAutorisees", Arrays.toString(ModeReglement.values()));
        } else {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Paramètre invalide: " + fieldName);
            response.put("valeurRecue", invalidValue);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}