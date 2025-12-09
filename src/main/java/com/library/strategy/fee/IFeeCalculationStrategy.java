package com.library.strategy.fee;

public interface IFeeCalculationStrategy {
    double calculateFee(int daysOverdue);
}
