package ma.atos.billing.invoice.billing_invoice.enties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "creancier", schema = "invoice")
public class Creancier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @SequenceGenerator(
            name = "global_seq",
            sequenceName = "global_sequence",
            allocationSize = 1
    )
    private Long id;

    private String nom;

    private String ice;

    private String rc;

    private String rib;

    private String banque;

    private String email;

    private String telephone;

    private String adresse;

    @OneToMany (mappedBy = "creancier")
    private List<Invoice> invoices ;

}