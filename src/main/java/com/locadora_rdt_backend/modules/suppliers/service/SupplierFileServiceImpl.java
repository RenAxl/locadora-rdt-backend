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
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierFileServiceImpl implements SupplierFileService {

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
        StoredFileSupport.validateName(name);
        StoredFileSupport.validateUpload(file);

        SupplierFile entity = new SupplierFile();
        entity.setSupplier(supplier);
        StoredFileSupport.fillFileData(entity, name, file);

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
}
