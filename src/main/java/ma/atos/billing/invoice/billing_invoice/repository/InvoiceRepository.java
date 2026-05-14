package ma.atos.billing.invoice.billing_invoice.repository;


import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.enums.ModeReglement;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByReference(String reference);

    List<Invoice> findByStatus(StatusInvoice status);

    List<Invoice> findByCustomer_Id(Long customerId);

    List<Invoice> findByCreancier_Id(Long creancierId);

    List<Invoice> findByPointDeVente_Id(Long pointDeVenteId);

    @Query("""
            select i
            from Invoice i
            where (:referenceContains is null or lower(i.reference) like lower(concat('%', :referenceContains, '%')))
              and (:status is null or i.status = :status)
              and (:modeReglement is null or i.modeReglement = :modeReglement)
              and (:customerId is null or i.customer.id = :customerId)
              and (:creancierId is null or i.creancier.id = :creancierId)
              and (:pointDeVenteId is null or i.pointDeVente.id = :pointDeVenteId)
              and (:dateInvoiceFrom is null or i.dateInvoice >= :dateInvoiceFrom)
              and (:dateInvoiceTo is null or i.dateInvoice <= :dateInvoiceTo)
              and (:dateDueFrom is null or i.dateDue >= :dateDueFrom)
              and (:dateDueTo is null or i.dateDue <= :dateDueTo)
              and (:montantTtcMin is null or i.montantTtc >= :montantTtcMin)
              and (:montantTtcMax is null or i.montantTtc <= :montantTtcMax)
            """)
    Page<Invoice> searchByCriteria(
            @Param("referenceContains") String referenceContains,
            @Param("status") StatusInvoice status,
            @Param("modeReglement") ModeReglement modeReglement,
            @Param("customerId") Long customerId,
            @Param("creancierId") Long creancierId,
            @Param("pointDeVenteId") Long pointDeVenteId,
            @Param("dateInvoiceFrom") LocalDate dateInvoiceFrom,
            @Param("dateInvoiceTo") LocalDate dateInvoiceTo,
            @Param("dateDueFrom") LocalDate dateDueFrom,
            @Param("dateDueTo") LocalDate dateDueTo,
            @Param("montantTtcMin") Double montantTtcMin,
            @Param("montantTtcMax") Double montantTtcMax,
            Pageable pageable
    );
}
