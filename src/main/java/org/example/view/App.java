package org.example.view;

import org.example.controller.*;
import org.example.model.*;
import org.example.repository.*;
import org.example.repository.implementations.*;
import org.example.service.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthService(userRepository);
        AuthController authController = new AuthController(authService);

        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientService(clientRepository, authService);
        ClientController clientController = new ClientController(clientService);

        AccountRepository accountRepository = new AccountRepositoryImpl();
        AccountService accountService = new AccountService(accountRepository);
        AccountController accountController = new AccountController(accountService);

        FeeRuleRepository feeRuleRepository = new FeeRuleRepositoryImpl();
        FeeRuleService feeRuleService = new FeeRuleService(feeRuleRepository);
        FeeRuleController feeRuleController = new FeeRuleController(feeRuleService);

        BankRepository bankRepository = new BankRepositoryImpl();
        BankService bankService = new BankService(bankRepository);

        TransactionRepository transactionRepository = new TransactionRepositoryImpl();
        TransactionService transactionService = new TransactionService(transactionRepository, accountRepository, bankService, feeRuleService);
        TransactionController transactionController = new TransactionController(transactionService);

        CreditRepository creditRepository = new CreditRepositoryImpl();
        CreditScheduleRepository scheduleRepository = new CreditScheduleRepositoryImpl();
        CreditService creditService = new CreditService(creditRepository, scheduleRepository, accountRepository, bankService, clientRepository);
        CreditController creditController = new CreditController(creditService);

        TestScheduler scheduler = new TestScheduler(
                creditService,
                accountService,
                clientRepository,
                accountRepository,
                bankService
        );


        ReportService reportService = new ReportService(accountRepository, creditRepository, transactionRepository, clientRepository, bankService);

        boolean running = true;

        while (running) {
            if (authService.getCurrentUser() == null) {
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
                User.Role currentRole = authService.getCurrentUser().getRole();

                displayMenuByRole(currentRole);

                System.out.print("Choose option: ");
                String choice = scanner.nextLine().trim();

                handleMenuChoice(choice, currentRole, scanner, authController, clientController,
                        accountController, transactionController, feeRuleController, creditController,
                        reportService, authService, scheduler);
            }
        }

        scanner.close();
        System.out.println("Thank you for using Bank Islamic!");
    }

    private static void displayMenuByRole(User.Role role) {
        System.out.println("\n=== Dashboard [" + role.name() + "] ===");
        System.out.println("1. Show Profile");

        switch (role) {
            case ADMIN:
                displayAdminMenu();
                break;
            case MANAGER:
                displayManagerMenu();
                break;
            case TELLER:
                displayTellerMenu();
                break;
            case AUDITOR:
                displayAuditorMenu();
                break;
        }

        System.out.println("0. Logout");
    }

    private static void displayAdminMenu() {
        System.out.println("2. Add New Client");
        System.out.println("3. Show All Clients");
        System.out.println("4. Create New Account");
        System.out.println("5. Deactivate Account");
        System.out.println("6. Show All Accounts");
        System.out.println("7. Profile Update");
        System.out.println("8. Create New User");
        System.out.println("9. Deposit Money for Client");
        System.out.println("10. Withdraw Money for Client");
        System.out.println("11. Transfer Money Intern for Client");
        System.out.println("12. Transfer Money Extern for Client");
        System.out.println("13. Add Fee Rule");
        System.out.println("14. Deactivate Fee Rule");
        System.out.println("15. Activate Fee Rule");
        System.out.println("16. Show All Fee Rules");
        System.out.println("17. History of All Transactions");
        System.out.println("18. Request for Credits");
        System.out.println("19. Validation of Credits");
        System.out.println("20. Show All Credits");
        System.out.println("21. Show Total Bank Balance");
        System.out.println("22. Show Credit Revenue");
        System.out.println("23. Show Top Clients in the Bank");
        System.out.println("24. Data Export Rapport to TXT");
    }

    private static void displayManagerMenu() {
        System.out.println("17. History of All Transactions");
        System.out.println("19. Validation of Credits");
        System.out.println("20. Show All Credits");
    }

    private static void displayTellerMenu() {
        System.out.println("2. Add New Client");
        System.out.println("3. Show All Clients");
        System.out.println("4. Create New Account");
        System.out.println("5. Deactivate Account");
        System.out.println("6. Show All Accounts");
        System.out.println("7. Profile Update");
        System.out.println("9. Deposit Money for Client");
        System.out.println("10. Withdraw Money for Client");
        System.out.println("11. Transfer Money Intern for Client");
        System.out.println("12. Transfer Money Extern for Client");
        System.out.println("17. History of All Transactions");
        System.out.println("18. Request for Credits");
    }

    private static void displayAuditorMenu() {
        System.out.println("21. Show Total Bank Balance");
        System.out.println("22. Show Credit Revenue");
        System.out.println("23. Show Top Clients in the Bank");
        System.out.println("24. Data Export Rapport to TXT");
    }

    private static void handleMenuChoice(String choice, User.Role role, Scanner scanner,
                                         AuthController authController, ClientController clientController,
                                         AccountController accountController, TransactionController transactionController,
                                         FeeRuleController feeRuleController, CreditController creditController,
                                         ReportService reportService, AuthService authService, TestScheduler scheduler) {

        if (!hasPermission(role, choice)) {
            System.out.println("⚠️ Access Denied! You don't have permission for this action.");
            return;
        }

        switch (choice) {
            case "1":
                authController.showProfile();
                break;
            case "2":
                handleAddClient(scanner, clientController);
                break;
            case "3":
                clientController.showAllClients();
                break;
            case "4":
                handleCreateAccount(scanner, accountController);
                break;
            case "5":
                handleDeactivateAccount(scanner, accountController);
                break;
            case "6":
                accountController.showAllAccounts();
                break;
            case "7":
                handleUpdateProfile(scanner, authController);
                break;
            case "8":
                handleCreateUser(scanner, authController);
                break;
            case "9":
                handleDeposit(scanner, transactionController);
                break;
            case "10":
                handleWithdraw(scanner, transactionController);
                break;
            case "11":
                handleTransferInternal(scanner, transactionController);
                break;
            case "12":
                handleTransferExternal(scanner, transactionController);
                break;
            case "13":
                handleAddFeeRule(scanner, feeRuleController, authService);
                break;
            case "14":
                handleDeactivateFeeRule(scanner, feeRuleController);
                break;
            case "15":
                handleActivateFeeRule(scanner, feeRuleController);
                break;
            case "16":
                feeRuleController.getAllFeeRules();
                break;
            case "17":
                transactionController.history();
                break;
            case "18":
                handleRequestCredit(scanner, creditController);
                break;
            case "19":
                handleValidateCredit(scanner, creditController, scheduler);
                break;
            case "20":
                creditController.showAllCredits();
                break;
            case "21":
                reportService.showTotalBankBalance();
                break;
            case "22":
                reportService.showCreditRevenue();
                break;
            case "23":
                reportService.showTopClients(3);
                break;
            case "24":
                reportService.exportReportToTXT("BankYahyaIslamic.txt");
                break;
            case "0":
                authController.logout();
                System.out.println("Successfully logged out.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static boolean hasPermission(User.Role role, String option) {
        switch (role) {
            case ADMIN:
                return true;

            case MANAGER:
                return option.equals("1") || option.equals("17") || option.equals("19") || option.equals("20") || option.equals("0");

            case TELLER:
                return option.equals("1") || option.equals("2") || option.equals("3") || option.equals("4") ||
                        option.equals("5") || option.equals("6") || option.equals("7") || option.equals("9") ||
                        option.equals("10") || option.equals("11") || option.equals("12") || option.equals("17") ||
                        option.equals("18") || option.equals("0");

            case AUDITOR:
                return option.equals("1") || option.equals("21") || option.equals("22") || option.equals("23") ||
                        option.equals("24") || option.equals("0");

            default:
                return false;
        }
    }

    private static void handleAddClient(Scanner scanner, ClientController clientController) {
        boolean success = false;
        do {
            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("CIN: ");
            String cin = scanner.nextLine().trim();
            System.out.print("Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Address: ");
            String address = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Salary: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine().trim());

            success = clientController.createClient(firstName, lastName, cin, phone, address, email, salary);
            if (!success) {
                System.out.print("Retry? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) break;
            }
        } while (!success);
    }

    private static void handleCreateAccount(Scanner scanner, AccountController accountController) {
        boolean success = false;
        do {
            System.out.println("Available account types:");
            for (Account.AccountType t : Account.AccountType.values()) {
                System.out.println("- " + t.name());
            }
            System.out.print("Account type: ");
            String type = scanner.nextLine().trim();
            System.out.print("Client ID: ");
            String clientId = scanner.nextLine().trim();

            success = accountController.createAccount(type, clientId);
            if (!success) {
                System.out.print("Retry? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) break;
            }
        } while (!success);
    }

    private static void handleDeactivateAccount(Scanner scanner, AccountController accountController) {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        accountController.deactivateAccount(accountId);
    }

    private static void handleUpdateProfile(Scanner scanner, AuthController authController) {
        System.out.print("New name (empty to skip): ");
        String name = scanner.nextLine().trim();
        System.out.print("New email (empty to skip): ");
        String email = scanner.nextLine().trim();
        System.out.print("New password (empty to skip): ");
        String password = scanner.nextLine().trim();
        authController.updateProfile(name, email, password);
    }

    private static void handleCreateUser(Scanner scanner, AuthController authController) {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.println("Available roles:");
        for (User.Role r : User.Role.values()) {
            System.out.println("- " + r.name());
        }
        System.out.print("Role: ");
        String role = scanner.nextLine().trim();
        authController.createUser(name, email, password, role);
    }

    private static void handleDeposit(Scanner scanner, TransactionController transactionController) {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        transactionController.deposit(accountId, amount);
    }

    private static void handleWithdraw(Scanner scanner, TransactionController transactionController) {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        transactionController.withdraw(accountId, amount);
    }

    private static void handleTransferInternal(Scanner scanner, TransactionController transactionController) {
        System.out.print("Sender Account ID: ");
        String senderId = scanner.nextLine().trim();
        System.out.print("Receiver Account ID: ");
        String receiverId = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        transactionController.transferInternal(senderId, receiverId, amount);
    }

    private static void handleTransferExternal(Scanner scanner, TransactionController transactionController) {
        System.out.print("Sender Account ID: ");
        String senderId = scanner.nextLine().trim();
        System.out.print("External Receiver Account: ");
        String externalAccount = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        transactionController.transferExternal(senderId, externalAccount, amount);
    }

    private static void handleAddFeeRule(Scanner scanner, FeeRuleController feeRuleController, AuthService authService) {
        System.out.println("Operation types:");
        for (Transaction.TransactionType t : Transaction.TransactionType.values()) {
            System.out.println("- " + t.name());
        }
        System.out.print("Operation type: ");
        String operationType = scanner.nextLine().trim();

        System.out.println("Fee modes:");
        for (FeeRule.FeeMode f : FeeRule.FeeMode.values()) {
            System.out.println("- " + f.name());
        }
        System.out.print("Fee mode: ");
        String mode = scanner.nextLine().trim();

        System.out.print("Value: ");
        BigDecimal value = scanner.nextBigDecimal();
        scanner.nextLine();

        System.out.print("Currency: ");
        String currency = scanner.nextLine().trim();

        feeRuleController.addFeeRule(operationType, mode, value, currency, authService.getCurrentUser().getId());
    }

    private static void handleDeactivateFeeRule(Scanner scanner, FeeRuleController feeRuleController) {
        System.out.print("Fee Rule ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        feeRuleController.deactivateFeeRule(id);
    }

    private static void handleActivateFeeRule(Scanner scanner, FeeRuleController feeRuleController) {
        System.out.print("Fee Rule ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        feeRuleController.activateFeeRule(id);
    }

    private static void handleRequestCredit(Scanner scanner, CreditController creditController) {
        System.out.print("Amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Interest rate (%): ");
        double interestRate = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Duration (months): ");
        int duration = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();
        System.out.print("Income proof: ");
        String incomeProof = scanner.nextLine().trim();
        System.out.println("Credit types:");
        for (Credit.CreditType t : Credit.CreditType.values()) {
            System.out.println("- " + t.name());
        }
        System.out.print("Credit type: ");
        String creditType = scanner.nextLine().trim();

        creditController.requestCredit(amount, interestRate, duration, accountId, incomeProof, creditType);
    }

    private static void handleValidateCredit(Scanner scanner, CreditController creditController, TestScheduler scheduler) {
        System.out.print("Credit ID: ");
        String creditId = scanner.nextLine().trim();
        System.out.print("Accept? (true/false): ");
        String isAccepted = scanner.nextLine().trim();

        boolean success = creditController.validateCredit(creditId, isAccepted);
        if (success && !scheduler.isCreditDeductionJobRunning()) {
            scheduler.startCreditDeductionJob();
            System.out.println("Credit deduction job activated!");
        }
    }
}