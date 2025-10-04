package org.example.repository;

import org.example.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void create(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    void updateBalance(Account account);
    void deactivateAccount(UUID id);
    Optional<Account> findByClientId(UUID clientId);
    List<Account> findAllByClientId(UUID clientId);
}
