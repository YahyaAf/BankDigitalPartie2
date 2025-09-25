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

        // create client
        tellerService.createClient(
                "Mohamed",
                "Ali",
                "CIN123456",
                "+212600000000",
                "Rue X, Casablanca",
                "mohamed.ali@example.com",
                new BigDecimal("3500.50")
        );

        // logout
        auth.logout();
    }
}
