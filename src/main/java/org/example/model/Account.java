package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType type;
    private UUID clientId;
    private boolean isActive;

    public enum AccountType{
        CURRENT,
        SAVINGS,
        CREDIT
    }

    public Account(UUID id, String accountNumber, BigDecimal balance, AccountType type, UUID clientId, boolean isActive){
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.type = type;
        this.clientId = clientId;
        this.isActive = isActive;
    }

    public Account(AccountType type, UUID clientId){
        this.id = UUID.randomUUID();
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = BigDecimal.ZERO;
        this.type = type;
        this.clientId = clientId;
        this.isActive = true;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
