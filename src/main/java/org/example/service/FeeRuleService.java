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

    public void deactivateFeeRule(Long id) {
        feeRuleRepository.updateStatus(id, false);
    }

    public void activateFeeRule(Long id) {
        feeRuleRepository.updateStatus(id, true);
    }

    public BigDecimal calculateFee(Transaction.TransactionType operationType, BigDecimal amount) {
        Optional<FeeRule> feeRuleOpt = feeRuleRepository.findActiveByOperationType(operationType);

        if (feeRuleOpt.isEmpty()) return BigDecimal.ZERO; 

        FeeRule rule = feeRuleOpt.get();
        switch (rule.getMode()) {
            case FIX:
                return rule.getValue();
            case PERCENT:
                return amount.multiply(rule.getValue().divide(BigDecimal.valueOf(100)));
            default:
                throw new IllegalArgumentException("Unsupported fee mode: " + rule.getMode());
        }
    }
}
