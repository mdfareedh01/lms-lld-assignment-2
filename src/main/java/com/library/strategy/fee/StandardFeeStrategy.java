package com.library.strategy.fee;

public class StandardFeeStrategy implements IFeeCalculationStrategy {
    private static final double DAILY_RATE = 10.0;

    @Override
    public double calculateFee(int daysOverdue) {
        if (daysOverdue <= 0)
            return 0.0;
        return daysOverdue * DAILY_RATE;
    }
}
