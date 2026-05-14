package ma.atos.billing.invoice.billing_invoice.repository;


import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.enums.StatusInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByReference(String reference);

    List<Invoice> findByStatus(StatusInvoice status);

    List<Invoice> findByCustomer_Id(Long customerId);

    List<Invoice> findByCreancier_Id(Long creancierId);

    List<Invoice> findByPointDeVente_Id(Long pointDeVenteId);
}
