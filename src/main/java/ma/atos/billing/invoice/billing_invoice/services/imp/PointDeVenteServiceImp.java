package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.criteria.Predicate;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteType;
import ma.atos.billing.invoice.billing_invoice.entities.Agence;
import ma.atos.billing.invoice.billing_invoice.entities.Distributeur;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import ma.atos.billing.invoice.billing_invoice.mappers.PointDeVenteMapper;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
    }

    @Override
    public PointDeVenteDto getPointDeVenteById(long id) {
        PointDeVente pointDeVente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToTypedDto(pointDeVente);
    }

    @Override
    public Page<PointDeVenteDto> searchPointDeVente(PointDeVenteSearchCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<PointDeVente> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(criteria.getNom())) {
                predicates.add(cb.like(cb.lower(root.get("nom")), likeValue(criteria.getNom())));
            }

            if (hasText(criteria.getAdresse())) {
                predicates.add(cb.like(cb.lower(root.get("adresse")), likeValue(criteria.getAdresse())));
            }

            if (hasText(criteria.getTelephone())) {
                predicates.add(cb.like(cb.lower(root.get("telephone")), likeValue(criteria.getTelephone())));
            }

            if (hasText(criteria.getType_point_de_vente())) {
                if ("AGENCE".equalsIgnoreCase(criteria.getType_point_de_vente())) {
                    predicates.add(cb.equal(root.type(), Agence.class));
                } else if ("DISTRIBUTEUR".equalsIgnoreCase(criteria.getType_point_de_vente())) {
                    predicates.add(cb.equal(root.type(), Distributeur.class));
                }
            }

            if (hasText(criteria.getCodeAgence())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Agence.class).get("codeAgence")), likeValue(criteria.getCodeAgence())));
            }

            if (hasText(criteria.getResponsable())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Agence.class).get("responsable")), likeValue(criteria.getResponsable())));
            }

            if (hasText(criteria.getRegion())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Agence.class).get("region")), likeValue(criteria.getRegion())));
            }

            if (hasText(criteria.getTypeAgence())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Agence.class).get("typeAgence")), likeValue(criteria.getTypeAgence())));
            }

            if (hasText(criteria.getCodeDistributeur())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Distributeur.class).get("codeDistributeur")), likeValue(criteria.getCodeDistributeur())));
            }

            if (hasText(criteria.getZoneDistribution())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Distributeur.class).get("zoneDistribution")), likeValue(criteria.getZoneDistribution())));
            }

            if (hasText(criteria.getNomCommercial())) {
                predicates.add(cb.like(cb.lower(cb.treat(root, Distributeur.class).get("nomCommercial")), likeValue(criteria.getNomCommercial())));
            }

            if (criteria.getCommission() != null) {
                predicates.add(cb.equal(cb.treat(root, Distributeur.class).get("commission"), criteria.getCommission()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(this::mapToTypedDto);
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

    private PointDeVenteDto mapToTypedDto(PointDeVente pointDeVente) {
        if (pointDeVente instanceof Agence agence) {
            return mapper.toAgenceDto(agence);
        }
        if (pointDeVente instanceof Distributeur distributeur) {
            return mapper.toDistributeurDto(distributeur);
        }
        return mapper.toPointDeVenteDto(pointDeVente);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String likeValue(String value) {
        return "%" + value.toLowerCase() + "%";
    }
}
