package Banks.Events;

import Banks.Other.DepositConditions;
import lombok.Getter;

@Getter
public class DepositConditionsChangeEvent implements Event {
    private final DepositConditions newValue;

    public DepositConditionsChangeEvent(DepositConditions value) {
        this.newValue = value;
    }
}
