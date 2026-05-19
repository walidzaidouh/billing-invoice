package ma.atos.billing.invoice.billing_invoice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.config.RabbitMQConfig;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final InvoiceService invoiceService;
    private final InvoiceEventPublisher invoiceEventPublisher;

    @RabbitListener(queues = RabbitMQConfig.INVOICE_QUEUE)
    public void onPaymentResult(PaymentResultEvent event) {
        log.info("PaymentResultEvent received: transactionId={} transactionId={} type={} montant={} caisse={} pdv={}",
                event.id(), event.invoiceId(), event.operationType(), event.montant(),
                event.caisseId(), event.pdvId());

        if (event.invoiceId() == null) {
            log.error("PaymentResultEvent has null invoiceId — message ignoré");
            return;
        }

        // 1. Mettre à jour le statut de la facture → PAID
        invoiceService.updateInvoiceStatus(event.invoiceId(), StatusInvoice.PAID);
        log.info("Invoice {} mise à jour → PAID", event.invoiceId());

        // 2. Publier le statut dans PAYMENT_STATUS_QUEUE
        invoiceEventPublisher.publishInvoiceStatus(event.invoiceId(),event.id(),StatusInvoice.PAID);
        log.info("Statut PAID publié dans PAYMENT_STATUS_QUEUE pour invoiceId={}", event.invoiceId());
    }
}