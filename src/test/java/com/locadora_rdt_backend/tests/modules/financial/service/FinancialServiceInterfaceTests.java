package com.locadora_rdt_backend.tests.modules.financial.service;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class FinancialServiceInterfaceTests {

    @Test
    void payableServiceDefaultFindAllPagedShouldBuildFilters() {
        PayableService service = Mockito.mock(PayableService.class, Mockito.CALLS_REAL_METHODS);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.doReturn(Page.<PayableDTO>empty())
                .when(service)
                .findAllPaged(Mockito.any(PayableFilterDTO.class), Mockito.eq(pageRequest));

        service.findAllPaged("aluguel", pageRequest);

        ArgumentCaptor<PayableFilterDTO> captor = ArgumentCaptor.forClass(PayableFilterDTO.class);
        Mockito.verify(service).findAllPaged(captor.capture(), Mockito.eq(pageRequest));

        PayableFilterDTO filters = captor.getValue();
        Assertions.assertEquals("aluguel", filters.getSearch());
        Assertions.assertEquals("ALL", filters.getStatus());
        Assertions.assertEquals("DUE_DATE", filters.getPeriodType());
        Assertions.assertEquals("dueDate", filters.getOrderBy());
        Assertions.assertEquals("ASC", filters.getDirection());
    }

    @Test
    void receivableServiceDefaultFindAllPagedShouldBuildFilters() {
        ReceivableService service = Mockito.mock(ReceivableService.class, Mockito.CALLS_REAL_METHODS);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.doReturn(Page.<ReceivableDTO>empty())
                .when(service)
                .findAllPaged(Mockito.any(ReceivableFilterDTO.class), Mockito.eq(pageRequest));

        service.findAllPaged("cliente", pageRequest);

        ArgumentCaptor<ReceivableFilterDTO> captor = ArgumentCaptor.forClass(ReceivableFilterDTO.class);
        Mockito.verify(service).findAllPaged(captor.capture(), Mockito.eq(pageRequest));

        ReceivableFilterDTO filters = captor.getValue();
        Assertions.assertEquals("cliente", filters.getSearch());
        Assertions.assertEquals("ALL", filters.getStatus());
        Assertions.assertEquals("DUE_DATE", filters.getPeriodType());
        Assertions.assertEquals("dueDate", filters.getOrderBy());
        Assertions.assertEquals("ASC", filters.getDirection());
    }
}
