package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.enums.ModeReglement;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSearchCriteria {


    // Pageable config (optional)
    private Integer page;

    private Integer size;

    /**
     * Field name of Invoice to sort by (ex: dateInvoice, reference, montantTtc).
     */
    private String sortBy;

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
    private String sortDir;
    public Pageable toPageable() {
        int pageNumber = page != null ? Math.max(page, 0) : 0;
        int pageSize = size != null ? Math.max(size, 1) : 10;
        pageSize = Math.min(pageSize, 200);

        String resolvedSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "dateInvoice";

        Sort.Direction direction = Sort.Direction.DESC;
        if (sortDir != null && !sortDir.isBlank()) {
            try {
                direction = Sort.Direction.fromString(sortDir);
            } catch (IllegalArgumentException ex) {
                direction = Sort.Direction.DESC;
            }
        }

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, resolvedSortBy));
    }
}