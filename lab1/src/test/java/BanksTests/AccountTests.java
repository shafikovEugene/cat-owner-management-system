package BanksTests;

import Banks.Accounts.BaseAccount;
import Banks.Accounts.DebitAccount;
import Banks.Banks.Bank;
import Banks.Banks.BankPolicies;
import Banks.Banks.CentralBank;
import Banks.Clients.Client;
import Banks.Other.DepositCondition;
import Banks.Other.DepositConditions;
import Banks.Transactions.Transaction;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountTests {
    @Test
    public void DepositTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank bank = CentralBank.getInstance().findBank("Sberbank");

        Client client = bank.createClient("Anton", "Petrov");

        BaseAccount account = bank.createAccount(client, DebitAccount.class, 0);

        assertEquals(0, account.getBalance(), 0.001);
        account.deposit(10000);
        assertEquals(10000, account.getBalance(), 0.001);
    }

    @Test
    public void WithdrawTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank bank = CentralBank.getInstance().findBank("Sberbank");

        Client client = bank.createClient("Anton", "Petrov", "address", "123123");

        BaseAccount account = bank.createAccount(client, DebitAccount.class, 0);

        assertEquals(0, account.getBalance(), 0.001);
        account.deposit(10000);
        assertEquals(10000, account.getBalance(), 0.001);
        account.withdraw(1000);
        assertEquals(9000, account.getBalance(), 0.001);
    }

    @Test
    public void WithdrawUnverifiedTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank bank = CentralBank.getInstance().findBank("Sberbank");

        Client client = bank.createClient("Anton", "Petrov");

        BaseAccount account = bank.createAccount(client, DebitAccount.class, 0);

        assertFalse(account.isVerified());

        assertEquals(0, account.getBalance(), 0.001);
        account.deposit(10000);
        assertEquals(10000, account.getBalance(), 0.001);
        Transaction t = account.withdraw(1000);
        assertEquals(10000, account.getBalance(), 0.001);
        assertFalse(t.isSuccessful());
    }

    @Test
    public void TransferTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 100000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank bank = CentralBank.getInstance().findBank("Sberbank");

        Client client1 = bank.createClient("Anton", "Petrov");
        Client client2 = bank.createClient("Ivan", "Ivanov");

        BaseAccount account1 = bank.createAccount(client1, DebitAccount.class, 0);
        BaseAccount account2 = bank.createAccount(client2, DebitAccount.class, 0);


        account1.deposit(10000);
        assertEquals(0, account2.getBalance(), 0.001);
        Transaction t = account1.transferTo(account2, 1000);
        assertEquals(1000, account2.getBalance(), 0.001);
        assertTrue(t.isSuccessful());
    }
}
