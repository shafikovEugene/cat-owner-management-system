package BanksTests;

import Banks.Banks.Bank;
import Banks.Banks.BankPolicies;
import Banks.Banks.CentralBank;
import Banks.Other.DepositCondition;
import Banks.Other.DepositConditions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateBankTests {
    @Test
    public void BankCreateTest() {
        BankPolicies bankPolicies = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies);

        Bank foundBank = CentralBank.getInstance().findBank("Sberbank");
        assertEquals("Sberbank", foundBank.getName());
        assertEquals(bankPolicies, foundBank.getBankPolicies());
    }

    @Test
    public void MultipleBanksCreateTest() {
        BankPolicies bankPolicies1 = new BankPolicies(100, 10000, 5, 90, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        BankPolicies bankPolicies2 = new BankPolicies(150, 15000, 2, 180, new DepositConditions().addCondition(new DepositCondition(1, 999999999, 5)));
        CentralBank.getInstance().createBank("Sberbank", bankPolicies1);
        CentralBank.getInstance().createBank("VTB", bankPolicies2);

        Bank foundBank1 = CentralBank.getInstance().findBank("Sberbank");
        Bank foundBank2 = CentralBank.getInstance().findBank("VTB");
        assertEquals("Sberbank", foundBank1.getName());
        assertEquals("VTB", foundBank2.getName());
        assertEquals(bankPolicies1, foundBank1.getBankPolicies());
        assertEquals(bankPolicies2, foundBank2.getBankPolicies());
    }
}
