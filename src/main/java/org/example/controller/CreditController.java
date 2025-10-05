package org.example.controller;

import org.example.model.Account;
import org.example.model.Credit;
import org.example.service.CreditService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreditController {
    private final CreditService creditService;

    public CreditController(CreditService creditService){
        this.creditService = creditService;
    }

    public boolean requestCredit(BigDecimal amount, double interestRate, int durationMonths,
                                 String accountIdInput, String incomeProof, String type) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be > 0");
            return false;
        }
        if (interestRate <= 0 || interestRate > 100) {
            System.out.println("Invalid interest rate.");
            return false;
        }
        if (durationMonths <= 0) {
            System.out.println("Duration must be > 0");
            return false;
        }
        if (accountIdInput == null || accountIdInput.isBlank()) {
            System.out.println("Account ID cannot be empty.");
            return false;
        }

        UUID accountId;
        try {
            accountId = UUID.fromString(accountIdInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Account ID format.");
            return false;
        }

        if (incomeProof == null || incomeProof.isBlank()) {
            System.out.println("Income proof required.");
            return false;
        }

        Credit.CreditType creditType;
        try {
            creditType = Credit.CreditType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid credit type. Use SIMPLE or COMPOSITE");
            return false;
        }

        return creditService.requestCredit(amount, interestRate, durationMonths,accountId, incomeProof, creditType);
    }

    public boolean validateCredit(String creditIdInput, String acceptedInput) {
        if (creditIdInput == null || creditIdInput.isBlank()) {
            System.out.println("Credit ID cannot be empty.");
            return false;
        }

        UUID creditId;
        try {
            creditId = UUID.fromString(creditIdInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Credit ID format.");
            return false;
        }

        if (acceptedInput == null || acceptedInput.isBlank()) {
            System.out.println("Accepted input cannot be empty (true/false).");
            return false;
        }

        boolean accepted;
        try {
            accepted = Boolean.parseBoolean(acceptedInput.toLowerCase());
        } catch (Exception e) {
            System.out.println("Accepted must be true or false.");
            return false;
        }

        return creditService.validateCredit(creditId, accepted);
    }

    public void showAllCredits(){
        creditService.showAllCredits();
    }
}
