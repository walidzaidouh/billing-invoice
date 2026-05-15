package ma.atos.billing.invoice.billing_invoice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.atos.billing.invoice.billing_invoice.enums.TypeCreancier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreancierDto {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le type de créancier est obligatoire")
    private TypeCreancier typeCreancier;

    private String ice;
    private String rc;
    private String rib;
    private String banque;
    private String email;
    private String telephone;
    private String adresse;

    // Notice we DO NOT include the List<Invoice> here!
    // This keeps the JSON response clean and prevents infinite loops.
}