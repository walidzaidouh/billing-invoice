package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.criteria.Predicate;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteType;
import ma.atos.billing.invoice.billing_invoice.dtos.AgenceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.DistributeurDto;
import ma.atos.billing.invoice.billing_invoice.entities.Agence;
import ma.atos.billing.invoice.billing_invoice.entities.Distributeur;
import ma.atos.billing.invoice.billing_invoice.entities.PointDeVente;
import ma.atos.billing.invoice.billing_invoice.mappers.PointDeVenteMapper;
import ma.atos.billing.invoice.billing_invoice.repository.PointDeVenteRepository;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

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
    @CacheEvict(value = "pointsDeVente-list", allEntries = true)
    public PointDeVenteDto create(PointDeVenteDto dto) {
        PointDeVente entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    @CachePut(value = "pointDeVente", key = "#id")
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
    @CacheEvict(value = "pointDeVente", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
    }
  
    @Override
    @Cacheable(value = "pointDeVente" , key = "#id")
    public PointDeVenteDto getPointDeVenteById(long id) {
        System.out.println("before call find by id");
        PointDeVente pointDeVente = pointDeVenteRepository.findById(id).orElseThrow(()-> new RuntimeException("point de vente not found"));

        System.out.println("after call find by id");

        if(pointDeVente instanceof Agence agence){
            AgenceDto agenceDto = pointDeVenteMapper.toAgenceDto(agence);
            return agenceDto;
        }

        if(pointDeVente instanceof Distributeur distributeur){
            DistributeurDto distributeurDto = pointDeVenteMapper.toDistributeurDto(distributeur);
            return distributeurDto;
        }



        return pointDeVenteMapper.toPointDeVenteDto(pointDeVente);

    }

    @Override
    @Cacheable(value = "pointDeVenteList" , key = "#criteria.nom + '-' + #criteria.type_point_de_vente + '-' + #criteria.region + '-' + #page")
    public Page<PointDeVenteDto> searchPointDeVente(PointDeVenteSearchCriteria criteria, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<PointDeVente> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getNom() != null && !criteria.getNom().isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("nom")),
                                "%" + criteria.getNom().toLowerCase() + "%")
                );
            }

            if (criteria.getType_point_de_vente() != null && !criteria.getType_point_de_vente().isEmpty()) {

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
