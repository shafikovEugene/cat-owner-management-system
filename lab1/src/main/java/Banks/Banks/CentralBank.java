package Banks.Banks;

import Banks.Accounts.BaseAccount;
import Banks.Accounts.CreditAccount;
import Banks.Accounts.DepositAccount;
import Banks.Transactions.CancelTransaction;
import Banks.Transactions.CancelTransferTransaction;
import Banks.Transactions.Transaction;
import Banks.Transactions.TransferTransaction;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CentralBank {
    private static CentralBank instance;
    private final UUID id;
    private final List<Bank> banks;
    private final List<Transaction> transactionHistory;

    private CentralBank() {
        id = UUID.randomUUID();
        banks = new ArrayList<>();
        transactionHistory = new ArrayList<>();
    }

    public static CentralBank getInstance() {
        if (instance == null) {
            instance = new CentralBank();
        }
        return instance;
    }

    public void createBank(String name, BankPolicies bankPolicies) {
        banks.add(new Bank(name, bankPolicies));
    }

    public void deleteBank(UUID id) {
        banks.removeIf(bank -> bank.getId().equals(id));
    }

    public Bank findBank(String bankName) {
        for (Bank bank: banks) {
            if (bank.getName().equals(bankName)) {
                return bank;
            }
        }

        throw new IllegalArgumentException("Error: Bank with name " + bankName + " not found.");
    }

    public Transaction findTransaction(UUID transactionId) {
        for (Bank bank: banks) {
            for (Transaction transaction: bank.getTransactionHistory()) {
                if (transaction.getId().equals(transactionId)) {
                    return transaction;
                }
            }
        }

        throw new IllegalArgumentException("Error: Transaction with id " + transactionId + " not found.");
    }

    public Transaction transferBetweenTwoBanks(BaseAccount sender, BaseAccount receiver, double amount) {
        TransferTransaction transaction;

        if (amount < 0) {
            System.out.println("Failed transfer: Transfer amount is negative");
            transaction = new TransferTransaction(sender, receiver, amount, false);
        } else if (!sender.isVerified() && amount > sender.getBank().getBankPolicies().getUnverifiedTransferLimit()) {
            System.out.println("Failed transfer: Transfer amount is bigger than unverified transfer limit");
            transaction = new TransferTransaction(sender, receiver, amount, false);
        } else if (sender instanceof CreditAccount) {
            if (sender.getBalance() + ((CreditAccount) sender).getCreditLimit() - amount >= 0) {
                sender.decreaseBalance(amount);
                receiver.increaseBalance(amount);
                transaction = new TransferTransaction(sender, receiver, amount, true);
            } else {
                sender.decreaseBalance(amount + sender.getBank().getBankPolicies().getCreditCommission());
                receiver.increaseBalance(amount);
                transaction = new TransferTransaction(sender, receiver, amount, sender.getBank().getBankPolicies().getCreditCommission(), true);
            }
        } else if (sender instanceof DepositAccount && LocalDate.now().isBefore(((DepositAccount) sender).getDepositEndDate())) {
            System.out.println("Failed transfer: Deposit period has not ended yet");
            transaction = new TransferTransaction(sender, receiver, amount, false);
        } else if (sender.getBalance() - amount < 0) {
            System.out.println("Failed transfer: Insufficient sender balance");
            transaction = new TransferTransaction(sender, receiver, amount, false);
        } else {
            sender.decreaseBalance(amount);
            receiver.increaseBalance(amount);
            transaction = new TransferTransaction(sender, receiver, amount, true);
        }

        sender.getTransactionHistory().add(transaction);
        receiver.getTransactionHistory().add(transaction);

        transactionHistory.add(transaction);
        sender.getBank().getTransactionHistory().add(transaction);
        receiver.getBank().getTransactionHistory().add(transaction);

        return transaction;
    }

    public UUID cancelTransaction(UUID transactionId) {
        Transaction transaction = findTransaction(transactionId);

        return cancelTransaction(transaction).getId();
    }

    public Transaction cancelTransaction(Transaction transaction) {
        CancelTransaction cancelTransaction;

        transaction.getSender().increaseBalance(transaction.getAmount() + transaction.getCommission());
        transaction.getReceiver().decreaseBalance(transaction.getAmount());
        cancelTransaction = new CancelTransferTransaction(transaction.getReceiver(), transaction.getSender(), transaction.getAmount(), true);

        transaction.setCanceled(true);
        cancelTransaction.setCanceled(true);

        transaction.getSender().getTransactionHistory().add(cancelTransaction);
        transaction.getReceiver().getTransactionHistory().add(cancelTransaction);

        transactionHistory.add(cancelTransaction);
        transaction.getSender().getBank().getTransactionHistory().add(cancelTransaction);
        transaction.getReceiver().getBank().getTransactionHistory().add(cancelTransaction);

        return transaction;
    }

    public void addDailyIntrest() {
        for (Bank bank: banks) {
            bank.addDailyIntrest();
        }
    }

    public void collectMonthlyIntrest() {
        for (Bank bank: banks) {
            bank.collectMonthlyIntrest();
        }
    }
}
