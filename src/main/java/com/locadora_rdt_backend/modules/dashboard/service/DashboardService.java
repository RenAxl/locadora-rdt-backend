package com.locadora_rdt_backend.modules.dashboard.service;

import com.locadora_rdt_backend.modules.dashboard.dto.DashboardDailyRentalDTO;
import com.locadora_rdt_backend.modules.dashboard.dto.DashboardSummaryDTO;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.rentals.rental.repository.ItemUnitRepository;
import com.locadora_rdt_backend.modules.rentals.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    private static final ZoneId PROJECT_ZONE = ZoneId.of("America/Sao_Paulo");

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final ItemUnitRepository itemUnitRepository;

    public DashboardService(RentalRepository rentalRepository, CustomerRepository customerRepository,
            ItemUnitRepository itemUnitRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.itemUnitRepository = itemUnitRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDTO getSummary() {
        LocalDate today = LocalDate.now(PROJECT_ZONE);
        Instant startToday = today.atStartOfDay(PROJECT_ZONE).toInstant();
        Instant endToday = today.plusDays(1).atStartOfDay(PROJECT_ZONE).toInstant();

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setAvailableGames(itemUnitRepository.countAvailableGames());
        summary.setActiveConsoles(itemUnitRepository.countActiveConsoles());
        summary.setActiveRentals(rentalRepository.countByStatus("RENTED"));
        summary.setReturnsToday(rentalRepository.countByStatusAndActualReturnDateBetween(
                "DELIVERED", startToday, endToday));
        summary.setActiveCustomers(customerRepository.countByActiveTrue());
        summary.setOverdueRentals(rentalRepository.countByStatusAndExpectedReturnDateBefore("RENTED", Instant.now()));
        summary.setDailyRentals(getDailyRentals(today));
        return summary;
    }

    private List<DashboardDailyRentalDTO> getDailyRentals(LocalDate today) {
        List<DashboardDailyRentalDTO> days = new ArrayList<>();
        LocalDate firstDay = today.minusDays(6);
        for (int index = 0; index < 7; index++) {
            LocalDate date = firstDay.plusDays(index);
            Instant start = date.atStartOfDay(PROJECT_ZONE).toInstant();
            Instant end = date.plusDays(1).atStartOfDay(PROJECT_ZONE).toInstant();
            long quantity = rentalRepository.countByRentalDateBetween(start, end);
            days.add(new DashboardDailyRentalDTO(date, dayLabel(date.getDayOfWeek()), quantity));
        }
        return days;
    }

    private String dayLabel(DayOfWeek day) {
        String[] labels = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};
        return labels[day.getValue() - 1];
    }
}
