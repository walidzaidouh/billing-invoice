package ma.atos.billing.invoice.billing_invoice.controllers;

import ma.atos.billing.invoice.billing_invoice.services.InvoiceWorkflowService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceWorkflowController {

    private final InvoiceWorkflowService invoiceWorkflowService;

    public InvoiceWorkflowController(InvoiceWorkflowService invoiceWorkflowService) {
        this.invoiceWorkflowService = invoiceWorkflowService;
    }

    @PostMapping("/control")
    public String controlInvoice(@RequestBody ControlInvoiceRequest request) {
        return invoiceWorkflowService.startInvoiceControl(
                request.invoiceNumber(),
                request.amount()
        );
    }

    public record ControlInvoiceRequest(
            String invoiceNumber,
            BigDecimal amount
    ) {}
}