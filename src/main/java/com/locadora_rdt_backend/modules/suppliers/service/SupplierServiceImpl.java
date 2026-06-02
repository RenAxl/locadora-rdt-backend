package com.locadora_rdt_backend.modules.suppliers.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.suppliers.constants.SupplierErrorMessages;
import com.locadora_rdt_backend.modules.suppliers.dto.*;
import com.locadora_rdt_backend.modules.suppliers.mapper.SupplierMapper;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static final Set<String> ALLOWED_IMAGE_TYPES =
            new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));
    private static final long MAX_IMAGE_SIZE = 2L * 1024 * 1024;
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
        return mapper.toDetailsDTO(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier findEntityById(Long id) {
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
        Supplier entity = findEntityById(id);
        mapper.copyToEntity(dto, entity);
        validateUniqueFields(entity, id);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile file) {
        Supplier entity = findEntityById(id);
        validateImage(file);

        try {
            entity.setImage(file.getBytes());
            entity.setImageContentType(file.getContentType());
            entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
            repository.save(entity);
        } catch (IOException e) {
            throw new FileException("Erro ao ler a imagem enviada.");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier entity = findEntityById(id);

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

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("É obrigatório enviar uma imagem.");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new FileException("Tipo de imagem inválido. Use JPG, PNG ou WEBP.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileException("Imagem muito grande. Máximo: 2MB.");
        }
    }
}
