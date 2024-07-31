package Banks.Banks;

import Banks.Other.DepositConditions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankPolicies {
    private double creditCommission;
    private double unverifiedTransferLimit;
    private double debitIntrestPercentage;
    private long depositLengthInDays;
    private DepositConditions depositConditions;

    public BankPolicies(double creditCommission, double unverifiedTransferLimit, double debitIntrestPercentage, long depositLengthInDays, DepositConditions depositConditions) {
        this.creditCommission = creditCommission;
        this.unverifiedTransferLimit = unverifiedTransferLimit;
        this.debitIntrestPercentage = debitIntrestPercentage;
        this.depositLengthInDays = depositLengthInDays;
        this.depositConditions = depositConditions;
    }
}
