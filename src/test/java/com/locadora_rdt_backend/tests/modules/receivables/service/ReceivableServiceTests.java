package com.locadora_rdt_backend.tests.modules.receivables.service;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReceivableServiceTests {

    @InjectMocks
    private ReceivableServiceImpl service;

    @Mock
    private ReceivableRepository repository;

    @Mock
    private ReceivableMapper mapper;

    private Receivable entity;
    private ReceivableDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Receivable();
        entity.setId(1L);
        entity.setDescription("Movie rental");
        entity.setAmount(new BigDecimal("45.90"));

        dto = new ReceivableDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Receivable> page = new PageImpl<>(List.of(entity));

        Mockito.when(repository.find("Movie", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<ReceivableDTO> result = service.findAllPaged("Movie", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1L, result.getContent().get(0).getId());
        Mockito.verify(repository).find("Movie", pageRequest);
    }
}
