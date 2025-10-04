package org.example.repository;

import org.example.model.Bank;

import java.util.Optional;
import java.util.UUID;

public interface BankRepository {
    void create(Bank bank);
    Optional<Bank> findById(UUID id);
    Optional<Bank> findFirst();
    void updateTotalBalance(UUID id,java.math.BigDecimal newBalance);
}
