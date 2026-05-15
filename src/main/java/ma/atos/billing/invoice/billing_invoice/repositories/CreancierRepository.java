package ma.atos.billing.invoice.billing_invoice.repositories;

import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreancierRepository extends JpaRepository<Creancier, Long>, JpaSpecificationExecutor<Creancier> {
    // Now this repository can accept dynamic "Criteria" specifications!
}