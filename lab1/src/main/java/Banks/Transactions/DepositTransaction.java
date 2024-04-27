package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class DepositTransaction extends Transaction {
    public DepositTransaction(BaseAccount account, double amount, boolean successful, String message) {
        super(null, account, amount, 0, successful, message);
    }

    public DepositTransaction(BaseAccount account, double amount, boolean successful) {
        this(account, amount, successful, "");
    }
}
