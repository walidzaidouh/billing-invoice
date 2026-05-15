package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.controllers.PointDeVenteController;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import org.springframework.data.domain.Page;

public interface PointDeventeService {
    public PointDeVenteDto getPointDeVenteById(long id);
    public Page<PointDeVenteDto> searchPointDeVente(PointDeVenteSearchCriteria criteria, int page, int size);

    }
