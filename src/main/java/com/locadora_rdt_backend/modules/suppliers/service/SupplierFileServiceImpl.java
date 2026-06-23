package com.locadora_rdt_backend.modules.suppliers.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.suppliers.constants.SupplierErrorMessages;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileViewDTO;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.model.SupplierFile;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierFileRepository;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
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
public class SupplierFileServiceImpl implements SupplierFileService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "application/pdf",
            "application/zip", "application/x-rar-compressed",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword", "text/plain",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel", "application/xml", "text/xml",
            "application/vnd.oasis.opendocument.text"
    );

    private final SupplierFileRepository fileRepository;
    private final SupplierRepository supplierRepository;

    public SupplierFileServiceImpl(
            SupplierFileRepository fileRepository,
            SupplierRepository supplierRepository
    ) {
        this.fileRepository = fileRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public SupplierFileDTO upload(Long supplierId, String name, MultipartFile file) {
        Supplier supplier = findSupplierById(supplierId);
        validateName(name);
        validateFile(file);
        String originalFilename = file.getOriginalFilename();

        SupplierFile entity = new SupplierFile();
        entity.setSupplier(supplier);
        entity.setName(name.trim());
        entity.setOriginalFileName(originalFilename);
        entity.setStoredFileName(generateStoredFileName(originalFilename));
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());

        try {
            entity.setData(file.getBytes());
        } catch (IOException e) {
            throw new FileException("Erro ao ler o arquivo enviado.");
        }

        return new SupplierFileDTO(fileRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierFileDTO> findAllBySupplier(Long supplierId) {
        findSupplierById(supplierId);
        return fileRepository.findBySupplierIdOrderByIdDesc(supplierId)
                .stream()
                .map(SupplierFileDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierFileViewDTO download(Long supplierId, Long fileId) {
        SupplierFile entity = findFileBelongsToSupplier(supplierId, fileId);
        return new SupplierFileViewDTO(
                entity.getOriginalFileName(),
                entity.getContentType(),
                entity.getData()
        );
    }

    @Override
    @Transactional
    public void delete(Long supplierId, Long fileId) {
        fileRepository.delete(findFileBelongsToSupplier(supplierId, fileId));
    }

    private SupplierFile findFileBelongsToSupplier(Long supplierId, Long fileId) {
        findSupplierById(supplierId);
        SupplierFile entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        SupplierErrorMessages.FILE_NOT_FOUND
                ));

        if (!entity.getSupplier().getId().equals(supplierId)) {
            throw new ResourceNotFoundException(
                    "Arquivo não pertence ao fornecedor informado."
            );
        }

        return entity;
    }

    private Supplier findSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        SupplierErrorMessages.SUPPLIER_NOT_FOUND
                ));
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new FileException("É obrigatório informar o nome do arquivo.");
        }
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
        return UUID.randomUUID() + "-" + normalizeFileName(originalFilename);
    }

    private String normalizeFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return normalized.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}
