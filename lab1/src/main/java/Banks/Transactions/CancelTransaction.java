package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class CancelTransaction extends Transaction {
    public CancelTransaction(BaseAccount sender, BaseAccount receiver, double amount, double commission, boolean successful, String message) {
        super(sender, receiver, amount, commission, successful, message);
    }
}
