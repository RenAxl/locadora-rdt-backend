package com.locadora_rdt_backend.modules.employees.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.dto.file.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.file.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.model.EmployeeFile;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeFileRepository;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
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
public class EmployeeFileService {

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

    private final EmployeeFileRepository employeeFileRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeFileService(EmployeeFileRepository employeeFileRepository,
                               EmployeeRepository employeeRepository) {
        this.employeeFileRepository = employeeFileRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeFileDTO upload(Long employeeId, String name, MultipartFile file) {
        Employee employee = findEmployeeById(employeeId);
        validateFile(file);

        EmployeeFile entity = new EmployeeFile();
        entity.setEmployee(employee);
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

        entity = employeeFileRepository.save(entity);
        return new EmployeeFileDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFileDTO> findAllByEmployee(Long employeeId) {
        findEmployeeById(employeeId);

        return employeeFileRepository.findByEmployeeIdOrderByIdDesc(employeeId)
                .stream()
                .map(EmployeeFileDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public EmployeeFileViewDTO download(Long employeeId, Long fileId) {
        EmployeeFile entity = findFileBelongsToEmployee(employeeId, fileId);
        return new EmployeeFileViewDTO(
                entity.getOriginalFileName(),
                entity.getContentType(),
                entity.getData()
        );
    }


    @Transactional
    public void delete(Long employeeId, Long fileId) {
        EmployeeFile entity = findFileBelongsToEmployee(employeeId, fileId);
        employeeFileRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public EmployeeFile findFileBelongsToEmployee(Long employeeId, Long fileId) {
        findEmployeeById(employeeId);

        EmployeeFile entity = employeeFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId));

        if (!entity.getEmployee().getId().equals(employeeId)) {
            throw new ResourceNotFoundException("Arquivo não pertence ao cliente informado.");
        }

        return entity;
    }

    @Transactional(readOnly = true)
    public Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + employeeId));
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