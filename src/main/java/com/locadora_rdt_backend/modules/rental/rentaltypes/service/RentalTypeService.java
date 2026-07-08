package com.locadora_rdt_backend.modules.rental.rentaltypes.service;

import com.locadora_rdt_backend.modules.rental.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface RentalTypeService {

    Page<RentalTypeDTO> findAllPaged(String name, PageRequest pageRequest);

    RentalTypeDetailsDTO findById(Long id);

    RentalType findEntityById(Long id);

    RentalTypeDTO insert(RentalTypeInsertDTO dto);

    RentalTypeDTO update(Long id, RentalTypeUpdateDTO dto);

    void delete(Long id);

    void deleteAll(List<Long> ids);

    void changeActiveStatus(Long id, boolean active);
}
