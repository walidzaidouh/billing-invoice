package ma.atos.billing.invoice.billing_invoice.messaging;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentResultEvent(
        Long id  ,
        Long invoiceId ,
        BigDecimal montant,
        String operationType,
        Long caisseId,
        Long pdvId,
        Long customerId
) {}