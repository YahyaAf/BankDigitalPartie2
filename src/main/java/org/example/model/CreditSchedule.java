package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDate;

public class CreditSchedule {

    private UUID id;
    private UUID creditId;
    private UUID accountId;
    private LocalDate dueDate;
    private BigDecimal amountDue;
    private PaymentStatus status;
    private BigDecimal penalty;

    public enum PaymentStatus {
        UNPAID,
        PAID,
        LATE
    }

    public CreditSchedule(UUID creditId, LocalDate dueDate, BigDecimal amountDue, PaymentStatus status, BigDecimal penalty) {
        this.id = UUID.randomUUID();
        this.creditId = creditId;
        this.dueDate = dueDate;
        this.amountDue = amountDue;
        this.status = status;
        this.penalty = penalty;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCreditId() { return creditId; }
    public void setCreditId(UUID creditId) { this.creditId = creditId; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public BigDecimal getPenalty() { return penalty; }
    public void setPenalty(BigDecimal penalty) { this.penalty = penalty; }

    @Override
    public String toString() {
        return "CreditSchedule{" +
                "id=" + id +
                ", creditId=" + creditId +
                ", accountId=" + accountId +
                ", dueDate=" + dueDate +
                ", amountDue=" + amountDue +
                ", status=" + status +
                ", penalty=" + penalty +
                '}';
    }
}
