package com.locadora_rdt_backend.modules.rentals.rental.dto;

import java.time.Instant;

public class RentalStatusHistoryDTO {
    private Long id;
    private String previousStatus;
    private String newStatus;
    private String reason;
    private Instant changedAt;
    private String changedBy;

    public Long getId() { return id; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public String getReason() { return reason; }
    public Instant getChangedAt() { return changedAt; }
    public String getChangedBy() { return changedBy; }
    public void setId(Long id) { this.id = id; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public void setReason(String reason) { this.reason = reason; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}
