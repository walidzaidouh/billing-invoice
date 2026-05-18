package ma.atos.billing.invoice.billing_invoice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie un PaymentRequestEvent vers le Payment Service après création de la facture.
     * Exchange : invoice.exchange — routing key : invoice.payment.request
     */
    public void publishPaymentRequest(InvoiceDto invoiceDto) {
        PaymentRequestEvent event = new PaymentRequestEvent(
                invoiceDto.getId(),
                invoiceDto.getReference(),
                invoiceDto.getCustomerId(),
                invoiceDto.getCreancierId(),
                invoiceDto.getPointDeVenteId(),
                invoiceDto.getMontantTtc(),
                invoiceDto.getModeReglement() != null ? invoiceDto.getModeReglement().name() : null,
                LocalDateTime.now()
        );

        log.info("Publishing PaymentRequestEvent for invoiceId={} reference={}",
                event.invoiceId(), event.reference());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVOICE_EXCHANGE,
                RabbitMQConfig.PAYMENT_REQUEST_ROUTING_KEY,
                event
        );
    }
}