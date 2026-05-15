package ma.atos.billing.invoice.billing_invoice.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgenceDto extends PointDeVenteDto {
    private String codeAgence;

    private String responsable;

    private String region;

    private String typeAgence;
}
