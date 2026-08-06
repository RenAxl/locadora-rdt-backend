package com.locadora_rdt_backend.modules.dashboard.dto;

import java.util.List;

public class DashboardSummaryDTO {
    private Long availableGames;
    private Long activeConsoles;
    private Long activeRentals;
    private Long returnsToday;
    private Long activeCustomers;
    private Long overdueRentals;
    private List<DashboardDailyRentalDTO> dailyRentals;

    public Long getAvailableGames() { return availableGames; }
    public Long getActiveConsoles() { return activeConsoles; }
    public Long getActiveRentals() { return activeRentals; }
    public Long getReturnsToday() { return returnsToday; }
    public Long getActiveCustomers() { return activeCustomers; }
    public Long getOverdueRentals() { return overdueRentals; }
    public List<DashboardDailyRentalDTO> getDailyRentals() { return dailyRentals; }
    public void setAvailableGames(Long availableGames) { this.availableGames = availableGames; }
    public void setActiveConsoles(Long activeConsoles) { this.activeConsoles = activeConsoles; }
    public void setActiveRentals(Long activeRentals) { this.activeRentals = activeRentals; }
    public void setReturnsToday(Long returnsToday) { this.returnsToday = returnsToday; }
    public void setActiveCustomers(Long activeCustomers) { this.activeCustomers = activeCustomers; }
    public void setOverdueRentals(Long overdueRentals) { this.overdueRentals = overdueRentals; }
    public void setDailyRentals(List<DashboardDailyRentalDTO> dailyRentals) { this.dailyRentals = dailyRentals; }
}
