package com.framework.models;

import java.util.Map;

public record RequestLoanData(
        String testCaseId,
        double loanAmount,
        double downPayment,
        String fromAccount,
        String expectedStatus,
        String description
) {
    public static RequestLoanData fromMap(Map<String, String> data) {
        double loanAmount = 0.0;
        double downPayment = 0.0;
        try {
            loanAmount = Double.parseDouble(data.getOrDefault("LoanAmount", "0.0"));
            downPayment = Double.parseDouble(data.getOrDefault("DownPayment", "0.0"));
        } catch (NumberFormatException ignored) {}

        return new RequestLoanData(
                data.getOrDefault("TestCaseID", ""),
                loanAmount,
                downPayment,
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("ExpectedStatus", ""),
                data.getOrDefault("Description", "")
        );
    }
}
