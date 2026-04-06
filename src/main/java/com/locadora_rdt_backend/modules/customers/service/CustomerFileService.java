package com.locadora_rdt_backend.modules.customers.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileResponseDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.model.CustomerFile;
import com.locadora_rdt_backend.modules.customers.repository.CustomerFileRepository;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerFileService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/zip",
            "application/x-rar-compressed",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "text/plain",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel",
            "application/xml",
            "text/xml",
            "application/vnd.oasis.opendocument.text"
    );

    private final CustomerFileRepository customerFileRepository;
    private final CustomerRepository customerRepository;

    public CustomerFileService(CustomerFileRepository customerFileRepository,
                               CustomerRepository customerRepository) {
        this.customerFileRepository = customerFileRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerFileDTO upload(Long customerId, String name, MultipartFile file) {
        Customer customer = findCustomerById(customerId);
        validateFile(file);

        CustomerFile entity = new CustomerFile();
        entity.setCustomer(customer);
        entity.setName(name.trim());
        entity.setOriginalFileName(file.getOriginalFilename());
        entity.setStoredFileName(generateStoredFileName(file.getOriginalFilename()));
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());

        try {
            entity.setData(file.getBytes());
        } catch (IOException e) {
            throw new FileException("Erro ao ler o arquivo enviado.");
        }

        entity = customerFileRepository.save(entity);
        return new CustomerFileDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerFileDTO> findAllByCustomer(Long customerId) {
        findCustomerById(customerId);

        return customerFileRepository.findByCustomerIdOrderByIdDesc(customerId)
                .stream()
                .map(CustomerFileDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CustomerFileResponseDTO download(Long customerId, Long fileId) {
        CustomerFile entity = findFileBelongsToCustomer(customerId, fileId);
        return new CustomerFileResponseDTO(
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
    public Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + customerId));
    }

    @Transactional(readOnly = true)
    public CustomerFile findFileBelongsToCustomer(Long customerId, Long fileId) {
        findCustomerById(customerId);

        CustomerFile entity = customerFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId));

        if (!entity.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Arquivo não pertence ao cliente informado.");
        }

        return entity;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("É obrigatório enviar um arquivo.");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new FileException("Nome original do arquivo é inválido.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileException("O arquivo excede o tamanho máximo permitido de 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileException("Tipo de arquivo não permitido.");
        }
    }

    private String generateStoredFileName(String originalFilename) {
        String cleanName = normalizeFileName(originalFilename);
        return UUID.randomUUID().toString() + "-" + cleanName;
    }

    private String normalizeFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return normalized.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}