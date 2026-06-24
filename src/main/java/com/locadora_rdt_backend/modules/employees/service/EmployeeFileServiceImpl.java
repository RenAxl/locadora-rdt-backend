package com.locadora_rdt_backend.modules.employees.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.model.EmployeeFile;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeFileRepository;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.shared.service.StoredFileSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeFileServiceImpl implements EmployeeFileService {

    private final EmployeeFileRepository employeeFileRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeFileServiceImpl(EmployeeFileRepository employeeFileRepository,
                               EmployeeRepository employeeRepository) {
        this.employeeFileRepository = employeeFileRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeFileDTO upload(Long employeeId, String name, MultipartFile file) {
        Employee employee = getEmployeeById(employeeId);
        StoredFileSupport.validateName(name);
        StoredFileSupport.validateUpload(file);

        EmployeeFile entity = new EmployeeFile();
        entity.setEmployee(employee);
        StoredFileSupport.fillFileData(entity, name, file);

        entity = employeeFileRepository.save(entity);
        return new EmployeeFileDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFileDTO> findAllByEmployee(Long employeeId) {
        getEmployeeById(employeeId);

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
        getEmployeeById(employeeId);

        EmployeeFile entity = employeeFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + fileId));

        if (!entity.getEmployee().getId().equals(employeeId)) {
            throw new ResourceNotFoundException("Arquivo não pertence ao cliente informado.");
        }

        return entity;
    }

    @Transactional(readOnly = true)
    public Employee findEmployeeById(Long employeeId) {
        return getEmployeeById(employeeId);
    }

    private Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + employeeId));
    }
}
