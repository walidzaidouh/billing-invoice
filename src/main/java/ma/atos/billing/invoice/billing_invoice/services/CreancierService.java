package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.entities.Creancier;
import ma.atos.billing.invoice.billing_invoice.mappers.CreancierMapper;
import ma.atos.billing.invoice.billing_invoice.repositories.CreancierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreancierService {

    private final CreancierRepository creancierRepository;
    private final CreancierMapper creancierMapper; // Inject the mapper

    public CreancierService(CreancierRepository creancierRepository, CreancierMapper creancierMapper) {
        this.creancierRepository = creancierRepository;
        this.creancierMapper = creancierMapper;
    }

    // CREATE
    public CreancierDto createCreancier(CreancierDto creancierDto) {
        Creancier entityToSave = creancierMapper.fromDtoToEntity(creancierDto);
        Creancier savedEntity = creancierRepository.save(entityToSave);
        return creancierMapper.fromEntityToDto(savedEntity);
    }

    // READ (All)
    public List<CreancierDto> getAllCreanciers() {
        return creancierRepository.findAll()
                .stream()
                .map(creancierMapper::fromEntityToDto)
                .collect(Collectors.toList());
    }

    // READ (By ID)
    public CreancierDto getCreancierById(Long id) {
        Creancier entity = creancierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creancier not found with id " + id));
        return creancierMapper.fromEntityToDto(entity);
    }

    // UPDATE
    public CreancierDto updateCreancier(Long id, CreancierDto creancierDtoDetails) {
        Creancier existingCreancier = creancierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creancier not found with id " + id));

        // Update fields
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
    public void deleteCreancier(Long id) {
        creancierRepository.deleteById(id);
    }
}