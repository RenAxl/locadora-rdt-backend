package com.locadora_rdt_backend.modules.employees.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.departments.repository.DepartmentRepository;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.mapper.EmployeeMapper;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper mapper;

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/webp")
    );

    private static final long MAX_PHOTO_SIZE = 2L * 1024 * 1024;

    public EmployeeService(
            EmployeeRepository repository,
            PositionRepository positionRepository,
            DepartmentRepository departmentRepository,
            EmployeeMapper mapper
    ) {
        this.repository = repository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(name, pageRequest)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public EmployeeDetailsDTO findById(Long id) {
        Employee entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public EmployeeDTO insert(EmployeeInsertDTO dto) {
        try {
            Employee entity = mapper.toEntity(dto);

            Position position = positionRepository.getOne(dto.getPositionId());
            Department department = departmentRepository.getOne(dto.getDepartmentId());

            entity.setPosition(position);
            entity.setDepartment(department);
            entity.setCreatedBy(getAuthenticatedUsername());

            entity = repository.save(entity);

            return mapper.toDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Cargo ou setor não encontrado");
        }
    }

    @Transactional
    public EmployeeDTO update(Long id, EmployeeUpdateDTO dto) {
        try {
            Employee entity = repository.getOne(id);

            mapper.updateEntity(entity, dto);

            Position position = positionRepository.getOne(dto.getPositionId());
            Department department = departmentRepository.getOne(dto.getDepartmentId());

            entity.setPosition(position);
            entity.setDepartment(department);
            entity.setUpdatedBy(getAuthenticatedUsername());

            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Funcionário, cargo ou setor não encontrado");
        }
    }

    @Transactional
    public void updatePhoto(Long id, MultipartFile file) {
        Employee entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }

        validatePhoto(file);

        try {
            entity.setPhoto(file.getBytes());
            entity.setPhotoContentType(file.getContentType());
            entity.setUpdatedBy(getAuthenticatedUsername());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler bytes do arquivo.", e);
        }

        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public Employee findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
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
                .map(Employee::getId)
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
            throw new RuntimeException("Error changing employee status.", e);
        }
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

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        return authentication.getName();
    }
}