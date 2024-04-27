package BanksTests;

import Banks.Banks.Bank;
import Banks.Banks.BankPolicies;
import Banks.Banks.CentralBank;
import Banks.Clients.Client;
import Banks.Other.DepositCondition;
import Banks.Other.DepositConditions;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTests {
    @Test
    public void UnverifiedClientCreateTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank foundBank = CentralBank.getInstance().findBank("Sberbank");

        Client client = foundBank.createClient("Anton", "Petrov");

        assertEquals("Anton", client.getFirstName());
        assertEquals("Petrov", client.getLastName());
        assertFalse(client.getVerified());
    }

    @Test
    public void VerifiedClientCreateTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank foundBank = CentralBank.getInstance().findBank("Sberbank");

        Client client = foundBank.createClient("Anton", "Petrov", "Moscow MyAddress", "123123123");

        assertEquals("Anton", client.getFirstName());
        assertEquals("Petrov", client.getLastName());
        assertTrue(client.getVerified());
    }

    @Test
    public void ClientChangeVerificationTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank foundBank = CentralBank.getInstance().findBank("Sberbank");

        Client client = foundBank.createClient("Anton", "Petrov");

        assertEquals("Anton", client.getFirstName());
        assertEquals("Petrov", client.getLastName());
        assertFalse(client.getVerified());

        client.setAddress("address");
        assertFalse(client.getVerified());

        client.setPassport("123123123");
        assertTrue(client.getVerified());
    }

}
