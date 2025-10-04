package org.example.controller;

import org.example.model.Account;
import org.example.service.AccountService;

import java.util.UUID;

public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    public boolean createAccount(String type, String clientId){
        if(type == null || type.isBlank()){
            System.out.println("Type of account is null or blank");
            return false;
        }

        Account.AccountType accountType;
        try {
            accountType = Account.AccountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid account type. Allowed values: CREDIT, SAVINGS, etc.");
            return false;
        }

        if(clientId == null || clientId.isBlank()){
            System.out.println("Client ID is null or blank");
            return false;
        }

        UUID clientUUID;
        try{
            clientUUID = UUID.fromString(clientId);
        }catch(IllegalArgumentException e){
            System.out.println("Invalid Client ID format. Must be UUID.");
            return false;
        }

        return accountService.createAccount(accountType, clientUUID);

    }

    public boolean deactivateAccount(String accountId){
        if(accountId == null || accountId.isBlank()){
            System.out.println("Client ID is null or blank");
            return false;
        }
        UUID accUUID;
        try{
            accUUID = UUID.fromString(accountId);
        }catch(IllegalArgumentException e){
            System.out.println("Invalid Account ID format. Must be UUID.");
            return false;
        }
        return accountService.deactivateAccount(accUUID);
    }

    public void showAllAccounts(){
        accountService.showAllAccounts();
    }

}
