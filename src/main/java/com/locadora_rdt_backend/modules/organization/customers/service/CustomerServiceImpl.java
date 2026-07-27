package com.locadora_rdt_backend.modules.organization.customers.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.organization.customers.constants.CustomerErrorMessages;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.organization.customers.mapper.CustomerMapper;
import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.shared.service.ImageUploadSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    private final CustomerMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public CustomerServiceImpl(
            CustomerRepository repository,
            CustomerMapper mapper,
            AuthenticationFacade authenticationFacade
            ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(name, pageRequest)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CustomerDetailsDTO findById(Long id) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorMessages.CUSTOMER_NOT_FOUND));
        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public CustomerDTO insert(CustomerInsertDTO dto) {
        Customer entity = mapper.toEntity(dto);

        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);

    }

    @Transactional
    public CustomerDTO update(Long id, CustomerUpdateDTO dto) {
        try {
            Customer entity = repository.getOne(id);
            mapper.updateEntity(entity, dto);
            entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void updatePhoto(Long id, MultipartFile file) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorMessages.CUSTOMER_NOT_FOUND));

        ImageUploadSupport.validatePhoto(file);
        entity.setPhoto(ImageUploadSupport.readBytes(file, "Falha ao ler bytes do arquivo."));
        entity.setPhotoContentType(file.getContentType());
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public Customer findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorMessages.CUSTOMER_NOT_FOUND));
    }




    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void deleteAll(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Lista de ids vazia");
        }

        List<Long> existingIds = repository.findAllById(ids)
                .stream()
                .map(Customer::getId)
                .collect(Collectors.toList());


        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        repository.deleteAllByIds(ids);
    }

    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        try {
            int updated = repository.updateActiveById(id, active);

            if (updated == 0) {
                throw new ResourceNotFoundException("Id not found " + id);
            }

        } catch (DataAccessException e) {
            throw new DatabaseException("Error changing customer status.");
        }
    }

}
