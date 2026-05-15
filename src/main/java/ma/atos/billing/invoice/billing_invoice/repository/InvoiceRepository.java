package ma.atos.billing.invoice.billing_invoice.repository;


import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.enums.ModeReglement;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> , JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByReference(String reference);

    List<Invoice> findByStatus(StatusInvoice status);

    List<Invoice> findByCustomer_Id(Long customerId);

    List<Invoice> findByCreancier_Id(Long creancierId);

    List<Invoice> findByPointDeVente_Id(Long pointDeVenteId);

}
