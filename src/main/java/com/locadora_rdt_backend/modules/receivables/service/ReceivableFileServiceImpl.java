package com.locadora_rdt_backend.modules.receivables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableFileDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableFileViewDTO;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.receivables.model.ReceivableFile;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableFileRepository;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceivableFileServiceImpl implements ReceivableFileService {

    private final ReceivableFileRepository fileRepository;
    private final ReceivableRepository receivableRepository;

    public ReceivableFileServiceImpl(
            ReceivableFileRepository fileRepository,
            ReceivableRepository receivableRepository
    ) {
        this.fileRepository = fileRepository;
        this.receivableRepository = receivableRepository;
    }

    @Override
    @Transactional
    public ReceivableFileDTO upload(Long receivableId, String name, MultipartFile file) {
        Receivable receivable = getReceivable(receivableId);

        ReceivableFile entity = new ReceivableFile();
        entity.setReceivable(receivable);
        StoredFileSupport.fillFileData(entity, name, file);

        receivable.setFileName(entity.getOriginalFileName());
        fileRepository.save(entity);

        return new ReceivableFileDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceivableFileDTO> findAllByReceivable(Long receivableId) {
        getReceivable(receivableId);

        return fileRepository.findByReceivableIdOrderByIdDesc(receivableId)
                .stream()
                .map(ReceivableFileDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivableFileViewDTO download(Long receivableId, Long fileId) {
        ReceivableFile entity = findFileBelongsToReceivable(receivableId, fileId);
        return new ReceivableFileViewDTO(entity.getOriginalFileName(), entity.getContentType(), entity.getData());
    }

    @Override
    @Transactional
    public void delete(Long receivableId, Long fileId) {
        ReceivableFile entity = findFileBelongsToReceivable(receivableId, fileId);
        fileRepository.delete(entity);
    }

    private ReceivableFile findFileBelongsToReceivable(Long receivableId, Long fileId) {
        getReceivable(receivableId);

        ReceivableFile entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId));

        if (!entity.getReceivable().getId().equals(receivableId)) {
            throw new ResourceNotFoundException("Arquivo não pertence à conta informada.");
        }

        return entity;
    }

    private Receivable getReceivable(Long receivableId) {
        return receivableRepository.findById(receivableId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a receber não encontrada. Id: " + receivableId));
    }
}
