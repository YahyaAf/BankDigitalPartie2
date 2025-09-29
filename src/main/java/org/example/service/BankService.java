package org.example.service;

import org.example.model.Bank;
import org.example.repository.BankRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class BankService {
    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository){
        this.bankRepository = bankRepository;
    }

    public void createBank(String name, BigDecimal initialBalance){
        Bank bank = new Bank(name,initialBalance);
        bankRepository.create(bank);
        System.out.println("Bank created: " + bank);
    }

    public Optional<Bank> getBankById(UUID id){
        return bankRepository.findById(id);
    }

    public Optional<Bank> getBank(){
        return bankRepository.findFirst();
    }

    public void addToBalance(UUID bankId, BigDecimal amount){
        Optional<Bank> bankOpt =  bankRepository.findById(bankId);
        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            BigDecimal newBalance = bank.getTotalBalance().add(amount);
            bank.setTotalBalance(newBalance);
            bankRepository.updateTotalBalance(bankId,newBalance);
            System.out.println("Bank balance increased by " + amount + ", new balance: " + newBalance);
        }else{
            System.out.println("Bank not found with ID: " + bankId);
        }
    }

    public void subtractFromBalance(UUID bankId, BigDecimal amount) {
        Optional<Bank> bankOpt = bankRepository.findById(bankId);
        if (bankOpt.isPresent()) {
            Bank bank = bankOpt.get();
            if (bank.getTotalBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient bank balance!");
                return;
            }
            BigDecimal newBalance = bank.getTotalBalance().subtract(amount);
            bank.setTotalBalance(newBalance);
            bankRepository.updateTotalBalance(bankId,newBalance);
            System.out.println("Bank balance decreased by " + amount + ", new balance: " + newBalance);
        } else {
            System.out.println("Bank not found with ID: " + bankId);
        }
    }
}
