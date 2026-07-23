package com.locadora_rdt_backend.modules.organization.suppliers.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.organization.suppliers.constants.SupplierErrorMessages;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.mapper.SupplierMapper;
import com.locadora_rdt_backend.modules.organization.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.organization.suppliers.repository.SupplierRepository;
import com.locadora_rdt_backend.shared.service.ImageUploadSupport;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static final Long NEW_ENTITY_ID = -1L;

    private final SupplierRepository repository;
    private final SupplierMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public SupplierServiceImpl(
            SupplierRepository repository,
            SupplierMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> findAllPaged(String name, PageRequest pageRequest) {
        String normalizedName = name == null ? "" : name.trim();
        return repository.findByNameContainingIgnoreCase(normalizedName, pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDetailsDTO findById(Long id) {
        return mapper.toDetailsDTO(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier findEntityById(Long id) {
        return getEntityById(id);
    }

    private Supplier getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        SupplierErrorMessages.SUPPLIER_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public SupplierDTO insert(SupplierInsertDTO dto) {
        Supplier entity = mapper.toEntity(dto);
        validateUniqueFields(entity, NEW_ENTITY_ID);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public SupplierDTO update(Long id, SupplierUpdateDTO dto) {
        Supplier entity = getEntityById(id);
        mapper.copyToEntity(dto, entity);
        validateUniqueFields(entity, id);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile file) {
        Supplier entity = getEntityById(id);
        ImageUploadSupport.validateRequiredImage(file);
        entity.setImage(ImageUploadSupport.readBytes(file, "Erro ao ler a imagem enviada."));
        entity.setImageContentType(file.getContentType());
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier entity = getEntityById(id);

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    SupplierErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    private void validateUniqueFields(Supplier supplier, Long ignoredId) {
        if (repository.existsByCnpjAndIdNot(supplier.getCnpj(), ignoredId)) {
            throw new IllegalArgumentException("CNPJ já existe");
        }

        if (repository.existsByEmailIgnoreCaseAndIdNot(supplier.getEmail(), ignoredId)) {
            throw new IllegalArgumentException("Email já existe");
        }

        if (repository.existsByPhoneNumberAndIdNot(supplier.getPhoneNumber(), ignoredId)) {
            throw new IllegalArgumentException("Telefone já existe");
        }
    }

}
