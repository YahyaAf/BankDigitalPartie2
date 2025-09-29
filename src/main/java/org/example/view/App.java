package org.example.view;

import org.example.controller.AuthController;
import org.example.model.Account;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.UserRepository;
import org.example.repository.implementations.AccountRepositoryImpl;
import org.example.repository.implementations.ClientRepositoryImpl;
import org.example.repository.implementations.UserRepositoryImpl;
import org.example.service.AccountService;
import org.example.service.AuthService;
import org.example.service.ClientService;

import java.util.Scanner;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthService(userRepository);
        AuthController authController = new AuthController(authService);

        boolean running = true;

        while (running) {
            if (authService.getCurrentUser() == null) {
                // Login Menu
                System.out.println("\n=================== Welcome To Bank Islamic =================");
                System.out.println("1. Login");
                System.out.println("0. Exit");
                System.out.print("Choose your choice: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        // Login Process
                        boolean loginSuccessful = false;

                        do {
                            System.out.print("Please enter your email: ");
                            String email = scanner.nextLine().trim();

                            System.out.print("Please enter your password: ");
                            String password = scanner.nextLine().trim();

                            loginSuccessful = authController.login(email, password);

                            if (!loginSuccessful) {
                                System.out.print("Login failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!loginSuccessful);
                        break;

                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } else {
                // Dashboard Menu
                System.out.println("\n=== Dashboard ===");
                System.out.println("1. Show Profile");
                System.out.println("0. Logout");
                System.out.print("Choose option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        authController.showProfile();
                        break;
                    case "0":
                        authController.logout();
                        System.out.println("Successfully logged out.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }

        scanner.close();
        System.out.println("Thank you for using Bank Islamic!");
    }
}