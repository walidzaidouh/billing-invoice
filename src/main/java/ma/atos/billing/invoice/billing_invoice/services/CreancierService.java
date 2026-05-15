package ma.atos.billing.invoice.billing_invoice.services;

import ma.atos.billing.invoice.billing_invoice.dtos.CreancierDto;
import ma.atos.billing.invoice.billing_invoice.dtos.CreancierSearchCriteria;
import ma.atos.billing.invoice.billing_invoice.mappers.CreancierMapper;
import ma.atos.billing.invoice.billing_invoice.repository.CreancierRepository;
import org.springframework.data.domain.Page;

public interface CreancierService {
    public CreancierDto createCreancier(CreancierDto creancierDto);
    public Page<CreancierDto> getAllCreanciers(int page, int size);
    public CreancierDto getCreancierById(Long id);
    public CreancierDto updateCreancier(Long id, CreancierDto creancierDtoDetails);
    public void deleteCreancier(Long id);
    public Page<CreancierDto> searchCreanciers(CreancierSearchCriteria criteria, int page, int size);
}
