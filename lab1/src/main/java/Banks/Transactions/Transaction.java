package Banks.Transactions;

import Banks.Accounts.BaseAccount;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class Transaction {
    private final UUID id;
    private final BaseAccount sender;
    private final BaseAccount receiver;
    private final double amount;
    private final double commission;
    private final boolean successful;
    private final String message;
    @Setter
    private boolean canceled = false;
    private final LocalDateTime timestamp;

    public Transaction(BaseAccount sender, BaseAccount receiver, double amount, double commission, boolean successful, String message) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.commission = commission;
        this.successful = successful;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
