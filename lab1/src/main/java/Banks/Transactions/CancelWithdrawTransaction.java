package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class CancelWithdrawTransaction extends CancelTransaction {
    public CancelWithdrawTransaction(BaseAccount account, double amount, boolean successful, String message) {
        super(null, account, amount, 0, successful, message);
    }

    public CancelWithdrawTransaction(BaseAccount account, double amount, boolean successful) {
        this(account, amount, successful, "");
    }
}
