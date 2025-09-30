package org.example.service;

import org.example.model.Account;
import org.example.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public boolean createAccount(Account.AccountType type, UUID clientId){
       List<Account> accounts = accountRepository.findAll();
       Boolean alreadyExsists = accounts.stream().anyMatch(account -> account.getClientId().equals(clientId) && account.getType().equals(type));
       if(alreadyExsists){
           System.out.println("Account of this type already exists");
           return false;
       }
       Account account = new Account(type, clientId);
       accountRepository.create(account);
       System.out.println("✅ Account created: " + account.getAccountNumber() + " for client " + clientId + " [" + type + "]");
       return true;
    }

    public Optional<Account> getAccountById(UUID id){
        return accountRepository.findById(id);
    }

    public Optional<Account> getAccountByNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public void updateBalance(UUID accountId, BigDecimal newBalance){
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(accountOptional.isPresent()){
            Account account = accountOptional.get();
            account.setBalance(newBalance);
            accountRepository.updateBalance(account);
            System.out.println("Balance updated for account " + account.getAccountNumber());
        }else{
            System.out.println("Account with id "+ accountId + " does not exist");
        }
    }

    public boolean deactivateAccount(UUID accountId){
        if(!accountRepository.findById(accountId).isPresent()){
            System.out.println("Account with id "+ accountId + " does not exist");
            return false;
        }
        accountRepository.deactivateAccount(accountId);
        System.out.println("Account deactivated: " + accountId);
        return true;
    }

    public void showAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            System.out.println("⚠️ No accounts found.");
        } else {
            System.out.println("=== Accounts List ===");
            for (Account acc : accounts) {
                System.out.println("ID: " + acc.getId());
                System.out.println("Account Number: " + acc.getAccountNumber());
                System.out.println("Balance: " + acc.getBalance());
                System.out.println("Type: " + acc.getType());
                System.out.println("Client ID: " + acc.getClientId());
                System.out.println("Active: " + acc.isActive());
                System.out.println("------------------------");
            }
        }
    }

}
