package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;

public interface PointDeventeService {

    PointDeVenteDto create(PointDeVenteDto dto);

    PointDeVenteDto update(Long id, PointDeVenteDto dto);

    void delete(Long id);
}
