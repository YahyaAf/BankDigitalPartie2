package org.example.service;

import org.example.service.CreditService;

import java.time.*;
import java.util.concurrent.*;

public class CreditScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final CreditService creditService;

    public CreditScheduler(CreditService creditService) {
        this.creditService = creditService;
    }

    public void startDailyJob() {
        long initialDelay = computeInitialDelayToMidnight();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDate today = LocalDate.now();
                if (today.getDayOfMonth() == 1) {
                    System.out.println("Running monthly credit deduction job on " + today);
                    creditService.processMonthlyPayments();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    private long computeInitialDelayToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, nextMidnight).toMillis();
    }

    public void stop() {
        scheduler.shutdown();
    }
}
