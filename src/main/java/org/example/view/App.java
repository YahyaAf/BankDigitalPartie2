package org.example.view;

import org.example.controller.*;
import org.example.model.Account;
import org.example.model.FeeRule;
import org.example.model.Transaction;
import org.example.model.User;
import org.example.repository.*;
import org.example.repository.implementations.*;
import org.example.service.*;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthService(userRepository);
        AuthController authController = new AuthController(authService);

        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientService(clientRepository,authService);
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
        TransactionService transactionService = new TransactionService(transactionRepository,accountRepository,bankService,feeRuleService);
        TransactionController transactionController = new TransactionController(transactionService);

        CreditRepository creditRepository = new CreditRepositoryImpl();
        CreditScheduleRepository scheduleRepository = new CreditScheduleRepositoryImpl();
        CreditService creditService = new CreditService(creditRepository,scheduleRepository,accountRepository,bankService);
        CreditController creditController = new CreditController(creditService);

        TestScheduler scheduler = new TestScheduler(
                creditService,
                accountService,
                clientRepository,
                accountRepository,
                bankService
        );
        scheduler.startSalaryJob();

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
                System.out.println("4. Create New Account");
                System.out.println("5. Deactivate Account");
                System.out.println("6. Show All Accounts");
                System.out.println("7. Profile update");
                System.out.println("8. Create New User");
                System.out.println("9. Deposit money for client");
                System.out.println("10. Withdraw money for client");
                System.out.println("11. Transfer money Intern for client");
                System.out.println("12. Transfer money Extern for client");
                System.out.println("13. Add Fee Rule");
                System.out.println("14. Deactivate Fee Rule");
                System.out.println("15 Activate Fee Rule");
                System.out.println("16. Show All Fee Rules");
                System.out.println("17. History of all transactions");
                System.out.println("18. Request for Credits");
                System.out.println("19. Validation of Credits");
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
                    case "4":
                        boolean createAccountSuccessful = false;
                        do {
                            System.out.println("Available account types:");
                            for (Account.AccountType t : Account.AccountType.values()) {
                                System.out.println("- " + t.name());
                            }
                            System.out.print("Please enter type of account: ");
                            String typeAccount = scanner.nextLine().trim();

                            System.out.print("Please enter client id: ");
                            String clientId = scanner.nextLine().trim();

                            createAccountSuccessful = accountController.createAccount(typeAccount, clientId);

                            if (!createAccountSuccessful) {
                                System.out.print("Create account failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!createAccountSuccessful);
                        break;
                    case "5":
                        boolean deactivateAccountSuccessful = false;
                        do {
                            System.out.print("Please enter id of account: ");
                            String accountId = scanner.nextLine().trim();

                            deactivateAccountSuccessful = accountController.deactivateAccount(accountId);

                            if (!deactivateAccountSuccessful) {
                                System.out.print("Deactivate failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!deactivateAccountSuccessful);
                        break;
                    case "6":
                        accountController.showAllAccounts();
                        break;
                    case "7":
                        boolean updateProfileSuccessful = false;
                        do {
                            System.out.print("Enter new name (leave empty to keep current): ");
                            String newName = scanner.nextLine().trim();

                            System.out.print("Enter new email (leave empty to keep current): ");
                            String newEmail = scanner.nextLine().trim();

                            System.out.print("Enter new password (leave empty to keep current): ");
                            String newPassword = scanner.nextLine().trim();

                            updateProfileSuccessful = authController.updateProfile(newName, newEmail, newPassword);

                            if (!updateProfileSuccessful) {
                                System.out.print("Update account failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!updateProfileSuccessful);
                        break;
                    case "8":
                        boolean createNewSuccessful = false;
                        do {
                            System.out.print("Enter name of user: ");
                            String name = scanner.nextLine().trim();

                            System.out.print("Enter email of user: ");
                            String email = scanner.nextLine().trim();

                            System.out.print("Enter password of user: ");
                            String password = scanner.nextLine().trim();

                            System.out.println("Available user roles:");
                            for (User.Role r: User.Role.values()) {
                                System.out.println("- " + r.name());
                            }
                            System.out.print("Please enter role of user: ");
                            String role = scanner.nextLine().trim();

                            createNewSuccessful = authController.createUser(name, email, password, role);

                            if (!createNewSuccessful) {
                                System.out.print("Create new account failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!createNewSuccessful);
                        break;
                    case "9":
                        boolean depositSuccessful = false;
                        do {
                            System.out.print("Please enter id of account: ");
                            String accountId = scanner.nextLine().trim();

                            System.out.print("Please enter amount of deposit: ");
                            BigDecimal depositAmount = scanner.nextBigDecimal();
                            scanner.nextLine();

                            depositSuccessful = transactionController.deposit(accountId,depositAmount);

                            if (!depositSuccessful) {
                                System.out.print("Deposit failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!depositSuccessful);
                        break;
                    case "10":
                        boolean withdrawSuccessful = false;
                        do {
                            System.out.print("Please enter id of account: ");
                            String accountId = scanner.nextLine().trim();

                            System.out.print("Please enter amount of withdraw: ");
                            BigDecimal withdrawAmount = scanner.nextBigDecimal();
                            scanner.nextLine();

                            withdrawSuccessful = transactionController.withdraw(accountId,withdrawAmount);

                            if (!withdrawSuccessful) {
                                System.out.print("Withdraw failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!withdrawSuccessful);
                        break;
                    case "11":
                        boolean transferInternSuccessful = false;
                        do {
                            System.out.print("Please enter id of account sender: ");
                            String senderId = scanner.nextLine().trim();

                            System.out.print("Please enter id of account receiver: ");
                            String receiverId = scanner.nextLine().trim();

                            System.out.print("Please enter amount of transfer intern: ");
                            BigDecimal transferInternAmount = scanner.nextBigDecimal();
                            scanner.nextLine();

                            transferInternSuccessful = transactionController.transferInternal(senderId,receiverId,transferInternAmount);

                            if (!transferInternSuccessful) {
                                System.out.print("Transfer Intern failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!transferInternSuccessful);
                        break;
                    case "12":
                        boolean transferExternSuccessful = false;
                        do {
                            System.out.print("Please enter id of account sender: ");
                            String senderIdExtern = scanner.nextLine().trim();

                            System.out.print("Please enter external receiver account (string, not UUID): ");
                            String externalReceiverAccount = scanner.nextLine().trim();

                            System.out.print("Please enter amount of transfer Extern: ");
                            BigDecimal transferExternAmount = scanner.nextBigDecimal();
                            scanner.nextLine();

                            transferExternSuccessful = transactionController.transferExternal(senderIdExtern,externalReceiverAccount,transferExternAmount);

                            if (!transferExternSuccessful) {
                                System.out.print("Transfer Extern failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!transferExternSuccessful);
                        break;
                    case "13":
                        boolean addFeeRuleSuccessful = false;
                        do {
                            System.out.println("Available operation types:");
                            for (Transaction.TransactionType t: Transaction.TransactionType.values()) {
                                System.out.println("- " + t.name());
                            }
                            System.out.print("Please enter operation type: ");
                            String operationType = scanner.nextLine().trim();

                            System.out.println("Available mode types:");
                            for (FeeRule.FeeMode f: FeeRule.FeeMode.values()) {
                                System.out.println("- " + f.name());
                            }
                            System.out.print("Please enter mode type: ");
                            String modeType = scanner.nextLine().trim();

                            System.out.print("Please enter value of Fee Rule: ");
                            BigDecimal valueFeeRule = scanner.nextBigDecimal();
                            scanner.nextLine();

                            System.out.print("Please enter currency: ");
                            String currency = scanner.nextLine().trim();

                            addFeeRuleSuccessful = feeRuleController.addFeeRule(operationType,modeType,valueFeeRule,currency,authService.getCurrentUser().getId());

                            if (!addFeeRuleSuccessful) {
                                System.out.print("Add feeRule failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!addFeeRuleSuccessful);
                        break;
                    case "14":
                        boolean deasctivateFeeRuleSuccessful = false;
                        do {
                            System.out.print("Please enter id of Fee Rule: ");
                            Long desId = scanner.nextLong();
                            scanner.nextLine();

                            deasctivateFeeRuleSuccessful = feeRuleController.deactivateFeeRule(desId);

                            if (!deasctivateFeeRuleSuccessful) {
                                System.out.print("Deactivate fee rule failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!deasctivateFeeRuleSuccessful);
                        break;
                    case "15":
                        boolean activateFeeRuleSuccessful = false;
                        do {
                            System.out.print("Please enter id of Fee Rule: ");
                            Long actId = scanner.nextLong();
                            scanner.nextLine();

                            activateFeeRuleSuccessful = feeRuleController.activateFeeRule(actId);

                            if (!activateFeeRuleSuccessful) {
                                System.out.print("Active fee rule failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!activateFeeRuleSuccessful);
                        break;
                    case "16":
                        feeRuleController.getAllFeeRules();
                        break;
                    case "17":
                        transactionController.history();
                        break;
                    case "18":
                        boolean requestCreditSuccessful = false;
                        do {
                            System.out.print("Please enter amount of credit requested: ");
                            BigDecimal amount = scanner.nextBigDecimal();
                            scanner.nextLine();

                            System.out.print("Please enter the interest rate (%): ");
                            double interestRate = scanner.nextDouble();
                            scanner.nextLine();

                            System.out.print("Please enter duration in months: ");
                            int durationMonths = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Please enter start date (yyyy-MM-dd) or leave empty for today: ");
                            String startDateInput = scanner.nextLine();

                            System.out.print("Please enter Account ID (UUID): ");
                            String accountIdInput = scanner.nextLine();

                            System.out.print("Please enter income proof description: ");
                            String incomeProof = scanner.nextLine();

                            requestCreditSuccessful = creditController.requestCredit(amount,interestRate,durationMonths,startDateInput,accountIdInput,incomeProof);

                            if (!requestCreditSuccessful) {
                                System.out.print("Request credit is failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!requestCreditSuccessful);
                        break;
                    case "19":
                        boolean validateCreditSuccessful = false;
                        do {
                            System.out.print("Please enter Credit ID: ");
                            String creditId = scanner.nextLine().trim();

                            System.out.print("Please add (true/false): ");
                            String isAccepted = scanner.nextLine().trim();

                            validateCreditSuccessful = creditController.validateCredit(creditId, isAccepted);

                            if (validateCreditSuccessful) {
                                if (!scheduler.isCreditDeductionJobRunning()) {
                                    scheduler.startCreditDeductionJob();
                                    System.out.println("Credit deduction job activated!");
                                }
                            } else {
                                System.out.print("Validation of credit is failed. Try again? (y/n): ");
                                String retry = scanner.nextLine().trim();

                                if (!retry.equalsIgnoreCase("y")) {
                                    break;
                                }
                            }
                        } while (!validateCreditSuccessful);
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