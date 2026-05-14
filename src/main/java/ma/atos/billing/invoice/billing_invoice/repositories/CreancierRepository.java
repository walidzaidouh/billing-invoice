package ma.atos.billing.invoice.billing_invoice.repositories;

import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreancierRepository extends JpaRepository<Creancier, Long> {
    // JpaRepository provides standard methods like save(), findAll(), findById(), deleteById() automatically.
}