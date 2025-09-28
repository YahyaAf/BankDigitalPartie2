package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Bank {
    private UUID id;
    private String name;
    private BigDecimal totalBalance;

    public Bank(UUID id, String name, BigDecimal totalBalance){
        this.id = id;
        this.name = name;
        this.totalBalance = totalBalance == null ? BigDecimal.ZERO : totalBalance;
    }
    public Bank(String name,BigDecimal totalBalance){
        this.id = UUID.randomUUID();
        this.name = name;
        this.totalBalance = totalBalance == null ? BigDecimal.ZERO : totalBalance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
