package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.enums.ModeReglement;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSearchCriteria {

    private String referenceContains;

    private StatusInvoice status;

    private ModeReglement modeReglement;

    private Long customerId;

    private Long creancierId;

    private Long pointDeVenteId;

    private LocalDate dateInvoiceFrom;

    private LocalDate dateInvoiceTo;

    private LocalDate dateDueFrom;

    private LocalDate dateDueTo;

    private Double montantTtcMin;

    private Double montantTtcMax;
}