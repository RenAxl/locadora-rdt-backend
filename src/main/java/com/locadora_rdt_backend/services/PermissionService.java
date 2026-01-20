package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.PermissionDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository repository;

    @Transactional(readOnly = true)
    public List<PermissionDTO> findAll(String groupName) {

        List<Permission> list;

        if (groupName == null || groupName.trim().isEmpty()) {
            list = repository.findAllByOrderByGroupNameAscNameAsc();
        } else {
            list = repository.findByGroupNameIgnoreCaseOrderByNameAsc(groupName.trim());
        }

        return list.stream()
                .map(PermissionDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findAllGroupNames() {
        return repository.findDistinctGroupNames();
    }
}
