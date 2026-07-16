package com.locadora_rdt_backend.tests.modules.rental.repository;

import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.repository.RentalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RentalRepositoryTests {

    @Autowired
    private RentalRepository repository;

    @Test
    void findFilteredShouldExecuteNativePagedQueryWithoutFilters() {
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "rental_date"
        );

        Page<Rental> result = repository.findFiltered(
                "",
                "",
                "",
                -1L,
                Instant.parse("1900-01-01T00:00:00Z"),
                Instant.parse("2999-12-31T23:59:59Z"),
                pageRequest
        );

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }
}
