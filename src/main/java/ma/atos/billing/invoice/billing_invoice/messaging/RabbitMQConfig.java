package ma.atos.billing.invoice.billing_invoice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour le microservice billing-invoice.
 *
 * Topologie :
 *
 *  [billing-invoice] ──publish──► invoice.exchange
 *                                      │ routing key: invoice.payment.request
 *                                      ▼
 *                              invoice.payment.request.queue  ──► [payment-service]
 *
 *  [payment-service] ──publish──► payment.exchange
 *                                      │ routing key: payment.result
 *                                      ▼
 *                              payment.result.queue  ──► [billing-invoice listener]
 *                                      │ (en cas d'erreur)
 *                                      ▼
 *                              payment.result.queue.dlq
 *
 * À partager avec l'équipe Payment :
 *   - Ils doivent publier sur payment.exchange avec routing key payment.result
 *   - Le payload attendu est défini par PaymentResultEvent
 */
@Configuration
public class RabbitMQConfig {

    // ─── Notre exchange (on publie) ──────────────────────────────────────────
    public static final String INVOICE_EXCHANGE            = "invoice.exchange";
    public static final String PAYMENT_REQUEST_QUEUE       = "invoice.payment.request.queue";
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "invoice.payment.request";

    // ─── Exchange Payment (on consomme) ─────────────────────────────────────
    public static final String PAYMENT_EXCHANGE            = "payment.exchange";
    public static final String PAYMENT_RESULT_QUEUE        = "payment.result.queue";
    public static final String PAYMENT_RESULT_DLQ          = "payment.result.queue.dlq";
    public static final String PAYMENT_RESULT_ROUTING_KEY  = "payment.result";

    // ─── Notre exchange ──────────────────────────────────────────────────────

    @Bean
    public DirectExchange invoiceExchange() {
        return new DirectExchange(INVOICE_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(PAYMENT_REQUEST_QUEUE).build();
    }

    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder
                .bind(paymentRequestQueue())
                .to(invoiceExchange())
                .with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    // ─── Exchange Payment (côté écoute) ─────────────────────────────────────

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    /** Dead-letter queue : messages non traités après épuisement des retries */
    @Bean
    public Queue paymentResultDlq() {
        return QueueBuilder.durable(PAYMENT_RESULT_DLQ).build();
    }

    @Bean
    public Queue paymentResultQueue() {
        return QueueBuilder.durable(PAYMENT_RESULT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PAYMENT_RESULT_DLQ)
                .build();
    }

    @Bean
    public Binding paymentResultBinding() {
        return BindingBuilder
                .bind(paymentResultQueue())
                .to(paymentExchange())
                .with(PAYMENT_RESULT_ROUTING_KEY);
    }

    // ─── Infrastructure partagée ─────────────────────────────────────────────

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}