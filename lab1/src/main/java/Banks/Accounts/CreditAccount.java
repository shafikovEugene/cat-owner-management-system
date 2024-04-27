package Banks.Accounts;

import Banks.Banks.Bank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreditAccount extends BaseAccount {
    private double creditLimit;
    public CreditAccount(Bank bank, double creditLimit) {
        super(bank);
        this.creditLimit = creditLimit;
    }
}
