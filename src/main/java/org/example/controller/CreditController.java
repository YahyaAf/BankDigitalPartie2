package org.example.controller;

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
                                 String startDateInput, String accountIdInput, String incomeProof) {

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

        LocalDate startDate = null;
        if (startDateInput != null && !startDateInput.isBlank()) {
            try {
                startDate = LocalDate.parse(startDateInput);
            } catch (Exception e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd");
                return false;
            }
        }

        return creditService.requestCredit(amount, interestRate, durationMonths, startDate, accountId, incomeProof);
    }
}
