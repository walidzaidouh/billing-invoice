package ma.atos.billing.invoice.billing_invoice.repository;

import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Long> , JpaSpecificationExecutor<PointDeVente> {

    List<PointDeVente> findByNomContainingIgnoreCase(String nom);

    Optional<PointDeVente> findById(long id);
}
