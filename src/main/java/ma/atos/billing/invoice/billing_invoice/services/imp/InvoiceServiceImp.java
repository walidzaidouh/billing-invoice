package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.entities.Customer;
import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import ma.atos.billing.invoice.billing_invoice.mappers.InvoiceMapper;
import ma.atos.billing.invoice.billing_invoice.repository.CreancierRepository;
import ma.atos.billing.invoice.billing_invoice.repository.CustomerRepository;
import ma.atos.billing.invoice.billing_invoice.repository.InvoiceRepository;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
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


    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final CreancierRepository creancierRepository;
    @Autowired
    private final PointDeVenteRepository pointDeVenteRepository;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        if (invoiceDto == null) {
            throw new IllegalArgumentException("invoiceDto must not be null");
        }

        Customer customer = getCustomer(invoiceDto.getCustomerId());
        Creancier creancier = getCreancier(invoiceDto.getCreancierId());
        PointDeVente pointDeVente = getPointDeVente(invoiceDto.getPointDeVenteId());
        Invoice entity = invoiceMapper.toEntity(invoiceDto, customer, creancier, pointDeVente);
        Invoice saved = invoiceRepository.save(entity);
        return invoiceMapper.toDto(saved);
    }

    @Override
    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (invoiceDto == null) {
            throw new IllegalArgumentException("invoiceDto must not be null");
        }

        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found id=" + id));

        Customer customer = getCustomer(invoiceDto.getCustomerId());
        Creancier creancier = getCreancier(invoiceDto.getCreancierId());
        PointDeVente pointDeVente = getPointDeVente(invoiceDto.getPointDeVenteId());

        invoiceMapper.updateEntity(existing, invoiceDto, customer, creancier, pointDeVente);
        Invoice saved = invoiceRepository.save(existing);
        return invoiceMapper.toDto(saved);
    }

    @Override
    //@Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Invoice entity = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found id=" + id));
        return invoiceMapper.toDto(entity);
    }

    @Override
    public void deleteInvoice(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found id=" + id);
        }

        invoiceRepository.deleteById(id);
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

    private Customer getCustomer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("customerId must not be null");
        }
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found id=" + id));
    }

    private Creancier getCreancier(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("creancierId must not be null");
        }
        return creancierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Creancier not found id=" + id));
    }

    private PointDeVente getPointDeVente(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("pointDeVenteId must not be null");
        }
        return pointDeVenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PointDeVente not found id=" + id));
    }
}
