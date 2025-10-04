package org.example.repository;

import org.example.model.FeeRule;
import org.example.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface FeeRuleRepository {
    void create(FeeRule feeRule);
    Optional<FeeRule> findById(Long id);
    List<FeeRule> findAll();
    List<FeeRule> findActiveRules();
    Optional<FeeRule> findActiveByOperationType(Transaction.TransactionType operationType);
    void updateStatus(Long id, boolean isActive);
}
