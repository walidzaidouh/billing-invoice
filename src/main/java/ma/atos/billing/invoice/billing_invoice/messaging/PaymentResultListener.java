package ma.atos.billing.invoice.billing_invoice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Écoute les résultats de paiement publiés par le Payment Service.
 *
 * Queue : payment.result.queue  (voir RabbitMQConfig)
 *
 * Sur PaymentResultEvent.status = "ACCEPTED" → facture passe à PAID
 * Sur PaymentResultEvent.status = "REJECTED" → facture passe à REJECTED
 *
 * En cas d'exception, le message est rejeté et part en DLQ (payment.result.queue.dlq)
 * après épuisement des retries configurés dans application.yaml.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final InvoiceService invoiceService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESULT_QUEUE)
    public void onPaymentResult(PaymentResultEvent event) {
        log.info("PaymentResultEvent received: invoiceId={} status={} processedAt={}",
                event.invoiceId(), event.status(), event.processedAt());

        if (event.invoiceId() == null) {
            log.error("PaymentResultEvent has null invoiceId — message ignoré");
            return;
        }

        StatusInvoice newStatus = switch (event.status()) {
            case "ACCEPTED" -> StatusInvoice.PAID;
            case "REJECTED" -> {
                log.warn("Payment rejected for invoiceId={} reason={}", event.invoiceId(), event.reason());
                yield StatusInvoice.REJECTED;
            }
            default -> {
                log.error("Statut inconnu '{}' pour invoiceId={}", event.status(), event.invoiceId());
                throw new IllegalArgumentException(
                        "Statut PaymentResultEvent inconnu: " + event.status()
                );
            }
        };

        invoiceService.updateInvoiceStatus(event.invoiceId(), newStatus);

        log.info("Invoice {} mise à jour → status={}", event.invoiceId(), newStatus);
    }
}