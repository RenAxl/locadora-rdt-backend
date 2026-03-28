package com.locadora_rdt_backend.modules.customers.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/webp")
    );

    private static final long MAX_PHOTO_SIZE = 2L * 1024 * 1024;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<Customer> list = repository.find(name, pageRequest);
        return list.map(CustomerDTO::new);
    }

    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return new CustomerDTO(entity);
    }

    @Transactional
    public CustomerDTO insert(CustomerInsertDTO dto) {
        Customer entity = new Customer();
        copyInsertDtoToEntity(dto, entity);

        entity.setActive(false);
        entity.setCreatedAt(LocalDateTime.now());

        entity = repository.save(entity);
        return new CustomerDTO(entity);
    }

    @Transactional
    public CustomerDTO update(Long id, CustomerUpdateDTO dto) {
        try {
            Customer entity = repository.getOne(id);
            copyUpdateDtoToEntity(dto, entity);
            entity.setUpdatedAt(LocalDateTime.now());

            entity = repository.save(entity);
            return new CustomerDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void updatePhoto(Long id, MultipartFile file) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }

        validatePhoto(file);

        try {
            entity.setPhoto(file.getBytes());
            entity.setPhotoContentType(file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler bytes do arquivo.", e);
        }

        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public Customer findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    private void validatePhoto(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Use JPG, PNG ou WEBP.");
        }

        if (file.getSize() > MAX_PHOTO_SIZE) {
            throw new IllegalArgumentException("Foto muito grande. Máximo: 2MB.");
        }
    }

    private void copyInsertDtoToEntity(CustomerInsertDTO dto, Customer entity) {
        entity.setName(dto.getName());
        entity.setCpf(dto.getCpf());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
    }

    private void copyUpdateDtoToEntity(CustomerUpdateDTO dto, Customer entity) {
        entity.setName(dto.getName());
        entity.setCpf(dto.getCpf());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
    }

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
            throw new RuntimeException("Error changing user status.", e);
        }
    }

}