package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributeurDto extends PointDeVenteDto {

    private String codeDistributeur;

    private String zoneDistribution;

    private String nomCommercial;

    private Double commission;
}
