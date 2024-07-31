package Banks.Accounts;

import Banks.Banks.Bank;
import lombok.Getter;

import java.time.LocalDate;

public class DepositAccount extends BaseAccount {
    private double intrest = 0;
    @Getter
    private final LocalDate depositStartDate;
    @Getter
    private final LocalDate depositEndDate;

    public DepositAccount(Bank bank) {
        super(bank);
        this.depositStartDate = LocalDate.now();
        this.depositEndDate = LocalDate.now().plusDays(bank.getBankPolicies().getDepositLengthInDays());
    }

    public void addDailyIntrest() {
        intrest += balance * (bank.getBankPolicies().getDepositConditions().getYearlyPercentage(balance) / 100 / 365);
    }

    public void collectMonthlyIntrest() {
        this.increaseBalance(intrest);
        intrest = 0;
    }
}