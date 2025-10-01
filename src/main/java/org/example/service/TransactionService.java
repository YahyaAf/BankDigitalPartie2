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

    public boolean deposit(UUID accountId, BigDecimal amount){
        Optional<Account> optAccount = accountRepository.findById(accountId);
        if (!optAccount.isPresent()) {
            System.out.println("Deposit failed: Account not found with id " + accountId);
            return false;
        }
        Account account = optAccount.get();

        if(!account.isActive()){
            System.out.println("Account not active is closed");
            return false;
        }

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
        return true;
    }

    public boolean withdraw(UUID accountId, BigDecimal amount){
        Optional<Account> optAccount = accountRepository.findById(accountId);
        if (!optAccount.isPresent()) {
            System.out.println("Withdraw failed: Account not found with id " + accountId);
            return false;
        }
        Account account = optAccount.get();

        if(!account.isActive()){
            System.out.println("Account not active is closed");
            return false;
        }

        if (account.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient balance!");
            return false;
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
        return true;
    }

    public boolean transferInternal(UUID senderId, UUID receiverId, BigDecimal amount){
        Optional<Account> optSender = accountRepository.findById(senderId);
        if (!optSender.isPresent()) {
            System.out.println("Transfer failed: Account of sender not found with id " + senderId);
            return false;
        }
        Account sender = optSender.get();

        Optional<Account> optReceiver = accountRepository.findById(receiverId);
        if (!optReceiver.isPresent()) {
            System.out.println("Transfer failed: Account of receiver not found with id " + receiverId);
            return false;
        }
        Account receiver = optReceiver.get();

        if(!sender.isActive()){
            System.out.println("Account not active is closed");
            return false;
        }

        if(!receiver.isActive()){
            System.out.println("Account not active is closed");
            return false;
        }

        if(sender.getBalance().compareTo(amount)<0){
            System.out.println("Insufficient balance!!!");
            return false;
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
        return true;
    }

    public boolean transferExternal(UUID senderId, String externalReceiverAccount, BigDecimal amount){
        Optional<Account> optSender = accountRepository.findById(senderId);
        if (!optSender.isPresent()) {
            System.out.println("Transfer failed: Account of sender not found with id " + senderId);
            return false;
        }
        Account sender = optSender.get();
        BigDecimal fee = feeRuleService.calculateFee(Transaction.TransactionType.TRANSFER_EXTERNAL,amount);
        BigDecimal totalDebit = amount.add(fee);

        if(!sender.isActive()){
            System.out.println("Account not active is closed");
            return false;
        }

        if(sender.getBalance().compareTo(totalDebit)<0){
            System.out.println("Insufficient balance!");
            return false;
        }

        sender.setBalance(sender.getBalance().subtract(totalDebit));
        accountRepository.updateBalance(sender);

        Optional<Bank> bankOpt = bankService.getBank();
        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            bankService.subtractFromBalance(bank.getId(),fee);
        }

        Transaction transaction = new Transaction(Transaction.TransactionType.TRANSFER_EXTERNAL,amount,senderId,externalReceiverAccount);
        transaction.setStatus(Transaction.TransactionStatus.SETTLED);
        transactionRepository.create(transaction);
        System.out.println("External transfer of " + totalDebit + " from " + sender.getAccountNumber() +
                " to external account " + externalReceiverAccount + " is SETTLED");
        return true;
    }


}
