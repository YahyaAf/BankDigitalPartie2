package org.example.view;

import org.example.model.Account;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.UserRepository;
import org.example.repository.implementations.AccountRepositoryImpl;
import org.example.repository.implementations.ClientRepositoryImpl;
import org.example.repository.implementations.UserRepositoryImpl;
import org.example.service.AccountService;
import org.example.service.AuthService;
import org.example.service.TellerService;

import java.math.BigDecimal;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepositoryImpl();
        AuthService auth = new AuthService(userRepo);
        AccountRepository accountRepo = new AccountRepositoryImpl();
        AccountService accountService = new AccountService(accountRepo);

        // login as existing teller/admin
        boolean ok = auth.login("admin@bank.com", "admin123"); // ولا teller@bank.com
        if (!ok) return;

        ClientRepository clientRepo = new ClientRepositoryImpl();
        TellerService tellerService = new TellerService(clientRepo, auth);

        try {
            UUID clientId = UUID.fromString("fe8c4ea0-90e7-44e5-83f4-692516ac00dc");
            accountService.createAccount(Account.AccountType.CREDIT,clientId);
//            accountService.createAccount(Account.AccountType.CHECKING, clientId);
        } catch (RuntimeException e) {
            System.out.println("⚠️ Test Passed: " + e.getMessage());
        }
        UUID accountId = UUID.fromString("12bf40a2-2856-40d4-9ffb-37e00061f252");
        accountService.deactivateAccount(accountId);
        accountService.showAllAccounts();

//        tellerService.showAllClients();

        // logout
        auth.logout();
    }
}
