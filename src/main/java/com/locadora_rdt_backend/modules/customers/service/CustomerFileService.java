package com.locadora_rdt_backend.modules.customers.service;

import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerFileService {

    CustomerFileDTO upload(Long customerId, String name, MultipartFile file);

    List<CustomerFileDTO> findAllByCustomer(Long customerId);

    CustomerFileViewDTO download(Long customerId, Long fileId);

    void delete(Long customerId, Long fileId);
}
