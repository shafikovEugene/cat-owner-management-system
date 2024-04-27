package Banks.Transactions;

import Banks.Accounts.BaseAccount;

public class TransferTransaction extends Transaction {
    public TransferTransaction(BaseAccount sender, BaseAccount receiver, double amount, double commission, boolean successful, String message) {
        super(sender, receiver, amount, commission, successful, message);
    }

    public TransferTransaction(BaseAccount sender, BaseAccount receiver, double amount, double commission, boolean successful) {
        this(sender, receiver, amount, commission, successful, "");
    }

    public TransferTransaction(BaseAccount sender, BaseAccount receiver, double amount, boolean successful, String message) {
        this(sender, receiver, amount, 0, successful, message);
    }

    public TransferTransaction(BaseAccount sender, BaseAccount receiver, double amount, boolean successful) {
        this(sender, receiver, amount, successful, "");
    }
}
