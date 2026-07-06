package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFileDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.model.ReceivableFile;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableFileRepository;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        List<ReceivableFile> files = fileRepository.findByReceivableIdOrderByIdDesc(receivableId);
        List<ReceivableFileDTO> result = new ArrayList<>();

        for (ReceivableFile file : files) {
            ReceivableFileDTO dto = new ReceivableFileDTO(file);
            result.add(dto);
        }

        return result;
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

        Optional<ReceivableFile> optionalFile = fileRepository.findById(fileId);

        if (optionalFile.isEmpty()) {
            throw new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId);
        }

        ReceivableFile entity = optionalFile.get();

        if (!entity.getReceivable().getId().equals(receivableId)) {
            throw new ResourceNotFoundException("Arquivo não pertence à conta informada.");
        }

        return entity;
    }

    private Receivable getReceivable(Long receivableId) {
        Optional<Receivable> optionalReceivable = receivableRepository.findById(receivableId);

        if (optionalReceivable.isEmpty()) {
            throw new ResourceNotFoundException("Conta a receber não encontrada. Id: " + receivableId);
        }

        return optionalReceivable.get();
    }
}
