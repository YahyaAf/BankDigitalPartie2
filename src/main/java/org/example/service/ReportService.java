package org.example.service;

import org.example.model.*;
import org.example.repository.*;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final AccountRepository accountRepository;
    private final CreditRepository creditRepository;
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final BankService bankService;

    public ReportService(AccountRepository accountRepo, CreditRepository creditRepo,
                         TransactionRepository transactionRepo, ClientRepository clientRepo, BankService bankService) {
        this.accountRepository = accountRepo;
        this.creditRepository = creditRepo;
        this.transactionRepository = transactionRepo;
        this.clientRepository = clientRepo;
        this.bankService = bankService;
    }

    public void showTotalBankBalance() {
        Optional<Bank> bankOpt = bankService.getBank();
        Bank bank = bankOpt.get();
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     SOLDE TOTAL DE LA BANQUE         ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Solde total: " + bank.getTotalBalance() + " DH");
        System.out.println("═════════════════════════════════════════\n");
    }

    public void showCreditRevenue() {
        List<Credit> credits = creditRepository.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int activeCredits = 0;

        for (Credit credit : credits) {
            if (credit.getInterestAmount() != null) {
                totalRevenue = totalRevenue.add(credit.getInterestAmount());
            }
            if (credit.getStatus() == Credit.CreditStatus.ACTIVE) {
                activeCredits++;
            }
        }

        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    REVENUS DES CRÉDITS               ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Nombre total de crédits: " + credits.size());
        System.out.println("Crédits actifs: " + activeCredits);
        System.out.println("Revenus totaux générés: " + totalRevenue + " DH");
        System.out.println("═════════════════════════════════════════\n");
    }

    public void showTopClients(int limit) {
        List<Transaction> transactions = transactionRepository.findAll();
        Map<UUID, Integer> clientTransactionCount = new HashMap<>();

        for (Transaction transaction : transactions) {
            UUID senderAccountId = transaction.getSenderAccountId();
            UUID receiverAccountId = transaction.getReceiverAccountId();

            if (senderAccountId != null) {
                Optional<Account> senderAccount = accountRepository.findById(senderAccountId);
                if (senderAccount.isPresent()) {
                    UUID clientId = senderAccount.get().getClientId();
                    clientTransactionCount.put(clientId,
                            clientTransactionCount.getOrDefault(clientId, 0) + 1);
                }
            }

            if (receiverAccountId != null) {
                Optional<Account> receiverAccount = accountRepository.findById(receiverAccountId);
                if (receiverAccount.isPresent()) {
                    UUID clientId = receiverAccount.get().getClientId();
                    clientTransactionCount.put(clientId,
                            clientTransactionCount.getOrDefault(clientId, 0) + 1);
                }
            }
        }

        List<Map.Entry<UUID, Integer>> sortedClients = clientTransactionCount.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          TOP " + limit + " CLIENTS PAR VOLUME D'OPÉRATIONS          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : sortedClients) {
            Optional<Client> clientOpt = clientRepository.findById(entry.getKey());
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                System.out.println("\n Rang #" + rank);
                System.out.println("   Nom: " + client.getFirstName() + " " + client.getLastName());
                System.out.println("   CIN: " + client.getCin());
                System.out.println("   Email: " + client.getEmail());
                System.out.println("   Nombre d'opérations: " + entry.getValue());
                System.out.println("   ─────────────────────────────────────");
                rank++;
            }
        }
        System.out.println("\n═══════════════════════════════════════════════════════════\n");
    }

    public void exportReportToTXT(String filename) {
        String fullPath = "bank_reports/" + filename;
        try (FileWriter writer = new FileWriter(fullPath)) {
            writer.append("════════════════════════════════════════════════\n");
            writer.append("          RAPPORT BANCAIRE COMPLET\n");
            writer.append("════════════════════════════════════════════════\n\n");

            Optional<Bank> bankOpt = bankService.getBank();
            Bank bank = bankOpt.get();

            writer.append("SOLDE TOTAL DE LA BANQUE\n");
            writer.append("Solde total: " + bank.getTotalBalance() + " DH\n\n");

            List<Credit> credits = creditRepository.findAll();
            BigDecimal totalRevenue = credits.stream()
                    .map(Credit::getInterestAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            writer.append("REVENUS DES CRÉDITS\n");
            writer.append("Nombre total de crédits: " + credits.size() + "\n");
            writer.append("Crédits actifs: " + credits.stream()
                    .filter(c -> c.getStatus() == Credit.CreditStatus.ACTIVE).count() + "\n");
            writer.append("Revenus générés: " + totalRevenue + " DH\n\n");

            writer.append("TOP 10 CLIENTS PAR VOLUME D'OPÉRATIONS\n");
            writer.append("────────────────────────────────────────────────\n");

            List<Transaction> transactions = transactionRepository.findAll();
            Map<UUID, Integer> clientTransactionCount = new HashMap<>();

            for (Transaction transaction : transactions) {
                UUID senderAccountId = transaction.getSenderAccountId();
                if (senderAccountId != null) {
                    Optional<Account> account = accountRepository.findById(senderAccountId);
                    if (account.isPresent()) {
                        UUID clientId = account.get().getClientId();
                        clientTransactionCount.put(clientId,
                                clientTransactionCount.getOrDefault(clientId, 0) + 1);
                    }
                }
            }

            List<Map.Entry<UUID, Integer>> sortedClients = clientTransactionCount.entrySet()
                    .stream()
                    .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            int rank = 1;
            for (Map.Entry<UUID, Integer> entry : sortedClients) {
                Optional<Client> clientOpt = clientRepository.findById(entry.getKey());
                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();
                    writer.append("\n   #" + rank + " - " + client.getFirstName() + " " + client.getLastName() + "\n");
                    writer.append("CIN: " + client.getCin() + "\n");
                    writer.append("Email: " + client.getEmail() + "\n");
                    writer.append("Opérations: " + entry.getValue() + "\n");
                    rank++;
                }
            }

            writer.append("\n════════════════════════════════════════════════\n");
            writer.append("           FIN DU RAPPORT\n");
            writer.append("════════════════════════════════════════════════\n");

            System.out.println("Rapport exporté vers: " + fullPath);

        } catch (IOException e) {
            System.err.println("Erreur lors de l'export TXT: " + e.getMessage());
        }
    }


}