package Banks.Events;

public class UnverifiedTransferLimitChangeEvent extends DoubleValueChangeEvent {
    public UnverifiedTransferLimitChangeEvent(double value) {
        super(value);
    }
}
