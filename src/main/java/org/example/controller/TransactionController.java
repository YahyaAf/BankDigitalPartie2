package org.example.controller;

import org.example.service.TransactionService;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    public boolean deposit(String accountUUID, BigDecimal amount){

        if(accountUUID == null || accountUUID.isBlank()){
            System.out.println("Account ID is null or blank");
            return false;
        }

        if(amount == null){
            System.out.println("Amount cannot be null.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Deposit amount must be greater than 0.");
            return false;
        }

        UUID accountId;
        try{
            accountId = UUID.fromString(accountUUID);
        }catch(IllegalArgumentException e){
            System.out.println("Invalid Account ID format. Must be UUID.");
            return false;
        }

        return transactionService.deposit(accountId, amount);
    }

    public boolean withdraw(String accountUUID, BigDecimal amount){
        if(accountUUID == null || accountUUID.isBlank()){
            System.out.println("Account ID is null or blank");
            return false;
        }

        if(amount == null){
            System.out.println("Amount cannot be null.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Withdraw amount must be greater than 0.");
            return false;
        }

        UUID accountId;
        try{
            accountId = UUID.fromString(accountUUID);
        }catch(IllegalArgumentException e){
            System.out.println("Invalid Account ID format. Must be UUID.");
            return false;
        }

        return transactionService.withdraw(accountId, amount);
    }

    public boolean transferInternal(UUID senderId, UUID receiverId, BigDecimal amount) {
        if (senderId == null || receiverId == null) {
            System.out.println("Sender and Receiver IDs cannot be null.");
            return false;
        }

        if (senderId.equals(receiverId)) {
            System.out.println("Sender and Receiver cannot be the same account.");
            return false;
        }
        if (amount == null) {
            System.out.println("Amount cannot be null.");
            return false;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Transfer amount must be greater than 0.");
            return false;
        }
        return transactionService.transferInternal(senderId, receiverId, amount);
    }

    public boolean transferExternal(UUID senderId, String externalReceiverAccount, BigDecimal amount){
        if (senderId == null) {
            System.out.println("Sender ID cannot be null.");
            return false;
        }
        if (externalReceiverAccount == null || externalReceiverAccount.isBlank()) {
            System.out.println("External receiver account cannot be empty.");
            return false;
        }
        if (amount == null) {
            System.out.println("Amount cannot be null.");
            return false;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Transfer amount must be greater than 0.");
            return false;
        }

        return transactionService.transferExternal(senderId, externalReceiverAccount, amount);
    }

}
