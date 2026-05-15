package ma.atos.billing.invoice.billing_invoice.mappers;

import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceDto;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.entities.Customer;
import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "creancierId", source = "creancier.id")
    @Mapping(target = "pointDeVenteId", source = "pointDeVente.id")
    InvoiceDto toDto(Invoice entity);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "creancier", ignore = true)
    @Mapping(target = "pointDeVente", ignore = true)
    Invoice toEntity(InvoiceDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "creancier", ignore = true)
    @Mapping(target = "pointDeVente", ignore = true)
    void updateEntityFromDto(@MappingTarget Invoice entity, InvoiceDto dto);

    default Invoice toEntity(
            InvoiceDto dto,
            Customer customer,
            Creancier creancier,
            PointDeVente pointDeVente
    ) {
        if (dto == null) {
            return null;
        }

        Invoice entity = toEntity(dto);
        entity.setCustomer(customer);
        entity.setCreancier(creancier);
        entity.setPointDeVente(pointDeVente);

        return entity;
    }

    default void updateEntity(
            @MappingTarget Invoice entity,
            InvoiceDto dto,
            Customer customer,
            Creancier creancier,
            PointDeVente pointDeVente
    ) {
        if (entity == null || dto == null) {
            return;
        }

        updateEntityFromDto(entity, dto);
        entity.setCustomer(customer);
        entity.setCreancier(creancier);
        entity.setPointDeVente(pointDeVente);
    }
}