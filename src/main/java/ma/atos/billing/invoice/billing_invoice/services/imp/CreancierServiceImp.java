package ma.atos.billing.invoice.billing_invoice.services.imp;

import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.dtos.CreancierSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.mappers.CreancierMapper;
import ma.atos.billing.invoice.billing_invoice.repository.CreancierRepository;
import ma.atos.billing.invoice.billing_invoice.services.PointDeventeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ma.atos.billing.invoice.billing_invoice.services.CreancierService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreancierServiceImp implements CreancierService {

    private final CreancierRepository creancierRepository;
    private final CreancierMapper creancierMapper;

    public CreancierServiceImp(CreancierRepository creancierRepository, CreancierMapper creancierMapper) {
        this.creancierRepository = creancierRepository;
        this.creancierMapper = creancierMapper;
    }

    // CREATE
    public CreancierDto createCreancier(CreancierDto creancierDto) {
        Creancier entityToSave = creancierMapper.fromDtoToEntity(creancierDto);
        Creancier savedEntity = creancierRepository.save(entityToSave);
        return creancierMapper.fromEntityToDto(savedEntity);
    }

    // READ (Paginated) -> THIS IS THE CHANGED METHOD
    public Page<CreancierDto> getAllCreanciers(int page, int size) {
        // Create a Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Fetch the page from the database
        Page<Creancier> creancierPage = creancierRepository.findAll(pageable);

        // The Page object has a built-in .map() function to convert Entities to DTOs!
        return creancierPage.map(creancierMapper::fromEntityToDto);
    }

    // READ (By ID)
    @Cacheable(value = "creanciers", key = "#id")
    public CreancierDto getCreancierById(Long id) {
        Creancier entity = creancierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creancier not found with id " + id));
        return creancierMapper.fromEntityToDto(entity);
    }

    // UPDATE
    @CacheEvict(value = "creanciers", key = "#id")
    public CreancierDto updateCreancier(Long id, CreancierDto creancierDtoDetails) {
        Creancier existingCreancier = creancierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creancier not found with id " + id));

        existingCreancier.setNom(creancierDtoDetails.getNom());
        existingCreancier.setTypeCreancier(creancierDtoDetails.getTypeCreancier());
        existingCreancier.setIce(creancierDtoDetails.getIce());
        existingCreancier.setRc(creancierDtoDetails.getRc());
        existingCreancier.setRib(creancierDtoDetails.getRib());
        existingCreancier.setBanque(creancierDtoDetails.getBanque());
        existingCreancier.setEmail(creancierDtoDetails.getEmail());
        existingCreancier.setTelephone(creancierDtoDetails.getTelephone());
        existingCreancier.setAdresse(creancierDtoDetails.getAdresse());

        Creancier updatedEntity = creancierRepository.save(existingCreancier);
        return creancierMapper.fromEntityToDto(updatedEntity);
    }

    // DELETE
    @CacheEvict(value = "creanciers", key = "#id")
    public void deleteCreancier(Long id) {
        creancierRepository.deleteById(id);
    }

    public Page<CreancierDto> searchCreanciers(CreancierSearchCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Creancier> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // If 'nom' is provided, add it to the query (using LIKE for partial matches)
            if (criteria.getNom() != null && !criteria.getNom().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), "%" + criteria.getNom().toLowerCase() + "%"));
            }

            // If 'typeCreancier' is provided, add an exact match
            if (criteria.getTypeCreancier() != null) {
                predicates.add(criteriaBuilder.equal(root.get("typeCreancier"), criteria.getTypeCreancier()));
            }

            // If 'ice' is provided
            if (criteria.getIce() != null && !criteria.getIce().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ice"), criteria.getIce()));
            }

            // Combine all the "if" statements with AND (Foolproof way)
            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            return criteriaBuilder.and(predicateArray);
        };

        // Execute the dynamic query!
        Page<Creancier> result = creancierRepository.findAll(spec, pageable);
        return result.map(creancierMapper::fromEntityToDto);
    }

}