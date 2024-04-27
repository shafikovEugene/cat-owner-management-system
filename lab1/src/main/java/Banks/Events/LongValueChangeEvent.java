package Banks.Events;

import lombok.Getter;

@Getter
public class LongValueChangeEvent implements Event {
    private final long newValue;

    public LongValueChangeEvent(long value) {
        newValue = value;
    }
}
