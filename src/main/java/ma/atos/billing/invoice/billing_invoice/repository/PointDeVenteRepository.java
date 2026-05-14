package ma.atos.billing.invoice.billing_invoice.repository;

import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Long> {

    List<PointDeVente> findByNomContainingIgnoreCase(String nom);
}
