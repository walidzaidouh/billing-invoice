package ma.atos.billing.invoice.billing_invoice.services.imp;

import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteType;
import ma.atos.billing.invoice.billing_invoice.entities.Agence;
import ma.atos.billing.invoice.billing_invoice.entities.Distributeur;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import ma.atos.billing.invoice.billing_invoice.mappers.PointDeVenteMapper;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PointDeVenteServiceImp implements PointDeventeService {

    private final PointDeVenteRepository repository;
    private final PointDeVenteMapper mapper;

    public PointDeVenteServiceImp(PointDeVenteRepository repository, PointDeVenteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PointDeVenteDto create(PointDeVenteDto dto) {
        PointDeVente entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public PointDeVenteDto update(Long id, PointDeVenteDto dto) {
        PointDeVente entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (dto.getType() != resolveType(entity)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        mapper.updateEntity(entity, dto);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
    }

    private PointDeVenteType resolveType(PointDeVente entity) {
        if (entity instanceof Agence) {
            return PointDeVenteType.AGENCE;
        }
        if (entity instanceof Distributeur) {
            return PointDeVenteType.DISTRIBUTEUR;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
