package Banks.Events;

public class DepositLengthInDaysChangeEvent extends LongValueChangeEvent {
    public DepositLengthInDaysChangeEvent(long value) {
        super(value);
    }
}
