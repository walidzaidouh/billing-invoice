package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.Data;
import ma.atos.billing.invoice.billing_invoice.enums.TypeCreancier;

@Data
public class CreancierSearchCriteria {
    private String nom;
    private TypeCreancier typeCreancier;
    private String ice;
    private String banque;
}