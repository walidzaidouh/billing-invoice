package ma.atos.billing.invoice.billing_invoice.mappers;

import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import org.mapstruct.Mapper;

// componentModel = "spring" tells MapStruct to generate a class marked with @Component
@Mapper(componentModel = "spring")
public interface CreancierMapper {

    // MapStruct will automatically write the implementation for this!
    CreancierDto fromEntityToDto(Creancier entity);

    // MapStruct will automatically write the implementation for this!
    Creancier fromDtoToEntity(CreancierDto dto);
}