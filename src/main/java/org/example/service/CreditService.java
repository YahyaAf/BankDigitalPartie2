package org.example.service;

import org.example.model.*;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.CreditRepository;
import org.example.repository.CreditScheduleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;
    private final BankService bankService;
    private final ClientRepository  clientRepository;

    public CreditService(CreditRepository creditRepository,
                         CreditScheduleRepository scheduleRepository,
                         AccountRepository accountRepository, BankService bankService, ClientRepository clientRepository) {
        this.creditRepository = creditRepository;
        this.scheduleRepository = scheduleRepository;
        this.accountRepository = accountRepository;
        this.bankService = bankService;
        this.clientRepository = clientRepository;
    }

    public boolean requestCredit(BigDecimal amount, double interestRate, int durationMonths,
                                 UUID accountId, String incomeProof, Credit.CreditType creditType) { // <-- Zid hna
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero");
            return false;
        }
        if (durationMonths <= 0) {
            System.out.println("Duration months must be greater than zero");
            return false;
        }

        if (creditType == null) {
            System.out.println("Credit type is required (SIMPLE or COMPOSITE)");
            return false;
        }

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            System.out.println("Account not found");
            return false;
        }

        Account account = accountOpt.get();

        if (account.getType() != Account.AccountType.CREDIT) {
            System.out.println("Credit request rejected: Only CREDIT accounts can request loans");
            System.out.println("Your account type is: " + account.getType());
            return false;
        }

        BigDecimal estimatedInterest;
        if (creditType == Credit.CreditType.SIMPLE) {
            estimatedInterest = amount.multiply(BigDecimal.valueOf(interestRate / 100));
        } else {
            estimatedInterest = amount.multiply(BigDecimal.valueOf(interestRate / 100));
        }

        BigDecimal totalAmount = amount.add(estimatedInterest);
        BigDecimal monthlyPayment = totalAmount.divide(BigDecimal.valueOf(durationMonths), 2, RoundingMode.HALF_UP);

        Optional<Client> clientOpt = clientRepository.findById(account.getClientId());
        if (clientOpt.isEmpty()) {
            System.out.println("Client not found");
            return false;
        }

        Client client = clientOpt.get();
        BigDecimal salary = client.getSalary();

        BigDecimal maxAllowed = salary.multiply(BigDecimal.valueOf(0.40));

        if (monthlyPayment.compareTo(maxAllowed) > 0) {
            System.out.println("Credit rejected: Monthly payment (" + monthlyPayment
                    + ") exceeds 40% of salary (" + maxAllowed + ")");
            System.out.println("   Your salary: " + salary);
            System.out.println("   Maximum allowed monthly payment: " + maxAllowed);
            return false;
        }

        System.out.println("Validation passed: Monthly payment " + monthlyPayment
                + " is within 40% of salary (" + maxAllowed + ")");

        LocalDate tempStartDate = LocalDate.now();
        LocalDate tempEndDate = tempStartDate.plusMonths(durationMonths);

        Credit credit = new Credit(amount, interestRate, tempStartDate, tempEndDate, durationMonths, accountId);

        credit.setIncomeProof(incomeProof);
        credit.setInterestAmount(BigDecimal.ZERO);
        credit.setStatus(Credit.CreditStatus.PENDING);
        credit.setValidationStatus(Credit.ValidationStatus.PENDING);
        credit.setType(creditType);

        creditRepository.save(credit);
        System.out.println("Credit request saved for account " + accountId + " (Type: " + creditType + ")");
        return true;
    }

    public boolean validateCredit(UUID creditId, boolean accepted) {
        Optional<Credit> optCredit = creditRepository.findById(creditId);

        if (optCredit.isEmpty()) {
            throw new RuntimeException("Credit not found");
        }

        Credit credit = optCredit.get();

        if (!accepted) {
            credit.setStatus(Credit.CreditStatus.REJECTED);
            credit.setValidationStatus(Credit.ValidationStatus.REJECTED);
            creditRepository.updateStatus(creditId, Credit.CreditStatus.REJECTED);
            creditRepository.updateValidationStatus(creditId, Credit.ValidationStatus.REJECTED);
            return false;
        }

        credit.setStatus(Credit.CreditStatus.ACTIVE);
        credit.setValidationStatus(Credit.ValidationStatus.ACCEPTED);

        credit.setStartDate(LocalDate.now());
        credit.setEndDate(credit.getStartDate().plusMonths(credit.getDurationMonths()));

        // Basic interest calculation (will be recalculated in generateSchedule)
        BigDecimal interestAmount = credit.getAmount()
                .multiply(BigDecimal.valueOf(credit.getInterestRate() / 100));
        credit.setInterestAmount(interestAmount);

        Optional<Account> accountOptional = accountRepository.findById(credit.getAccountId());
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            BigDecimal newBalance = account.getBalance().add(credit.getAmount());
            account.setBalance(newBalance);
            accountRepository.updateBalance(account);
            System.out.println("Balance updated for account " + account.getAccountNumber());
        } else {
            System.out.println("Account with id " + credit.getAccountId() + " does not exist");
        }

        Optional<Bank> bankOpt = bankService.getBank();
        if (bankOpt.isPresent()) {
            Bank bank = bankOpt.get();
            bankService.subtractFromBalance(bank.getId(), credit.getAmount());
        }

        creditRepository.update(credit);

        scheduleRepository.generateSchedule(credit); // Interest recalculated here
        return true;
    }

    public void processMonthlyPayments() {
        LocalDate today = LocalDate.now();
        List<CreditSchedule> dueSchedules = scheduleRepository.findDueSchedules(today);

        if (dueSchedules.isEmpty()) {
            System.out.println("ℹNo payments due today");
            return;
        }

        for (CreditSchedule schedule : dueSchedules) {
            Credit credit = creditRepository.findById(schedule.getCreditId()).get();
            Account account = accountRepository.findById(credit.getAccountId()).get();
            Bank bank = bankService.getBank().get();

            System.out.println("   Account balance: " + account.getBalance());

            if (account.getBalance().compareTo(schedule.getAmountDue()) >= 0) {
                account.setBalance(account.getBalance().subtract(schedule.getAmountDue()));
                accountRepository.updateBalance(account);

                bankService.addToBalance(bank.getId(), schedule.getAmountDue());

                schedule.setStatus(CreditSchedule.PaymentStatus.PAID);
                scheduleRepository.update(schedule);

                System.out.println("Monthly payment of " + schedule.getAmountDue()
                        + " collected from " + account.getAccountNumber());
            } else {
                schedule.setStatus(CreditSchedule.PaymentStatus.LATE);
                schedule.setPenalty(BigDecimal.valueOf(50));
                scheduleRepository.update(schedule);

                System.out.println("Insufficient balance! Payment marked LATE");
            }
        }
    }

}


