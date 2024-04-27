package Banks.Events;

import lombok.Getter;

@Getter
public class BooleanValueChangeEvent implements Event {
    private final boolean newValue;

    public BooleanValueChangeEvent(boolean value) {
        newValue = value;
    }
}
