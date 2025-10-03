package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Credit {
    private UUID id;
    private BigDecimal amount;
    private double interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationMonths;
    private CreditStatus status;
    private CreditType type;
    private UUID accountId;
    private String incomeProof;
    private BigDecimal interestAmount;
    private ValidationStatus validationStatus;

    public enum CreditStatus {
        PENDING,
        ACTIVE,
        LATE,
        CLOSED,
        REJECTED
    }

    public enum CreditType {
        SIMPLE,
        COMPOSITE
    }

    public enum ValidationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public Credit(UUID id, BigDecimal amount, double interestRate,
                  LocalDate startDate, LocalDate endDate, int durationMonths,
                  CreditStatus status, CreditType type, UUID accountId,
                  String incomeProof, BigDecimal interestAmount,
                  ValidationStatus validationStatus) {
        this.id = id;
        this.amount = amount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationMonths = durationMonths;
        this.status = status;
        this.type = type;
        this.accountId = accountId;
        this.incomeProof = incomeProof;
        this.interestAmount = interestAmount;
        this.validationStatus = validationStatus;
    }

    public Credit(BigDecimal amount, double interestRate,
                  LocalDate startDate, LocalDate endDate, int durationMonths,
                  UUID accountId) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationMonths = durationMonths;
        this.status = CreditStatus.PENDING;
        this.type = CreditType.SIMPLE;
        this.accountId = accountId;
        this.incomeProof = null;
        this.interestAmount = BigDecimal.ZERO;
        this.validationStatus = ValidationStatus.PENDING;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public CreditStatus getStatus() { return status; }
    public void setStatus(CreditStatus status) { this.status = status; }

    public CreditType getType() { return type; }
    public void setType(CreditType type) { this.type = type; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public String getIncomeProof() { return incomeProof; }
    public void setIncomeProof(String incomeProof) { this.incomeProof = incomeProof; }

    public BigDecimal getInterestAmount() { return interestAmount; }
    public void setInterestAmount(BigDecimal interestAmount) { this.interestAmount = interestAmount; }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }
    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", amount=" + amount +
                ", interestRate=" + interestRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationMonths=" + durationMonths +
                ", status=" + status +
                ", type=" + type +
                ", accountId=" + accountId +
                ", incomeProof='" + incomeProof + '\'' +
                ", interestAmount=" + interestAmount +
                '}';
    }
}
