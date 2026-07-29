package com.framework.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class LoanScenarioCalculator {

    private LoanScenarioCalculator() {}

    public record LoanTerms(String loanAmount, String downPayment) {}

    private static final BigDecimal SAFETY_MARGIN = BigDecimal.valueOf(1.5);

    public static LoanTerms forApproval(String loanProcessor, BigDecimal thresholdPercent, BigDecimal totalBalance) {
        BigDecimal thresholdRatio = thresholdPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal downPayment = BigDecimal.valueOf(10);

        BigDecimal loanAmount = switch (loanProcessor) {
            // qualifier = totalBalance / loanAmount >= threshold  =>  loanAmount <= totalBalance / threshold
            case "funds" ->
                    totalBalance.divide(thresholdRatio.multiply(SAFETY_MARGIN), 2, RoundingMode.HALF_UP);
            // qualifier = downPayment / loanAmount >= threshold  =>  loanAmount <= downPayment / threshold
            case "down" ->
                    downPayment.divide(thresholdRatio.multiply(SAFETY_MARGIN), 2, RoundingMode.HALF_UP);
            // qualifier = (totalBalance - downPayment) / (loanAmount - downPayment) >= threshold
            case "combined" -> {
                BigDecimal fundsBalance = totalBalance.subtract(downPayment);
                BigDecimal maxLoanBalance = fundsBalance.divide(thresholdRatio.multiply(SAFETY_MARGIN), 2, RoundingMode.HALF_UP);
                yield maxLoanBalance.add(downPayment);
            }
            default -> throw new IllegalStateException("Unrecognized loan processor: " + loanProcessor);
        };

        return new LoanTerms(loanAmount.max(BigDecimal.ONE).toPlainString(), downPayment.toPlainString());
    }

    public static LoanTerms forDenial(String loanProcessor, BigDecimal thresholdPercent, BigDecimal totalBalance) {
        BigDecimal loanAmount = totalBalance.max(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(1000));
        return new LoanTerms(loanAmount.toPlainString(), "10");
    }
}