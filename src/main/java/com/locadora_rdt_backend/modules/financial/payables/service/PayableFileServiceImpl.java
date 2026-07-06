package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.model.PayableFile;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableFileRepository;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PayableFileServiceImpl implements PayableFileService {

    private final PayableFileRepository fileRepository;
    private final PayableRepository payableRepository;

    public PayableFileServiceImpl(
            PayableFileRepository fileRepository,
            PayableRepository payableRepository
    ) {
        this.fileRepository = fileRepository;
        this.payableRepository = payableRepository;
    }

    @Override
    @Transactional
    public PayableFileDTO upload(Long payableId, String name, MultipartFile file) {
        Payable payable = getPayable(payableId);

        PayableFile entity = new PayableFile();
        entity.setPayable(payable);
        StoredFileSupport.fillFileData(entity, name, file);

        payable.setFileName(entity.getOriginalFileName());
        fileRepository.save(entity);

        return new PayableFileDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayableFileDTO> findAllByPayable(Long payableId) {
        getPayable(payableId);

        List<PayableFile> files = fileRepository.findByPayableIdOrderByIdDesc(payableId);
        List<PayableFileDTO> result = new ArrayList<>();

        for (PayableFile file : files) {
            PayableFileDTO dto = new PayableFileDTO(file);
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PayableFileViewDTO download(Long payableId, Long fileId) {
        PayableFile entity = findFileBelongsToPayable(payableId, fileId);
        return new PayableFileViewDTO(entity.getOriginalFileName(), entity.getContentType(), entity.getData());
    }

    @Override
    @Transactional
    public void delete(Long payableId, Long fileId) {
        PayableFile entity = findFileBelongsToPayable(payableId, fileId);
        fileRepository.delete(entity);
    }

    private PayableFile findFileBelongsToPayable(Long payableId, Long fileId) {
        getPayable(payableId);

        Optional<PayableFile> optionalFile = fileRepository.findById(fileId);

        if (optionalFile.isEmpty()) {
            throw new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId);
        }

        PayableFile entity = optionalFile.get();

        if (!entity.getPayable().getId().equals(payableId)) {
            throw new ResourceNotFoundException("Arquivo não pertence à conta informada.");
        }

        return entity;
    }

    private Payable getPayable(Long payableId) {
        Optional<Payable> optionalPayable = payableRepository.findById(payableId);

        if (optionalPayable.isEmpty()) {
            throw new ResourceNotFoundException("Conta a pagar não encontrada. Id: " + payableId);
        }

        return optionalPayable.get();
    }
}
