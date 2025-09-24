package org.example.view;

import org.example.repository.UserRepository;
import org.example.repository.implementations.UserRepositoryImpl;
import org.example.service.AuthService;

public class App {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthService(userRepository);

        // ===== Test Login =====
        System.out.println("🔹 Trying to login with admin@bank.com ...");
        boolean success = authService.login("admin@bank.com", "admin123");

        if (success) {
            // Show profile
            authService.showProfile();

            // Logout
            authService.logout();
        } else {
            System.out.println("Login failed!");
        }
    }
}
