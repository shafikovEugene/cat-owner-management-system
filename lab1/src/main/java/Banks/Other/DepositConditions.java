package Banks.Other;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DepositConditions {
    private final List<DepositCondition> conditions = new ArrayList<>();

    public DepositConditions addCondition(DepositCondition condition) {
        if (checkForOverlap(condition)) {
            throw new IllegalArgumentException("The new condition overlaps with existing conditions.");
        }
        conditions.add(condition);
        return this;
    }

    public double getYearlyPercentage(double balance) {
        for (DepositCondition condition : conditions) {
            if (balance >= condition.minBound && balance < condition.maxBound) {
                return condition.yearlyPercentage;
            }
        }
        throw new IllegalArgumentException("No matching deposit condition found for the balance: " + balance);
    }

    public String toString() {
        List<DepositCondition> sortedConditions = conditions.stream()
                .sorted(Comparator.comparingDouble(condition -> condition.minBound))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("Deposit Conditions:\n");
        for (DepositCondition condition : sortedConditions) {
            sb.append(String.format("Min Bound: %.2f, Max Bound: %.2f, Yearly Percentage: %.2f%%\n",
                    condition.minBound, condition.maxBound, condition.yearlyPercentage));
        }
        return sb.toString();
    }

    private boolean checkForOverlap(DepositCondition newCondition) {
        for (DepositCondition existingCondition : conditions) {
            if (overlapExists(newCondition, existingCondition)) {
                return true;
            }
        }
        return false;
    }

    private boolean overlapExists(DepositCondition newCondition, DepositCondition existingCondition) {
        return (newCondition.minBound < existingCondition.maxBound) &&
                (newCondition.maxBound > existingCondition.minBound);
    }
}
