package org.example.service;

import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.service.AccountService;
import org.example.service.CreditService;

import java.math.BigDecimal;
import java.util.concurrent.*;

public class TestScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final CreditService creditService;
    private final AccountService accountService;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    public TestScheduler(CreditService creditService, AccountService accountService, ClientRepository clientRepository,  AccountRepository accountRepository) {
        this.creditService = creditService;
        this.accountService = accountService;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    public void startSalaryJob() {
        scheduler.scheduleAtFixedRate(() -> {
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
    }

    public void startCreditDeductionJob() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("🏦 Running credit deduction job...");
                creditService.processMonthlyPayments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 4, TimeUnit.MINUTES);
    }

    public void start() {
        startSalaryJob();
        startCreditDeductionJob();
    }

    public void stop() {
        scheduler.shutdown();
    }
}
