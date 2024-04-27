package Banks.Accounts;

import Banks.Banks.Bank;
import Banks.Events.Event;
import Banks.Events.VerificationChangeEvent;
import Banks.Transactions.Transaction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class BaseAccount {
    protected final UUID id;
    protected double balance = 0;
    protected List<Transaction> transactionHistory;
    protected Bank bank;
    protected boolean verified = false;

    public BaseAccount(Bank bank) {
        this.id = UUID.randomUUID();
        this.bank = bank;
        this.transactionHistory = new ArrayList<>();
    }

    public Transaction transferTo(UUID other, double amount) {
        BaseAccount otherAccount = bank.findAccount(other);
        return this.transferTo(otherAccount, amount);
    }

    public Transaction transferTo(BaseAccount other, double amount) {
        return bank.transferBetweenAccounts(this, other, amount);
    }

    public Transaction deposit(double amount) {
        return bank.depositToAccount(this, amount);
    }

    public Transaction withdraw(double amount) {
        return bank.withdrawFromAccount(this, amount);
    }
    
    public void processEvent(Event event) {
        if (event instanceof VerificationChangeEvent) {
            verified = ((VerificationChangeEvent) event).isNewValue();
        }
        else {
            System.out.println("Unknown event");
        }
    }

    public void increaseBalance(double amount) {
        balance += amount;
    }

    public void decreaseBalance(double amount) {
        balance -= amount;
    }
}