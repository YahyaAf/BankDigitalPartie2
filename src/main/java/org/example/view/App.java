package org.example.view;

import org.example.repository.ClientRepository;
import org.example.repository.UserRepository;
import org.example.repository.implementations.ClientRepositoryImpl;
import org.example.repository.implementations.UserRepositoryImpl;
import org.example.service.AuthService;
import org.example.service.TellerService;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepositoryImpl();
        AuthService auth = new AuthService(userRepo);

        // login as existing teller/admin
        boolean ok = auth.login("admin@bank.com", "admin123"); // ولا teller@bank.com
        if (!ok) return;

        ClientRepository clientRepo = new ClientRepositoryImpl();
        TellerService tellerService = new TellerService(clientRepo, auth);

        tellerService.createClient(
                "Yahya",
                "Yami",
                "CIN1234586",
                "+212600000000",
                "Rue X, Casablanca",
                "yahya.ali@example.com",
                new BigDecimal("3500.50")
        );
        tellerService.createClient(
                "ahmed",
                "moha",
                "CIN123452",
                "+212600000000",
                "Rue X, Casablanca",
                "moha.ali@example.com",
                new BigDecimal("3500.50")
        );

        tellerService.showAllClients();

        // logout
        auth.logout();
    }
}
