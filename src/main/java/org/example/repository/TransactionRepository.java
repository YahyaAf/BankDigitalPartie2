package org.example.repository;

import org.example.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    void create(Transaction transaction);
    Optional <Transaction> findById(UUID id);
    List<Transaction> findAll();
    void updateStatus(UUID id, Transaction.TransactionStatus status);
}
