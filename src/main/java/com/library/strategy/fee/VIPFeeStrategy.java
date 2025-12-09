package com.library.strategy.fee;

public class VIPFeeStrategy implements IFeeCalculationStrategy {
    private static final double DAILY_RATE = 5.0;
    private static final double MAX_FEE = 50.0;

    @Override
    public double calculateFee(int daysOverdue) {
        if (daysOverdue <= 0)
            return 0.0;
        return Math.min(daysOverdue * DAILY_RATE, MAX_FEE);
    }
}
