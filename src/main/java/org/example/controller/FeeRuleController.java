package org.example.controller;

import org.example.model.Account;
import org.example.model.FeeRule;
import org.example.model.Transaction;
import org.example.service.FeeRuleService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class FeeRuleController {
    private final FeeRuleService feeRuleService;

    public FeeRuleController(FeeRuleService feeRuleService) {
        this.feeRuleService = feeRuleService;
    }

    public boolean addFeeRule(String operationType,
                              String feeMode,
                              BigDecimal value, String currency,
                              UUID createdBy){
        if(operationType == null){
            System.out.println("operationType is null");
            return false;
        }

        Transaction.TransactionType transactionType;
        try {
            transactionType = Transaction.TransactionType.valueOf(operationType.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid transaction type. Allowed values: TRANSACTION_EXTERN,TRANSACTION_INTERN...");
            return false;
        }

        FeeRule.FeeMode feeModeType;
        try {
            feeModeType = FeeRule.FeeMode.valueOf(feeMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid feeMode type. Allowed values: FIXED, PERCENTAGE.");
            return false;
        }

        if(feeMode == null){
            System.out.println("feeMode is null");
            return false;
        }

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Value must be greater than 0.");
            return false;
        }

        if (feeModeType == FeeRule.FeeMode.PERCENT && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            System.out.println("Percentage value cannot exceed 100.");
            return false;
        }

        if (currency == null || currency.isBlank()) {
            System.out.println("Currency is required.");
            return false;
        }

        if (!currency.matches("^[A-Z]{3}$")) { // ex: MAD, USD, EUR
            System.out.println("Currency must be a valid 3-letter code (e.g., MAD, USD, EUR).");
            return false;
        }

        if (createdBy == null) {
            System.out.println("CreatedBy user ID is required.");
            return false;
        }

        FeeRule feeRule = new FeeRule(transactionType,feeModeType,value,currency,createdBy);
        return feeRuleService.addFeeRule(feeRule);
    }
}
