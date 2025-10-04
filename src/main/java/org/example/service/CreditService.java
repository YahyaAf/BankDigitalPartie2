package org.example.service;

import org.example.model.Account;
import org.example.model.Bank;
import org.example.model.Credit;
import org.example.model.CreditSchedule;
import org.example.repository.AccountRepository;
import org.example.repository.CreditRepository;
import org.example.repository.CreditScheduleRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;
    private final BankService bankService;

    public CreditService(CreditRepository creditRepository,
                         CreditScheduleRepository scheduleRepository,
                         AccountRepository accountRepository, BankService bankService) {
        this.creditRepository = creditRepository;
        this.scheduleRepository = scheduleRepository;
        this.accountRepository = accountRepository;
        this.bankService = bankService;
    }

    public boolean requestCredit(BigDecimal amount, double interestRate, int durationMonths,
                                LocalDate startDate, UUID accountId, String incomeProof) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero");
            return false;
        }
        if (durationMonths <= 0) {
            System.out.println("Duration months must be greater than zero");
            return false;
        }
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        LocalDate endDate = startDate.plusMonths(durationMonths);

        Credit credit = new Credit(amount, interestRate, startDate, endDate, durationMonths, accountId);

        credit.setIncomeProof(incomeProof);
        credit.setInterestAmount(BigDecimal.ZERO);
        credit.setStatus(Credit.CreditStatus.PENDING);
        credit.setValidationStatus(Credit.ValidationStatus.PENDING);

        creditRepository.save(credit);
        System.out.println("Credit saved for account successfully " + accountId);
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
            creditRepository.updateStatus(creditId, Credit.CreditStatus.REJECTED);
            return false;
        }

        credit.setStatus(Credit.CreditStatus.ACTIVE);

        BigDecimal interestAmount = credit.getAmount()
                .multiply(BigDecimal.valueOf(credit.getInterestRate() / 100));
        credit.setInterestAmount(interestAmount);

        credit.setEndDate(credit.getStartDate().plusMonths(credit.getDurationMonths()));

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
            bankService.subtractFromBalance(bank.getId(),credit.getAmount());
        }

        creditRepository.update(credit);

        scheduleRepository.generateSchedule(credit);
        return true;
    }

    public void processMonthlyPayments() {
        List<CreditSchedule> dueSchedules = scheduleRepository.findDueSchedules(LocalDate.now());

        for (CreditSchedule schedule : dueSchedules) {
            Credit credit = creditRepository.findById(schedule.getCreditId()).get();
            Account account = accountRepository.findById(credit.getAccountId()).get();
            Bank bank = bankService.getBank().get();

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

                System.out.println("Monthly payment of " + schedule.getAmountDue()
                        + " is late for " + account.getAccountNumber());
            }
        }
    }

}


