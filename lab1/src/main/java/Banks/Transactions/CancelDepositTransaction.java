package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class CancelDepositTransaction extends CancelTransaction {
    public CancelDepositTransaction(BaseAccount account, double amount, boolean successful, String message) {
        super(account, null, amount, 0, successful, message);
    }

    public CancelDepositTransaction(BaseAccount account, double amount, boolean successful) {
        this(account, amount, successful, "");
    }
}
