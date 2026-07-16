package com.locadora_rdt_backend.tests.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

class PayableServiceInterfaceTests {

    @Test
    void findAllPagedShouldCreateDefaultFilters() {
        PayableService service = Mockito.mock(PayableService.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PayableDTO> expectedPage = new PageImpl<>(Collections.emptyList());
        ArgumentCaptor<PayableFilterDTO> filterCaptor = ArgumentCaptor.forClass(PayableFilterDTO.class);

        Mockito.doCallRealMethod().when(service).findAllPaged(Mockito.anyString(), Mockito.any(PageRequest.class));
        Mockito.when(service.findAllPaged(Mockito.any(PayableFilterDTO.class), Mockito.eq(pageRequest)))
                .thenReturn(expectedPage);

        Page<PayableDTO> result = service.findAllPaged("Aluguel", pageRequest);

        Mockito.verify(service).findAllPaged(filterCaptor.capture(), Mockito.eq(pageRequest));
        PayableFilterDTO filters = filterCaptor.getValue();
        Assertions.assertSame(expectedPage, result);
        Assertions.assertEquals("Aluguel", filters.getSearch());
        Assertions.assertEquals("ALL", filters.getStatus());
        Assertions.assertEquals("DUE_DATE", filters.getPeriodType());
        Assertions.assertEquals("dueDate", filters.getOrderBy());
        Assertions.assertEquals("ASC", filters.getDirection());
    }
}
