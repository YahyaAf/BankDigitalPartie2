package org.example.service;

import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.service.AccountService;
import org.example.service.CreditService;

import java.math.BigDecimal;
import java.util.concurrent.*;

public class TestScheduler {

    private final ScheduledExecutorService salaryScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService creditScheduler; // <-- séparé
    private ScheduledFuture<?> creditDeductionFuture;

    private final CreditService creditService;
    private final AccountService accountService;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    public TestScheduler(CreditService creditService, AccountService accountService,
                         ClientRepository clientRepository, AccountRepository accountRepository) {
        this.creditService = creditService;
        this.accountService = accountService;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    public void startSalaryJob() {
        salaryScheduler.scheduleAtFixedRate(() -> {
            try {
                clientRepository.findAll().forEach(client -> {
                    accountRepository.findByClientId(client.getId()).ifPresent(account -> {
                        accountService.addSalaryToBalance(account.getId(), client.getSalary());
                        System.out.println("Salary " + client.getSalary() + " added to account " + account.getAccountNumber());
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.MINUTES);

        System.out.println("Salary job started (every 3 minutes)");
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
            }, 1, 4, TimeUnit.MINUTES);

            System.out.println("Credit deduction job started (every 4 minutes)");
        } else {
            System.out.println("Credit deduction job is already running!");
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
