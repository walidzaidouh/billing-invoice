package ma.atos.billing.invoice.billing_invoice.repository;

import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Long> {

    List<PointDeVente> findByNomContainingIgnoreCase(String nom);

    Optional<PointDeVente> findById(long id);
}
