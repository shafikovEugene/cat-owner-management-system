package Banks.Events;

public class CreditLimitChangeEvent extends DoubleValueChangeEvent {
    public CreditLimitChangeEvent(double value) {
        super(value);
    }
}
