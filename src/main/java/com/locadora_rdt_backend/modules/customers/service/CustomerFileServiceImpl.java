package com.locadora_rdt_backend.modules.customers.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.model.CustomerFile;
import com.locadora_rdt_backend.modules.customers.repository.CustomerFileRepository;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerFileServiceImpl implements CustomerFileService {

    private final CustomerFileRepository customerFileRepository;
    private final CustomerRepository customerRepository;

    public CustomerFileServiceImpl(CustomerFileRepository customerFileRepository,
                               CustomerRepository customerRepository) {
        this.customerFileRepository = customerFileRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerFileDTO upload(Long customerId, String name, MultipartFile file) {
        Customer customer = getCustomerById(customerId);
        StoredFileSupport.validateName(name);
        StoredFileSupport.validateUpload(file);

        CustomerFile entity = new CustomerFile();
        entity.setCustomer(customer);
        StoredFileSupport.fillFileData(entity, name, file);

        entity = customerFileRepository.save(entity);
        return new CustomerFileDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerFileDTO> findAllByCustomer(Long customerId) {
        getCustomerById(customerId);

        return customerFileRepository.findByCustomerIdOrderByIdDesc(customerId)
                .stream()
                .map(CustomerFileDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CustomerFileViewDTO download(Long customerId, Long fileId) {
        CustomerFile entity = findFileBelongsToCustomer(customerId, fileId);
        return new CustomerFileViewDTO(
                entity.getOriginalFileName(),
                entity.getContentType(),
                entity.getData()
        );
    }


    @Transactional
    public void delete(Long customerId, Long fileId) {
        CustomerFile entity = findFileBelongsToCustomer(customerId, fileId);
        customerFileRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public CustomerFile findFileBelongsToCustomer(Long customerId, Long fileId) {
        getCustomerById(customerId);

        CustomerFile entity = customerFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId));

        if (!entity.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Arquivo não pertence ao cliente informado.");
        }

        return entity;
    }

    @Transactional(readOnly = true)
    public Customer findCustomerById(Long customerId) {
        return getCustomerById(customerId);
    }

    private Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + customerId));
    }
}
