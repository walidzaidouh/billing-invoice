package ma.atos.billing.invoice.billing_invoice.services.imp;

import jakarta.persistence.criteria.Predicate;
import ma.atos.billing.invoice.billing_invoice.dtos.AgenceDto;
import ma.atos.billing.invoice.billing_invoice.dtos.DistributeurDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteDto;
import ma.atos.billing.invoice.billing_invoice.dtos.PointDeVenteSearchCriteria;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PointDeVenteServiceImp  implements PointDeventeService {

    private final PointDeVenteRepository pointDeVenteRepository ;
    private final PointDeVenteMapper pointDeVenteMapper;

    public PointDeVenteServiceImp(PointDeVenteRepository pointDeVenteRepository , PointDeVenteMapper pointDeVenteMapper){
        this.pointDeVenteRepository=pointDeVenteRepository;
        this.pointDeVenteMapper =pointDeVenteMapper;
    }


    @Override
    public PointDeVenteDto getPointDeVenteById(long id) {
        PointDeVente pointDeVente = pointDeVenteRepository.findById(id).orElseThrow(()-> new RuntimeException("kkkkk"));
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

            if (criteria.getRegion() != null && !criteria.getRegion().isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("region")),
                                "%" + criteria.getRegion().toLowerCase() + "%")
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PointDeVente> result = pointDeVenteRepository.findAll(spec, pageable);

        return result.map(pv -> {
            if (pv instanceof Agence agence) {
                return pointDeVenteMapper.toAgenceDto(agence);
            } else if (pv instanceof Distributeur distributeur) {
                return pointDeVenteMapper.toDistributeurDto(distributeur);
            }
            return pointDeVenteMapper.toPointDeVenteDto(pv);
        });
    }

}
