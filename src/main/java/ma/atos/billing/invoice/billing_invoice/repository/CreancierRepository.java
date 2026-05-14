package ma.atos.billing.invoice.billing_invoice.repository;

import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.enums.TypeCreancier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreancierRepository extends JpaRepository<Creancier, Long> {

    List<Creancier> findByTypeCreancier(TypeCreancier typeCreancier);

    List<Creancier> findByNomContainingIgnoreCase(String nom);
}
