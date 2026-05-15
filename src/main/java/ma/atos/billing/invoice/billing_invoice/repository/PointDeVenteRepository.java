package ma.atos.billing.invoice.billing_invoice.repository;

import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente,Long>, JpaSpecificationExecutor<PointDeVente> {
    Optional<PointDeVente> findById(long id);
}
