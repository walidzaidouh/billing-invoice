package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.entities.Customer;
import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import ma.atos.billing.invoice.billing_invoice.exception.ResourceNotFoundException;
import ma.atos.billing.invoice.billing_invoice.mappers.InvoiceMapper;
import ma.atos.billing.invoice.billing_invoice.messaging.InvoiceEventPublisher;
import ma.atos.billing.invoice.billing_invoice.repository.CreancierRepository;
import ma.atos.billing.invoice.billing_invoice.repository.CustomerRepository;
import ma.atos.billing.invoice.billing_invoice.repository.InvoiceRepository;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import ma.atos.billing.invoice.billing_invoice.services.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final CreancierRepository creancierRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceEventPublisher invoiceEventPublisher;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        log.info("Creating invoice reference={}", invoiceDto != null ? invoiceDto.getReference() : "null");

        if (invoiceDto == null) {
            throw new IllegalArgumentException("invoiceDto must not be null");
        }

        Customer customer     = getCustomer(invoiceDto.getCustomerId());
        Creancier creancier   = getCreancier(invoiceDto.getCreancierId());
        PointDeVente pdv      = getPointDeVente(invoiceDto.getPointDeVenteId());

        Invoice entity = invoiceMapper.toEntity(invoiceDto, customer, creancier, pdv);

        // ✅ Statut forcé à DRAFT à la création — sera mis à jour par PaymentResultListener
        entity.setStatus(StatusInvoice.DRAFT);

        Invoice saved = invoiceRepository.save(entity);
        InvoiceDto savedDto = invoiceMapper.toDto(saved);

        // ✅ Publication du PaymentRequestEvent (renommé depuis InvoiceCreatedEvent)
        invoiceEventPublisher.publishPaymentRequest(savedDto);

        log.info("Invoice created id={} status=DRAFT", saved.getId());
        return savedDto;
    }



    @Override
    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {
        log.info("Updating invoice id={}", id);

        if (id == null)         throw new IllegalArgumentException("id must not be null");
        if (invoiceDto == null) throw new IllegalArgumentException("invoiceDto must not be null");

        Invoice existing = findInvoiceOrThrow(id);

        Customer customer   = getCustomer(invoiceDto.getCustomerId());
        Creancier creancier = getCreancier(invoiceDto.getCreancierId());
        PointDeVente pdv    = getPointDeVente(invoiceDto.getPointDeVenteId());

        invoiceMapper.updateEntity(existing, invoiceDto, customer, creancier, pdv);
        return invoiceMapper.toDto(invoiceRepository.save(existing));
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        log.info("Fetching invoice id={}", id);
        if (id == null) throw new IllegalArgumentException("id must not be null");
        return invoiceMapper.toDto(findInvoiceOrThrow(id));
    }

    @Override
    public void deleteInvoice(Long id) {
        log.info("Deleting invoice id={}", id);
        if (id == null) throw new IllegalArgumentException("id must not be null");
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice not found id=" + id);
        }
        invoiceRepository.deleteById(id);
    }

    @Override
    public Page<InvoiceDto> searchByCriteria(InvoiceSearchCriteria criteria, Pageable pageable) {
        if (criteria == null) {
            return invoiceRepository.findAll(pageable).map(invoiceMapper::toDto);
        }

        Specification<Invoice> spec = (Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (criteria.getReferenceContains() != null && !criteria.getReferenceContains().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("reference")),
                        "%" + criteria.getReferenceContains().toLowerCase() + "%"
                ));
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
        };

        return invoiceRepository.findAll(spec, pageable).map(invoiceMapper::toDto);
    }

    /**
     * ✅ NOUVEAU — Appelé par PaymentResultListener pour mettre à jour le statut
     * après réception de PaymentAcceptedEvent ou PaymentRejectedEvent.
     * N'est pas exposé via REST, uniquement accessible en interne.
     */
    @Override
    public void updateInvoiceStatus(Long invoiceId, StatusInvoice newStatus) {
        log.info("Updating invoice status: invoiceId={} → status={}", invoiceId, newStatus);

        Invoice invoice = findInvoiceOrThrow(invoiceId);
        StatusInvoice previousStatus = invoice.getStatus();
        invoice.setStatus(newStatus);
        invoiceRepository.save(invoice);

        log.info("Invoice {} status updated: {} → {}", invoiceId, previousStatus, newStatus);
    }

    @Override
    public InvoiceDto findByReference(String reference) {
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("reference must not be null or blank");
        }

        Invoice entity = invoiceRepository.findByReference(reference)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with reference=" + reference));
        return invoiceMapper.toDto(entity);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Invoice findInvoiceOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found id=" + id));
    }

    private Customer getCustomer(Long id) {
        if (id == null) throw new IllegalArgumentException("customerId must not be null");
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found id=" + id));
    }

    private Creancier getCreancier(Long id) {
        if (id == null) throw new IllegalArgumentException("creancierId must not be null");
        return creancierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Creancier not found id=" + id));
    }

    private PointDeVente getPointDeVente(Long id) {
        if (id == null) throw new IllegalArgumentException("pointDeVenteId must not be null");
        return pointDeVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PointDeVente not found id=" + id));
    }
}