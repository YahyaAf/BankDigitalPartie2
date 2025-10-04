package org.example.service;

import org.example.model.Account;
import org.example.model.Bank;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.service.AccountService;
import org.example.service.CreditService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class TestScheduler {

    private final ScheduledExecutorService salaryScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService creditScheduler; // <-- séparé
    private ScheduledFuture<?> creditDeductionFuture;

    private final CreditService creditService;
    private final AccountService accountService;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final BankService bankService;

    public TestScheduler(CreditService creditService, AccountService accountService,
                         ClientRepository clientRepository, AccountRepository accountRepository,  BankService bankService) {
        this.creditService = creditService;
        this.accountService = accountService;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.bankService = bankService;
    }

    public void startSalaryJob() {
        salaryScheduler.scheduleAtFixedRate(() -> {
            try {
                Optional<Bank> bankOpt = bankService.getBank();

                if (bankOpt.isEmpty()) {
                    System.out.println("Bank not found! Cannot process salaries.");
                    return;
                }

                Bank bank = bankOpt.get();

                clientRepository.findAll().forEach(client -> {
                    List<Account> accounts = accountRepository.findAllByClientId(client.getId());

                    accounts.stream()
                            .filter(account -> account.getType() == Account.AccountType.CREDIT)
                            .filter(Account::isActive)
                            .forEach(account -> {
                                accountService.addSalaryToBalance(account.getId(), client.getSalary());
                                bankService.addToBalance(bank.getId(), client.getSalary());
                            });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.MINUTES);

        System.out.println("Salary job started (every 3 minutes - CREDIT accounts only)");
    }

    public void startCreditDeductionJob() {
        if (creditScheduler == null || creditScheduler.isShutdown()) {
            creditScheduler = Executors.newScheduledThreadPool(1);
        }

        if (creditDeductionFuture == null || creditDeductionFuture.isCancelled()) {
            creditDeductionFuture = creditScheduler.scheduleAtFixedRate(() -> {
                try {
                    System.out.println("🏦 Running credit deduction job...");
                    creditService.processMonthlyPayments();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1, 10, TimeUnit.SECONDS); // <-- 10 seconds for testing

            System.out.println("Credit deduction job started (every 10 seconds)");
        }
    }

    public void stopCreditDeductionJob() {
        if (creditDeductionFuture != null && !creditDeductionFuture.isCancelled()) {
            creditDeductionFuture.cancel(false);
            System.out.println("Credit deduction job stopped!");
        }

        if (creditScheduler != null && !creditScheduler.isShutdown()) {
            creditScheduler.shutdown();
        }
    }

    public boolean isCreditDeductionJobRunning() {
        return creditDeductionFuture != null && !creditDeductionFuture.isCancelled();
    }

    public void stopAll() {
        salaryScheduler.shutdown();
        stopCreditDeductionJob();
    }
}
