package com.locadora_rdt_backend.tests.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

class ReceivableServiceInterfaceTests {

    @Test
    void findAllPagedShouldCreateDefaultFilters() {
        ReceivableService service = Mockito.mock(ReceivableService.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ReceivableDTO> expectedPage = new PageImpl<>(Collections.emptyList());
        ArgumentCaptor<ReceivableFilterDTO> filterCaptor = ArgumentCaptor.forClass(ReceivableFilterDTO.class);

        Mockito.doCallRealMethod().when(service).findAllPaged(Mockito.anyString(), Mockito.any(PageRequest.class));
        Mockito.when(service.findAllPaged(Mockito.any(ReceivableFilterDTO.class), Mockito.eq(pageRequest)))
                .thenReturn(expectedPage);

        Page<ReceivableDTO> result = service.findAllPaged("Cliente", pageRequest);

        Mockito.verify(service).findAllPaged(filterCaptor.capture(), Mockito.eq(pageRequest));
        ReceivableFilterDTO filters = filterCaptor.getValue();
        Assertions.assertSame(expectedPage, result);
        Assertions.assertEquals("Cliente", filters.getSearch());
        Assertions.assertEquals("ALL", filters.getStatus());
        Assertions.assertEquals("DUE_DATE", filters.getPeriodType());
        Assertions.assertEquals("dueDate", filters.getOrderBy());
        Assertions.assertEquals("ASC", filters.getDirection());
    }
}
