package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.Data;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;

@Data
public class PublisherInvoice {
    Long idInvoice ;
    StatusInvoice statusInvoice ;
}
