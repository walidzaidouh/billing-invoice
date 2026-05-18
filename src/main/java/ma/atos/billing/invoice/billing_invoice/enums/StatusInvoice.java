package ma.atos.billing.invoice.billing_invoice.enums;

/**
 * Cycle de vie d'une facture dans le flux event-driven avec le Payment Service.
 *
 * DRAFT      → facture créée, PaymentRequestEvent publié, en attente du Payment Service
 * PROCESSING → (optionnel) Payment Service a bien reçu la demande et traite
 * PAID       → PaymentAcceptedEvent reçu, paiement confirmé
 * REJECTED   → PaymentRejectedEvent reçu, paiement refusé
 * EN_RETARD  → échéance dépassée sans paiement
 * ANNULEE    → facture annulée manuellement
 */
public enum StatusInvoice {
    DRAFT,
    PROCESSING,
    PAID,
    REJECTED,
    EN_RETARD,
    ANNULEE
}