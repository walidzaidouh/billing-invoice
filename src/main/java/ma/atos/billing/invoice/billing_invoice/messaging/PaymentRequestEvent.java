package ma.atos.billing.invoice.billing_invoice.messaging;

import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;

import java.time.LocalDateTime;


public record PaymentRequestEvent(
        Long invoiceId,
        StatusInvoice statusInvoice
) {}