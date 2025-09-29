package org.example.view;

import org.example.controller.AuthController;
import org.example.controller.ClientController;
import org.example.repository.ClientRepository;
import org.example.repository.UserRepository;
import org.example.repository.implementations.ClientRepositoryImpl;
import org.example.repository.implementations.UserRepositoryImpl;
import org.example.service.AuthService;
import org.example.service.ClientService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthService(userRepository);
        AuthController authController = new AuthController(authService);

        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientService(clientRepository,authService);
        ClientController clientController = new ClientController(clientService);

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
                System.out.println("\n=== Dashboard ===");
                System.out.println("1. Show Profile");
                System.out.println("2. Add New Client");
                System.out.println("3. Show All Clients");
                System.out.println("0. Logout");
                System.out.print("Choose option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        authController.showProfile();
                        break;
                    case "2":
                        boolean createClientSuccessful = false;
                        do {
                            System.out.print("Please enter your first name: ");
                            String firstName = scanner.nextLine().trim();

                            System.out.print("Please enter your last name: ");
                            String lastName = scanner.nextLine().trim();

                            System.out.print("Please enter your cin: ");
                            String cin = scanner.nextLine().trim();

                            System.out.print("Please enter your phone number: ");
                            String phoneNumber = scanner.nextLine().trim();

                            System.out.print("Please enter your address: ");
                            String address = scanner.nextLine().trim();

                            System.out.print("Please enter your email: ");
                            String email = scanner.nextLine().trim();

                            System.out.print("Please enter your salary: ");
                            BigDecimal salary = new BigDecimal(scanner.nextLine().trim());

                            createClientSuccessful = clientController.createClient(firstName, lastName, cin, phoneNumber, address, email, salary);

                            if (!createClientSuccessful) {
                                System.out.print("Creation of client failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!createClientSuccessful);
                        break;
                    case "3":
                        clientController.showAllClients();
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