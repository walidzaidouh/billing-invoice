package ma.atos.billing.invoice.billing_invoice.services.imp;

import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class InvoiceServiceImp implements InvoiceService {
    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        return null;
    }

    @Override
    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {
        return null;
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        return null;
    }

    @Override
    public void deleteInvoice(Long id) {

    }

    @Override
    public Page<InvoiceDto> searchByCriteria(InvoiceSearchCriteria criteria, Pageable pageable) {
        return null;
    }
}
