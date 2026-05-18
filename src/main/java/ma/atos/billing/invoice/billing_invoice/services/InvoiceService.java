package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {

    @Caching(evict = {
            @CacheEvict(value = "invoice", allEntries = true),
            @CacheEvict(value = "invoiceSearch", allEntries = true),
            @CacheEvict(value = "invoicesByCustomer", allEntries = true),
            @CacheEvict(value = "invoicesByCreancier", allEntries = true),
            @CacheEvict(value = "invoicesByPointDeVente", allEntries = true)
    })
    InvoiceDto createInvoice(InvoiceDto invoiceDto);

    @Caching(evict = {
            @CacheEvict(value = "invoice", key = "#id"),
            @CacheEvict(value = "invoiceSearch", allEntries = true),
            @CacheEvict(value = "invoicesByCustomer", allEntries = true),
            @CacheEvict(value = "invoicesByCreancier", allEntries = true),
            @CacheEvict(value = "invoicesByPointDeVente", allEntries = true)
    })
    InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto);

    @Cacheable(value = "invoice", key = "#id")
    InvoiceDto getInvoiceById(Long id);


    @Caching(evict = {
            @CacheEvict(value = "invoice", key = "#id"),
            @CacheEvict(value = "invoiceSearch", allEntries = true),
            @CacheEvict(value = "invoicesByCustomer", allEntries = true),
            @CacheEvict(value = "invoicesByCreancier", allEntries = true),
            @CacheEvict(value = "invoicesByPointDeVente", allEntries = true)
    })
    void deleteInvoice(Long id);


    @Cacheable(
            value = "invoiceSearch",
            key = "T(java.util.Objects).hash(#criteria.referenceContains, #criteria.status, #criteria.modeReglement, #criteria.customerId, #criteria.creancierId, #criteria.pointDeVenteId, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)"
    )
    Page<InvoiceDto> searchByCriteria(InvoiceSearchCriteria criteria, Pageable pageable);

    void updateInvoiceStatus(Long invoiceId, StatusInvoice newStatus);
}
