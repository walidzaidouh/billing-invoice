package ma.atos.billing.invoice.billing_invoice.messaging;

import java.time.LocalDateTime;

/**
 * Événement reçu depuis le Payment Service via payment.exchange.
 * routing key : payment.result
 *
 * Le Payment Service publie cet événement après avoir traité la demande.
 * Contrat à valider avec l'équipe Payment — le champ `status` contiendra
 * "ACCEPTED" ou "REJECTED" selon le résultat.
 */
public record PaymentResultEvent(
        Long invoiceId,
        String status,          // "ACCEPTED" | "REJECTED"
        String reason,          // motif du refus (null si accepté)
        LocalDateTime processedAt
) {}