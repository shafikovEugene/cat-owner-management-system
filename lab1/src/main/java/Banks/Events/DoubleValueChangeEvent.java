package Banks.Events;

import lombok.Getter;

@Getter
public class DoubleValueChangeEvent implements Event {
    private final double newValue;

    public DoubleValueChangeEvent(double value) {
        newValue = value;
    }
}
