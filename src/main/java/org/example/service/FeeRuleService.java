package org.example.service;

import org.example.model.FeeRule;
import org.example.model.Transaction;
import org.example.repository.FeeRuleRepository;
import org.example.repository.implementations.FeeRuleRepositoryImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class FeeRuleService {

    private final FeeRuleRepositoryImpl feeRuleRepository;

    public FeeRuleService(FeeRuleRepository feeRuleRepository) {
        this.feeRuleRepository = new FeeRuleRepositoryImpl();
    }

    public boolean addFeeRule(FeeRule feeRule) {
        Optional<FeeRule> existingActiveRule = feeRuleRepository.findActiveByOperationType(feeRule.getOperationType());
        if (existingActiveRule.isPresent() && feeRule.isActive()) {
            System.out.println("Error: An active fee rule already exists for operation type "
                    + feeRule.getOperationType());
            return false;
        }

        feeRuleRepository.create(feeRule);
        System.out.println("Fee Rule added successfully.");
        return true;
    }

    public Optional<FeeRule> getFeeRuleById(Long id) {
        return feeRuleRepository.findById(id);
    }

    public List<FeeRule> getAllFeeRules() {
        return feeRuleRepository.findAll();
    }

    public List<FeeRule> getActiveFeeRules() {
        return feeRuleRepository.findActiveRules();
    }

    public Optional<FeeRule> getActiveRuleForOperation(Transaction.TransactionType operationType) {
        return feeRuleRepository.findActiveByOperationType(operationType);
    }

    public boolean deactivateFeeRule(Long id) {
        Optional<FeeRule> feeRuleOpt  = feeRuleRepository.findById(id);
        if (feeRuleOpt.isEmpty()) {
            System.out.println("FeeRule not found with id: " + id);
            return false;
        }

        FeeRule feeRule = feeRuleOpt.get();
        if (!feeRule.isActive()) {
            System.out.println("FeeRule is already inactive.");
            return false;
        }

        feeRuleRepository.updateStatus(id, false);
        System.out.println("FeeRule deactivated successfully.");
        return true;
    }

    public boolean activateFeeRule(Long id) {
        Optional<FeeRule> feeRuleOpt = feeRuleRepository.findById(id);

        if (feeRuleOpt.isEmpty()) {
            System.out.println("FeeRule not found with id: " + id);
            return false;
        }

        FeeRule feeRule = feeRuleOpt.get();

        if (feeRule.isActive()) {
            System.out.println("FeeRule is already active.");
            return false;
        }

        Optional<FeeRule> activeRuleOpt =
                feeRuleRepository.findActiveByOperationType(feeRule.getOperationType());

        if (activeRuleOpt.isPresent()) {
            System.out.println("Cannot activate. Another active FeeRule exists for operation type: "
                    + feeRule.getOperationType());
            return false;
        }
        feeRuleRepository.updateStatus(id, true);
        System.out.println("FeeRule activated successfully.");
        return true;
    }

    public BigDecimal calculateFee(Transaction.TransactionType operationType, BigDecimal amount) {
        Optional<FeeRule> feeRuleOpt = feeRuleRepository.findActiveByOperationType(operationType);

        if (feeRuleOpt.isEmpty()) return BigDecimal.ZERO; 

        FeeRule rule = feeRuleOpt.get();
        if (!rule.isActive()) {
            return BigDecimal.ZERO;
        }

        switch (rule.getMode()) {
            case FIX:
                return rule.getValue();
            case PERCENT:
                return amount.multiply(rule.getValue().divide(BigDecimal.valueOf(100)));
            default:
                throw new IllegalArgumentException("Unsupported fee mode: " + rule.getMode());
        }
    }

    public void showAllFeeRules() {
        List<FeeRule> feeRules = feeRuleRepository.findAll();

        if (feeRules.isEmpty()) {
            System.out.println("No fee rules found.");
        } else {
            System.out.println("====== List of Fee Rules ======");
            for (FeeRule rule : feeRules) {
                System.out.println("ID: " + rule.getId());
                System.out.println("Operation Type: " + rule.getOperationType());
                System.out.println("Mode: " + rule.getMode());
                System.out.println("Value: " + rule.getValue());
                System.out.println("Currency: " + rule.getCurrency());
                System.out.println("Active: " + rule.isActive());
                System.out.println("Created At: " + rule.getCreatedAt());
                System.out.println("Created By: " + rule.getCreatedBy());
                System.out.println("------------------------");
            }
        }
    }


}
