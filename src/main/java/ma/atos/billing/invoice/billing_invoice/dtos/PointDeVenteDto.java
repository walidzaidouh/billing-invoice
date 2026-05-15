package ma.atos.billing.invoice.billing_invoice.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.entities.Invoice;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDeVenteDto {

    private Long id;

    private String nom;

    private String adresse;

    private  PointDeVenteType type ;

    private String telephone;

    private Date createdDate;

    private Date updatedDate;


}
