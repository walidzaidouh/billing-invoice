package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.mappers.InvoiceMapper;
import ma.atos.billing.invoice.billing_invoice.repository.InvoiceRepository;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImp implements InvoiceService {
     @Autowired
    private final InvoiceMapper invoiceMapper;
    @Autowired
    private InvoiceRepository invoiceRepository ;



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
    //@Transactional(readOnly = true)
    public Page<InvoiceDto> searchByCriteria(InvoiceSearchCriteria criteria, Pageable pageable) {
        if (criteria == null) {
            return invoiceRepository.findAll(pageable).map(invoiceMapper::toDto);
        }

        Specification<Invoice> spec = new Specification<Invoice>() {
            @Override
            public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                ArrayList<Predicate> predicates = new ArrayList<>();

                if (criteria.getReferenceContains() != null && !criteria.getReferenceContains().isBlank()) {
                    predicates.add(
                            cb.like(
                                    cb.lower(root.get("reference")),
                                    "%" + criteria.getReferenceContains().toLowerCase() + "%"
                            )
                    );
                }

                if (criteria.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
                }

                if (criteria.getModeReglement() != null) {
                    predicates.add(cb.equal(root.get("modeReglement"), criteria.getModeReglement()));
                }

                if (criteria.getCustomerId() != null) {
                    predicates.add(cb.equal(root.get("customer").get("id"), criteria.getCustomerId()));
                }

                if (criteria.getCreancierId() != null) {
                    predicates.add(cb.equal(root.get("creancier").get("id"), criteria.getCreancierId()));
                }

                if (criteria.getPointDeVenteId() != null) {
                    predicates.add(cb.equal(root.get("pointDeVente").get("id"), criteria.getPointDeVenteId()));
                }

                if (criteria.getDateInvoiceFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("dateInvoice"), criteria.getDateInvoiceFrom()));
                }

                if (criteria.getDateInvoiceTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("dateInvoice"), criteria.getDateInvoiceTo()));
                }

                if (criteria.getDateDueFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("dateDue"), criteria.getDateDueFrom()));
                }

                if (criteria.getDateDueTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("dateDue"), criteria.getDateDueTo()));
                }

                if (criteria.getMontantTtcMin() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("montantTtc"), criteria.getMontantTtcMin()));
                }

                if (criteria.getMontantTtcMax() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("montantTtc"), criteria.getMontantTtcMax()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        return invoiceRepository.findAll(spec, pageable).map(invoiceMapper::toDto);
    }
}
