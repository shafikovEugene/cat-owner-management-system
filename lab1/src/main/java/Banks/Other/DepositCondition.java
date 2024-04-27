package Banks.Other;

public class DepositCondition {
    public double minBound;
    public double maxBound;
    public double yearlyPercentage;

    public DepositCondition(double minBound, double maxBound, double yearlyPercentage) {
        this.minBound = minBound;
        this.maxBound = maxBound;
        this.yearlyPercentage = yearlyPercentage;
    }
}
