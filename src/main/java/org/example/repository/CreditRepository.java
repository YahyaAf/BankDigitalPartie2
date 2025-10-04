package org.example.repository;

import org.example.model.Credit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository {
    void save(Credit credit);
    Optional<Credit> findById(UUID id);
    List<Credit> findAll();
    List<Credit> findByAccountId(UUID accountId);
    void updateStatus(UUID id, Credit.CreditStatus status);
    void updateValidationStatus(UUID id, Credit.ValidationStatus validationStatus);
    void update(Credit credit);
}
