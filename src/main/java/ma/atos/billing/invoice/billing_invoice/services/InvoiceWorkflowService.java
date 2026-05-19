package ma.atos.billing.invoice.billing_invoice.services;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class InvoiceWorkflowService {

    private final RuntimeService runtimeService;

    public InvoiceWorkflowService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public String startInvoiceControl(String invoiceNumber, BigDecimal amount) {
        return runtimeService.startProcessInstanceByKey(
                "invoiceValidationProcess",
                invoiceNumber,
                Map.of(
                        "invoiceNumber", invoiceNumber,
                        "amount", amount
                )
        ).getProcessInstanceId();
    }
}