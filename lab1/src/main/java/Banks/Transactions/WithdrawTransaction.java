package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class WithdrawTransaction extends Transaction{
    public WithdrawTransaction(BaseAccount account, double amount, boolean successful, String message) {
        super(account, null, amount, 0, successful, message);
    }

    public WithdrawTransaction(BaseAccount account, double amount, boolean successful) {
        this(account, amount, successful, "");
    }
}
