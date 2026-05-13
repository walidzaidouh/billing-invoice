package ma.atos.billing.invoice.billing_invoice.enties;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice", schema = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @SequenceGenerator(
            name = "global_seq",
            sequenceName = "global_sequence",
            allocationSize = 1
    )
    private Long id;

    private String reference;

    private LocalDate dateInvoice;

    private LocalDate dateDue;

    private Double montantHt;

    private Double montantTva;

    private Double montantTtc;

    private String status;

    private String description;


    @ManyToOne
    @JoinColumn(name = "costumer_id")
    private Customer costumer;

    @ManyToOne
    @JoinColumn(name = "creancier_id")
    private  Creancier creancier  ;

    @ManyToOne
    @JoinColumn(name = "point_de_vente_id")
    private  PointDeVente pointDeVente;

}
