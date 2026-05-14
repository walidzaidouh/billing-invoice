package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {

    InvoiceDto createInvoice(InvoiceDto invoiceDto);

    InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto);

    InvoiceDto getInvoiceById(Long id);

    void deleteInvoice(Long id);

    Page<InvoiceDto> searchByCriteria(InvoiceSearchCriteria criteria, Pageable pageable);
    //Specifcation
}
