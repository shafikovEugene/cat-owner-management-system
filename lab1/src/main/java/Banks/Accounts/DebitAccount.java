package Banks.Accounts;

import Banks.Banks.Bank;

public class DebitAccount extends BaseAccount {
    protected double intrest = 0;

    public DebitAccount(Bank bank) {
        super(bank);
    }

    public void addDailyIntrest() {
        intrest += balance * (bank.getBankPolicies().getDebitIntrestPercentage() / 100 / 365);
    }

    public void collectMonthlyIntrest() {
        this.increaseBalance(intrest);
        intrest = 0;
    }
}
