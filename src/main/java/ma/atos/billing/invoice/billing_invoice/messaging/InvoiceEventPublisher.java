package ma.atos.billing.invoice.billing_invoice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.config.RabbitMQConfig;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie le statut d'une facture dans PAYMENT_STATUS_QUEUE.
     * Utilise le default exchange ("") avec le nom de la queue comme routing key,
     * car les queues sont créées manuellement dans RabbitMQ (pas bindées à un exchange custom).
     */
    public void publishInvoiceStatus(Long invoiceId, StatusInvoice status) {
        PaymentRequestEvent event = new PaymentRequestEvent(invoiceId, status);

        log.info("Publishing invoice status: invoiceId={} status={}", invoiceId, status);

        // default exchange "" → routing key = nom exact de la queue
        rabbitTemplate.convertAndSend("", RabbitMQConfig.PAYMENT_STATUS_QUEUE, event);
    }
}