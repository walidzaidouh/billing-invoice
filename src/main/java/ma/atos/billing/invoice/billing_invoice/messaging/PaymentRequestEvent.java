package ma.atos.billing.invoice.billing_invoice.messaging;

import java.time.LocalDateTime;

/**
 * Événement publié par billing-invoice vers invoice.exchange
 * routing key : invoice.payment.request
 *
 * Le Payment Service consomme cet événement pour déclencher le traitement du paiement.
 * Contrat défini côté billing-invoice — à partager avec l'équipe Payment.
 */
public record PaymentRequestEvent(
        Long invoiceId,
        String reference,
        Long customerId,
        Long creancierId,
        Long pointDeVenteId,
        Double montantTtc,
        String modeReglement,
        LocalDateTime requestedAt
) {}