package Banks.Console;

import Banks.Accounts.BaseAccount;
import Banks.Accounts.CreditAccount;
import Banks.Accounts.DebitAccount;
import Banks.Accounts.DepositAccount;
import Banks.Banks.Bank;
import Banks.Banks.BankPolicies;
import Banks.Banks.CentralBank;
import Banks.Clients.Client;
import Banks.Other.DepositCondition;
import Banks.Other.DepositConditions;
import Banks.TimeMachine.TimeMachine;
import Banks.Transactions.Transaction;

import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class Console {
    private final CentralBank centralBank = CentralBank.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        runMainMenu();
    }

    public static void runMainMenu() {
        while (true) {
            System.out.println("\n===== Main menu =====");
            System.out.println("===== Options =====");
            System.out.println("1. Create new bank");
            System.out.println("2. Choose bank");
            System.out.println("3. List banks");
            System.out.println("4. Skip days");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createBank();
                    break;
                case 2:
                    chooseBank();
                    break;
                case 3:
                    listBanks();
                    break;
                case 4:
                    skipDays();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void createBank() {
        System.out.println("Creating new bank");
        System.out.print("Enter bank name: ");
        String bankName = scanner.nextLine();


        System.out.println("Enter bank policies");
        System.out.print("Enter credit commission: ");
        double creditCommission = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter unverified transfer limit: ");
        double unverifiedTransferLimit = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter debit intrest percentage: ");
        double debitIntrestPercentage = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter deposit length in days: ");
        long depositLengthInDays = scanner.nextLong();
        scanner.nextLine();

        DepositConditions depositConditions = new DepositConditions();

        System.out.println("Entering deposit conditions: ");
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Add new condition");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter min bound: ");
                    double minBound = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Enter max bound: ");
                    double maxBound = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Enter yearly percentage: ");
                    double yearlyPercentage = scanner.nextDouble();
                    scanner.nextLine();

                    depositConditions.addCondition(new DepositCondition(minBound, maxBound, yearlyPercentage));

                    System.out.println("Condition added successfully");
                    break;
                case 2:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        CentralBank.getInstance().createBank(
                bankName,
                new BankPolicies(
                        creditCommission,
                        unverifiedTransferLimit,
                        debitIntrestPercentage,
                        depositLengthInDays,
                        depositConditions
                )
        );

        System.out.println("Bank created successfully");
    }

    private static void chooseBank() {
        listBanks();
        System.out.print("Choose bank number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > CentralBank.getInstance().getBanks().size()) {
            System.out.println("Invalid bank number. Returning to main menu...");
            return;
        }

        Bank bank = CentralBank.getInstance().getBanks().get(choice - 1);

        runBankMenu(bank);
    }

    private static void listBanks() {
        System.out.println("List of all banks: ");
        List<Bank> bankList = CentralBank.getInstance().getBanks();
        for (int i = 1; i < bankList.size() + 1; i++) {
            System.out.println(i + ". " + bankList.get(i - 1).getName() + " ID: " + bankList.get(i - 1).getId());
        }
    }

    private static void skipDays() {
        System.out.println("Skipping days");

        System.out.print("Enter number of days to skip: ");
        int n = scanner.nextInt();
        scanner.nextLine();

        TimeMachine.getInstance().skipDays(n);

        System.out.println("Successfully skipped " + n + " days");
    }

    private static void runBankMenu(Bank bank) {
        while (true) {
            System.out.println("\n===== Bank menu =====");
            System.out.println("You are now in " + bank.getName() + " bank.");
            System.out.println("===== Options =====");
            System.out.println("1. Create new client");
            System.out.println("2. Choose client");
            System.out.println("3. List clients");
            System.out.println("4. List transaction history");
            System.out.println("5. Cancel transaction");
            System.out.println("6. Return to main menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createClient(bank);
                    break;
                case 2:
                    chooseClient(bank);
                    break;
                case 3:
                    listClients(bank);
                    break;
                case 4:
                    listTransactionHistory(bank);
                    break;
                case 5:
                    cancelTransaction(bank);
                    break;
                case 6:
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void createClient(Bank bank) {
        System.out.println("Creating new client");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter address (optional): ");
        String address = scanner.nextLine();

        System.out.print("Enter passport (optional): ");
        String passport = scanner.nextLine();

        bank.createClient(firstName, lastName, address, passport);

        System.out.println("Client created successfully");
    }

    private static void chooseClient(Bank bank) {
        listClients(bank);
        System.out.print("Choose client number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > bank.getClients().size()) {
            System.out.println("Invalid client number. Returning to bank menu...");
            return;
        }

        Client client = bank.getClients().get(choice - 1);

        runClientMenu(bank, client);
    }

    private static void listClients(Bank bank) {
        System.out.println("List of all clients in " + bank.getName() + " bank: ");
        for (int i = 1; i < bank.getClients().size() + 1; i++) {
            Client client = bank.getClients().get(i - 1);
            System.out.println(i + ". " + client.getFirstName() + ' ' + client.getLastName() + " ID: " + client.getId());
        }
    }

    private static void listTransactionHistory(Bank bank) {
        System.out.println("List of all transactions in " + bank.getName() + " bank: ");
        for (int i = 1; i < bank.getTransactionHistory().size() + 1; i++) {
            Transaction transaction = bank.getTransactionHistory().get(i - 1);
            System.out.println(i + ". ID: " + transaction.getId()
                    + "; Sender: " + ((transaction.getSender() != null) ? transaction.getSender().getId() : "null")
                    + "; Receiver: " + ((transaction.getReceiver() != null) ? transaction.getReceiver().getId() : "null")
                    + "; Amount: " + transaction.getAmount()
                    + "; Commission: " + transaction.getCommission()
                    + "; Successful: " + transaction.isSuccessful()
                    + "; Message: " + transaction.getMessage()
                    + "; Cancelled: " + transaction.isCanceled()
                    + "; Created at: " + transaction.getTimestamp());
        }
    }

    private static void cancelTransaction(Bank bank) {
        listTransactionHistory(bank);

        System.out.print("Choose transaction number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > bank.getTransactionHistory().size()) {
            System.out.println("Invalid transaction number. Returning to bank menu...");
            return;
        }

        Transaction transaction = bank.getTransactionHistory().get(choice - 1);

        Transaction cancelTransaction = bank.cancelTransaction(transaction);

        if (cancelTransaction.isSuccessful()) {
            System.out.println("Transaction cancelled successfully");
        } else {
            System.out.println("Failed to cancel: " + cancelTransaction.getMessage());
        }
    }

    private static void runClientMenu(Bank bank, Client client) {
        while (true) {
            System.out.println("\n===== Client menu =====");
            System.out.println("You are now in " + bank.getName() + " bank.");
            System.out.println("Chosen client: " + client.getFirstName() + ' ' + client.getLastName() + "; ID: " + client.getId());
            System.out.println("===== Options =====");
            System.out.println("1. Create new account");
            System.out.println("2. Choose account");
            System.out.println("3. List accounts");
            System.out.println("4. Add address");
            System.out.println("5. Add passport");
            System.out.println("6. Return to bank menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createAccount(bank, client);
                    break;
                case 2:
                    chooseAccount(bank, client);
                    break;
                case 3:
                    listAccounts(bank, client);
                    break;
                case 4:
                    addAddress(bank, client);
                    break;
                case 5:
                    addPassport(bank, client);
                    break;
                case 6:
                    System.out.println("Returning to bank menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }




    private static void createAccount(Bank bank, Client client) {
        System.out.println("Creating new account");
        System.out.println("Choose account type: ");
        System.out.println("1. Credit account");
        System.out.println("2. Debit account");
        System.out.println("3. Deposit account");
        System.out.println("4. Cancel");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        BaseAccount account;
        switch (choice) {
            case 1:
                System.out.print("Enter credit limit: ");
                double creditLimit = scanner.nextDouble();
                scanner.nextLine();
                account = bank.createAccount(client, CreditAccount.class, creditLimit);
                break;
            case 2:
                account = bank.createAccount(client, DebitAccount.class, 0);
                break;
            case 3:
                account = bank.createAccount(client, DepositAccount.class, 0);
                break;
            case 4:
                System.out.println("Returning to client menu...");
                return;
            default:
                System.out.println("Invalid choice. Returning to client menu...");
                return;
        }

        System.out.println("Account created successfully");
    }

    private static void chooseAccount(Bank bank, Client client) {
        listAccounts(bank, client);
        System.out.print("Choose account number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > client.getAccounts().size()) {
            System.out.println("Invalid account number. Returning to client menu...");
            return;
        }

        BaseAccount account = client.getAccounts().get(choice - 1);

        runAccountMenu(bank, client, account);
    }

    private static void listAccounts(Bank bank, Client client) {
        System.out.println("List of all accounts owned by " + client.getFirstName() + ' ' + client.getLastName() + ':');

        for (int i = 1; i < client.getAccounts().size() + 1; i++) {
            BaseAccount account = client.getAccounts().get(i - 1);
            System.out.println(i + ". ID: " + account.getId() + "; Balance: " + account.getBalance());
        }
    }

    private static void addAddress(Bank bank, Client client) {
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        client.setAddress(address);

        System.out.print("Successfully set address: " + address);
    }

    private static void addPassport(Bank bank, Client client) {
        System.out.print("Enter passport: ");
        String passport = scanner.nextLine();

        client.setPassport(passport);

        System.out.print("Successfully set passport: " + passport);
    }

    private static void runAccountMenu(Bank bank, Client client, BaseAccount account) {
        while (true) {
            System.out.println("\n===== Account menu =====");
            System.out.println("You are now in " + bank.getName() + " bank.");
            System.out.println("Chosen client: " + client.getFirstName() + ' ' + client.getLastName() + "; ID: " + client.getId());
            System.out.println("Chosen account ID: " + account.getId() + "; Balance: " + account.getBalance());
            System.out.println("===== Options =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Transaction history");
            System.out.println("5. Return to client menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    deposit(account);
                    break;
                case 2:
                    withdraw(account);
                    break;
                case 3:
                    transfer(account);
                    break;
                case 4:
                    listAccountTransactionHistory(account);
                    break;
                case 5:
                    System.out.println("Returning to client menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void deposit(BaseAccount account) {
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Transaction transaction = account.deposit(amount);

        if (transaction.isSuccessful()) {
            System.out.println("Successful deposit");
        } else {
            System.out.println("Failed deposit: " + transaction.getMessage());
        }
    }

    private static void withdraw(BaseAccount account) {
        System.out.print("Enter withdraw amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Transaction transaction = account.withdraw(amount);

        if (transaction.isSuccessful()) {
            System.out.println("Successful withdraw");
        } else {
            System.out.println("Failed withdraw: " + transaction.getMessage());
        }
    }

    private static void transfer(BaseAccount account) {
        listBanks();
        System.out.print("Choose receiver bank number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > CentralBank.getInstance().getBanks().size()) {
            System.out.println("Invalid bank number. Returning to account menu...");
            return;
        }

        Bank receiverBank = CentralBank.getInstance().getBanks().get(choice - 1);

        listClients(receiverBank);
        System.out.print("Choose receiver client number: ");
        choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > receiverBank.getClients().size()) {
            System.out.println("Invalid client number. Returning to account menu...");
            return;
        }

        Client receiver = receiverBank.getClients().get(choice - 1);

        listAccounts(receiverBank, receiver);
        System.out.print("Choose receiver account number: ");
        choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > receiver.getAccounts().size()) {
            System.out.println("Invalid account number. Returning to account menu...");
            return;
        }

        BaseAccount receiverAccount = receiver.getAccounts().get(choice - 1);

        System.out.print("Enter transfer amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Transaction transaction = account.transferTo(receiverAccount, amount);

        if (transaction.isSuccessful()) {
            System.out.println("Successful transfer");
        } else {
            System.out.println("Failed transfer: " + transaction.getMessage());
        }
    }

    private static void listAccountTransactionHistory(BaseAccount account) {
        System.out.println("List of all transactions associated with account " + account.getId() + ':');
        for (int i = 1; i < account.getTransactionHistory().size() + 1; i++) {
            Transaction transaction = account.getTransactionHistory().get(i - 1);
            System.out.println(i + ". ID: " + transaction.getId()
                    + "; Sender: " + ((transaction.getSender() != null) ? transaction.getSender().getId() : "null")
                    + "; Receiver: " + ((transaction.getReceiver() != null) ? transaction.getReceiver().getId() : "null")
                    + "; Amount: " + transaction.getAmount()
                    + "; Commission: " + transaction.getCommission()
                    + "; Successful: " + transaction.isSuccessful()
                    + "; Message: " + transaction.getMessage()
                    + "; Cancelled: " + transaction.isCanceled()
                    + "; Created at: " + transaction.getTimestamp());
        }
    }
}
