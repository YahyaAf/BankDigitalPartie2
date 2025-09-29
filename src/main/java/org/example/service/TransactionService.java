package org.example.service;

import org.example.model.Account;
import org.example.model.Bank;
import org.example.model.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BankService bankService;
    private final FeeRuleService feeRuleService;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, BankService bankService, FeeRuleService feeRuleService){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.bankService = bankService;
        this.feeRuleService = feeRuleService;
    }

    public void deposit(UUID accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.updateBalance(account);

        Optional<Bank> bankOpt = bankService.getBank();
        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            bankService.addToBalance(bank.getId(),amount);
        }

        Transaction transaction = new Transaction(
                Transaction.TransactionType.DEPOSIT,
                amount,
                accountId,
                accountId
        );
        transaction.setStatus(Transaction.TransactionStatus.SETTLED);
        transactionRepository.create(transaction);
        System.out.println("Deposit of " + amount + " completed for account " + account.getAccountNumber());
    }

    public void withdraw(UUID accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient balance!");
            return;
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.updateBalance(account);

        Optional<Bank> bankOpt = bankService.getBank();
        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            bankService.subtractFromBalance(bank.getId(),amount);
        }

        Transaction transaction = new Transaction(
                Transaction.TransactionType.WITHDRAW,
                amount,
                accountId,
                accountId
        );
        transaction.setStatus(Transaction.TransactionStatus.SETTLED);
        transactionRepository.create(transaction);
        System.out.println("Withdraw of " + amount + " completed for account " + account.getAccountNumber());
    }

    public void transferInternal(UUID senderId, UUID receiverId, BigDecimal amount){
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(()->new RuntimeException("Account sender not found"));
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(()->new RuntimeException("Account receiver not found"));

        if(sender.getBalance().compareTo(amount)<0){
            System.out.println("Insufficient balance!");
            return;
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.updateBalance(receiver);
        accountRepository.updateBalance(sender);

        Transaction transactionOut = new Transaction(Transaction.TransactionType.TRANSFER_OUT,amount,senderId,receiverId);
        transactionOut.setStatus(Transaction.TransactionStatus.SETTLED);
        transactionRepository.create(transactionOut);

        Transaction transactionIn = new Transaction(Transaction.TransactionType.TRANSFER_IN,amount,senderId,receiverId);
        transactionIn.setStatus(Transaction.TransactionStatus.SETTLED);
        transactionRepository.create(transactionIn);
        System.out.println("Internal transfer of " + amount + " from " + sender.getAccountNumber() + " to " + receiver.getAccountNumber());
    }

    public void transferExternal(UUID senderId, String externalReceiverAccount, BigDecimal amount){
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(()->new RuntimeException("Account sender not found"));
        if(sender.getBalance().compareTo(amount)<0){
            System.out.println("Insufficient balance!");
            return;
        }
        BigDecimal fee = feeRuleService.calculateFee(Transaction.TransactionType.TRANSFER_EXTERNAL,amount);
        BigDecimal totalDebit = amount.add(fee);

        if(sender.getBalance().compareTo(totalDebit)<0){
            System.out.println("Insufficient balance!");
            return;
        }

        sender.setBalance(sender.getBalance().subtract(totalDebit));
        accountRepository.updateBalance(sender);

        Optional<Bank> bankOpt = bankService.getBank();
        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            bankService.subtractFromBalance(bank.getId(),fee);
        }

        Transaction transaction = new Transaction(Transaction.TransactionType.TRANSFER_EXTERNAL,amount,senderId,externalReceiverAccount);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transactionRepository.create(transaction);
        System.out.println("External transfer of " + totalDebit + " from " + sender.getAccountNumber() +
                " to external account " + externalReceiverAccount + " is PENDING");
    }

    public void validationExternalTransfer(UUID transactionId, boolean approve){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found"));
        if(transaction.getStatus() != Transaction.TransactionStatus.PENDING){
            throw new RuntimeException("only PENDING transactions are supported");
        }
        if(approve){
            Account sender = accountRepository.findById(transaction.getSenderAccountId())
                    .orElseThrow(()->new RuntimeException("Account sender not found"));
            if(sender.getBalance().compareTo(transaction.getAmount())>0){
                System.out.println("Insufficient balance!");
                return;
            }
            sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
            accountRepository.updateBalance(sender);

            Optional<Bank> bankOpt = bankService.getBank();
            if(bankOpt.isPresent()){
                Bank bank = bankOpt.get();
                bankService.subtractFromBalance(bank.getId(),transaction.getAmount());
            }

            transactionRepository.updateStatus(transactionId, Transaction.TransactionStatus.SETTLED);
            System.out.println("External transfer APPROVED and settled for transaction " + transactionId);
        }else{
            transactionRepository.updateStatus(transactionId, Transaction.TransactionStatus.FAILED);
            System.out.println("External transfer REJECTED for transaction " + transactionId);
        }
    }


}
