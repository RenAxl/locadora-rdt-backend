package com.locadora_rdt_backend.modules.dashboard.service;

import com.locadora_rdt_backend.modules.dashboard.constants.DashboardConstants;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

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
        LocalDate today = LocalDate.now(DashboardConstants.PROJECT_ZONE);
        Instant startToday = today.atStartOfDay(DashboardConstants.PROJECT_ZONE).toInstant();
        Instant endToday = today.plusDays(DashboardConstants.NEXT_DAY_OFFSET)
                .atStartOfDay(DashboardConstants.PROJECT_ZONE)
                .toInstant();

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setAvailableGames(itemUnitRepository.countAvailableGames());
        summary.setActiveConsoles(itemUnitRepository.countActiveConsoles());
        summary.setActiveRentals(rentalRepository.countByStatus(DashboardConstants.RENTED_STATUS));
        summary.setReturnsToday(rentalRepository.countByStatusAndActualReturnDateBetween(
                DashboardConstants.DELIVERED_STATUS, startToday, endToday));
        summary.setActiveCustomers(customerRepository.countByActiveTrue());
        summary.setOverdueRentals(rentalRepository.countByStatusAndExpectedReturnDateBefore(
                DashboardConstants.RENTED_STATUS,
                Instant.now()
        ));
        summary.setDailyRentals(getDailyRentals(today));
        return summary;
    }

    private List<DashboardDailyRentalDTO> getDailyRentals(LocalDate today) {
        List<DashboardDailyRentalDTO> days = new ArrayList<>();
        LocalDate firstDay = today.minusDays(DashboardConstants.DAYS_BEFORE_TODAY);
        for (int index = 0; index < DashboardConstants.DAYS_IN_WEEK; index++) {
            LocalDate date = firstDay.plusDays(index);
            Instant start = date.atStartOfDay(DashboardConstants.PROJECT_ZONE).toInstant();
            Instant end = date.plusDays(DashboardConstants.NEXT_DAY_OFFSET)
                    .atStartOfDay(DashboardConstants.PROJECT_ZONE)
                    .toInstant();
            long quantity = rentalRepository.countByRentalDateBetween(start, end);
            days.add(new DashboardDailyRentalDTO(date, dayLabel(date.getDayOfWeek()), quantity));
        }
        return days;
    }

    private String dayLabel(DayOfWeek day) {
        int labelIndex = day.getValue() - DashboardConstants.DAY_OF_WEEK_INDEX_OFFSET;
        return DashboardConstants.WEEK_DAY_LABELS.get(labelIndex);
    }
}
