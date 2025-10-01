package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FeeRule {
    private Long id;
    private Transaction.TransactionType operationType;
    private FeeMode mode;
    private BigDecimal value;
    private String currency;
    private boolean isActive;
    private LocalDateTime createdAt;
    private UUID createdBy;

    public enum FeeMode {
        FIX,
        PERCENT
    }

    public FeeRule(Long id, Transaction.TransactionType operationType, FeeMode mode, BigDecimal value,
                   String currency, boolean isActive, LocalDateTime createdAt, UUID createdBy) {
        this.id = id;
        this.operationType = operationType;
        this.mode = mode;
        this.value = value;
        this.currency = currency;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public FeeRule(Transaction.TransactionType operationType, FeeMode mode, BigDecimal value,
                   String currency, UUID createdBy) {
        this.operationType = operationType;
        this.mode = mode;
        this.value = value;
        this.currency = currency;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction.TransactionType getOperationType() {
        return operationType;
    }

    public void setOperationType(Transaction.TransactionType operationType) {
        this.operationType = operationType;
    }

    public FeeMode getMode() {
        return mode;
    }

    public void setMode(FeeMode mode) {
        this.mode = mode;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}
