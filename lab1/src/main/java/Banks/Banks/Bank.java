package Banks.Banks;

import Banks.Accounts.BaseAccount;
import Banks.Accounts.CreditAccount;
import Banks.Accounts.DebitAccount;
import Banks.Accounts.DepositAccount;
import Banks.Clients.Client;
import Banks.Clients.EventListener;
import Banks.Other.DepositConditions;
import Banks.Transactions.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Bank {
    private final UUID id;
    private final String name;
    private final List<Transaction> transactionHistory;
    private final BankPolicies bankPolicies;
    private final List<Client> clients;
    private final List<EventListener> subscribers;

    public Bank(String name, BankPolicies bankPolicies) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.bankPolicies = bankPolicies;
        this.transactionHistory = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    public Client createClient(String firstName, String lastName, String address, String passport) {
        Client client = new Client(firstName, lastName, address, passport);
        addClient(client);
        return client;
    }

    public Client createClient(String firstName, String lastName) {
        return createClient(firstName, lastName, "", "");
    }

    public <TAccount extends BaseAccount> UUID createAccount(UUID ownerId, Class<TAccount> accountType, double creditLimit) {
        Client owner = findClient(ownerId);

        return createAccount(owner, accountType, creditLimit).getId();
    }

    public <TAccount extends BaseAccount> BaseAccount createAccount(Client owner, Class<TAccount> accountType, double creditLimit) {
        BaseAccount account;
        if (accountType == CreditAccount.class) {
            account = new CreditAccount(this, creditLimit);
        } else if (accountType == DebitAccount.class) {
            account = new DebitAccount(this);
        } else if (accountType == DepositAccount.class) {
            account = new DepositAccount(this);
        } else {
            account = null;
        }

        owner.addAccount(account);

        return account;
    }

    public UUID withdrawFromAccount(UUID accountId, double amount) {
        BaseAccount account = findAccount(accountId);

        return withdrawFromAccount(account, amount).getId();
    }

    public Transaction withdrawFromAccount(BaseAccount account, double amount) {
        WithdrawTransaction transaction;

        if (amount < 0) {
            transaction = new WithdrawTransaction(account, amount, false, "Withdraw amount is negative");
        } else if (!account.isVerified()) {
            transaction = new WithdrawTransaction(account, amount, false, "Account is not verified");
        } else if (account instanceof CreditAccount) {
            if (account.getBalance() + ((CreditAccount) account).getCreditLimit() - amount >= 0) {
                account.decreaseBalance(amount);
                transaction = new WithdrawTransaction(account, amount, true);
            } else {
                transaction = new WithdrawTransaction(account, amount, false, "Insufficient balance");
            }
        } else if (account instanceof DepositAccount && LocalDate.now().isBefore(((DepositAccount) account).getDepositEndDate())) {
            transaction = new WithdrawTransaction(account, amount, false, "Deposit period has not ended yet");
        } else if (account.getBalance() - amount < 0) {
            transaction = new WithdrawTransaction(account, amount, false, "Insufficient account balance");
        } else {
            account.decreaseBalance(amount);
            transaction = new WithdrawTransaction(account, amount, true);
        }

        transactionHistory.add(transaction);
        account.getTransactionHistory().add(transaction);
        return transaction;
    }

    public UUID depositToAccount(UUID accountId, double amount) {
        BaseAccount account = findAccount(accountId);

        return depositToAccount(account, amount).getId();
    }

    public Transaction depositToAccount(BaseAccount account, double amount) {
        DepositTransaction transaction;

        if (amount < 0) {
            transaction = new DepositTransaction(account, amount, false, "Deposit amount is negative");
        }
        else {
            account.increaseBalance(amount);
            transaction = new DepositTransaction(account, amount, true);
        }

        transactionHistory.add(transaction);
        account.getTransactionHistory().add(transaction);
        return transaction;
    }

    public UUID transferBetweenAccounts(UUID senderAccountId, UUID receiverAccountId, double amount) {
        BaseAccount sender = findAccount(senderAccountId);
        BaseAccount receiver = findAccount(receiverAccountId);

        return transferBetweenAccounts(sender, receiver, amount).getId();
    }

    public Transaction transferBetweenAccounts(BaseAccount sender, BaseAccount receiver, double amount) {
        TransferTransaction transaction;

        if (sender.getBank() != receiver.getBank()) {
            return CentralBank.getInstance().transferBetweenTwoBanks(sender, receiver, amount);
        }

        if (amount < 0) {
            transaction = new TransferTransaction(sender, receiver, amount, false, "Transfer amount is negative");
        } else if (!sender.isVerified() && amount > bankPolicies.getUnverifiedTransferLimit()) {
            transaction = new TransferTransaction(sender, receiver, amount, false, "Transfer amount is bigger than unverified transfer limit");
        } else if (sender instanceof CreditAccount) {
            if (sender.getBalance() + ((CreditAccount) sender).getCreditLimit() - amount >= 0) {
                sender.decreaseBalance(amount);
                receiver.increaseBalance(amount);
                transaction = new TransferTransaction(sender, receiver, amount, true);
            } else {
                sender.decreaseBalance(amount + bankPolicies.getCreditCommission());
                receiver.increaseBalance(amount);
                transaction = new TransferTransaction(sender, receiver, amount, bankPolicies.getCreditCommission(), true);
            }
        } else if (sender instanceof DepositAccount && LocalDate.now().isBefore(((DepositAccount) sender).getDepositEndDate())) {
            transaction = new TransferTransaction(sender, receiver, amount, false, "Deposit period has not ended yet");
        } else if (sender.getBalance() - amount < 0) {
            transaction = new TransferTransaction(sender, receiver, amount, false, "Insufficient balance");
        } else {
            sender.decreaseBalance(amount);
            receiver.increaseBalance(amount);
            transaction = new TransferTransaction(sender, receiver, amount, true);
        }

        transactionHistory.add(transaction);
        sender.getTransactionHistory().add(transaction);
        receiver.getTransactionHistory().add(transaction);

        return transaction;
    }

    public UUID cancelTransaction(UUID transactionId) {
        Transaction transaction = findTransaction(transactionId);

        return cancelTransaction(transaction).getId();
    }

    public Transaction cancelTransaction(Transaction transaction) {
        if (transaction.isCanceled()) {
            System.out.println("Failed to cancel transaction with id " + transaction.getId() + ". Transaction is already cancelled.");
            return null;
        }

        if (!transaction.isSuccessful()) {
            System.out.println("Failed to cancel transaction with id " + transaction.getId() + ". Transaction is not successful.");
            return null;
        }

        Transaction cancelTransaction;

        if (transaction.getSender().getBank() != transaction.getReceiver().getBank()) {
            return CentralBank.getInstance().cancelTransaction(transaction);
        } else if (transaction instanceof TransferTransaction) {
            transaction.getSender().increaseBalance(transaction.getAmount() + transaction.getCommission());
            transaction.getReceiver().decreaseBalance(transaction.getAmount());
            cancelTransaction = new CancelTransferTransaction(transaction.getReceiver(), transaction.getSender(), transaction.getAmount(), true);
        } else if (transaction instanceof DepositTransaction) {
            transaction.getReceiver().decreaseBalance(transaction.getAmount());
            cancelTransaction = new CancelDepositTransaction(transaction.getReceiver(), transaction.getAmount(), true);
        } else {
            transaction.getSender().increaseBalance(transaction.getAmount() + transaction.getCommission());
            cancelTransaction = new CancelWithdrawTransaction(transaction.getSender(), transaction.getAmount(), true);
        }

        cancelTransaction.setCanceled(true);
        transaction.setCanceled(true);

        transactionHistory.add(cancelTransaction);
        transaction.getSender().getTransactionHistory().add(cancelTransaction);
        transaction.getReceiver().getTransactionHistory().add(cancelTransaction);

        return cancelTransaction;
    }

    public BaseAccount findAccount(UUID accountId) {
        for (Client client: clients) {
            for (BaseAccount account: client.getAccounts()) {
                if (account.getId().equals(accountId)) {
                    return account;
                }
            }
        }

        throw new IllegalArgumentException("Error: Account with id " + accountId + " not found.");
    }

    public Client findClient(UUID clientId) {
        for (Client client: clients) {
            if (client.getId().equals(clientId)) {
                return client;
            }
        }

        throw new IllegalArgumentException("Error: Client with id " + clientId + " not found.");
    }

    public Transaction findTransaction(UUID transactionId) {
        for (Transaction transaction: transactionHistory) {
            if (transaction.getId().equals(transactionId)) {
                return transaction;
            }
        }

        throw new IllegalArgumentException("Error: Transaction with id " + transactionId + " not found.");
    }

    public void addDailyIntrest() {
        for (Client client: clients) {
            for (BaseAccount account: client.getAccounts()) {
                if (account instanceof DebitAccount) {
                    ((DebitAccount) account).addDailyIntrest();
                } else if (account instanceof DepositAccount) {
                    ((DepositAccount) account).addDailyIntrest();
                }
            }
        }
    }

    public void collectMonthlyIntrest() {
        for (Client client: clients) {
            for (BaseAccount account: client.getAccounts()) {
                if (account instanceof DebitAccount) {
                    ((DebitAccount) account).collectMonthlyIntrest();
                } else if (account instanceof DepositAccount) {
                    ((DepositAccount) account).collectMonthlyIntrest();
                }
            }
        }
    }

    public void addClient(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }

    public void removeClient(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }

    public void setCreditCommission(double value) {
        bankPolicies.setCreditCommission(value);
        notifySubscribers("Credit commission has been changed to " + value + '.');
    }

    public void setUnverifiedCreditLimit(double value) {
        bankPolicies.setUnverifiedTransferLimit(value);
        notifySubscribers("Unverified credit limit has been changed to " + value + '.');
    }

    public void setDebitIntrestPercentage(double value) {
        bankPolicies.setDebitIntrestPercentage(value);
        notifySubscribers("Debit intrest percentage has been changed to " + value + '.');
    }

    public void setDepositLengthInDays(long value) {
        bankPolicies.setDepositLengthInDays(value);
        notifySubscribers("Deposit length has been changed to " + value + " days.");
    }

    public void setDepositConditions(DepositConditions newDepositConditions) {
        bankPolicies.setDepositConditions(newDepositConditions);
        notifySubscribers("Deposit conditions have been changed.\nNew " + newDepositConditions.toString());
    }

    public void notifySubscribers(String message) {
        for (EventListener client: subscribers) {
            client.notify(message);
        }
    }

    public void addSubscriber(Client client) {
        if (!subscribers.contains(client)) {
            subscribers.add(client);
        }
    }

    public void removeSubscriber(Client client) {
        subscribers.remove(client);
    }
}
