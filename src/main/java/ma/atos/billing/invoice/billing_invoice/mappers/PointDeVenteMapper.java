package ma.atos.billing.invoice.billing_invoice.mappers;


import ma.atos.billing.invoice.billing_invoice.dtos.AgenceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.DistributeurDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.entities.Agence;
import ma.atos.billing.invoice.billing_invoice.entities.Distributeur;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointDeVenteMapper {

    PointDeVenteDto toPointDeVenteDto(PointDeVente pointDeVente);
    AgenceDto toAgenceDto(Agence agence);
    DistributeurDto toDistributeurDto(Distributeur distributeur);

}
