package com.framework.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public record RequestLoanData(
        String testCaseId,
        double loanAmount,
        double downPayment,
        String fromAccount,
        String expectedStatus,
        String description
) {
    private static final Logger LOG = LoggerFactory.getLogger(TransferData.class);

    public static RequestLoanData fromMap(Map<String, String> data) {

        String testCaseId = data.getOrDefault("TestCaseID", "");
        String rawLoanAmount = data.getOrDefault("LoanAmount", "0.0").trim();
        String rawDownPayment = data.getOrDefault("DownPayment", "0.0").trim();

        double loanAmount = 0.0;
        double downPayment = 0.0;

        try {
            loanAmount = Double.parseDouble(rawLoanAmount);
            downPayment = Double.parseDouble(rawDownPayment);
            if (loanAmount <= 0.0 ||  downPayment <= 0.0) {
                LOG.warn("Test Data Warning [TestCaseID: '{}']: Amount is non-positive: '{}', '{}'.", testCaseId, loanAmount,  downPayment);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Test Data Warning [TestCaseID: '{}']: Invalid numeric value for Amount: '{}', '{}'.", testCaseId, rawLoanAmount,  rawDownPayment);
        }

        return new RequestLoanData(
                testCaseId,
                loanAmount,
                downPayment,
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("ExpectedStatus", ""),
                data.getOrDefault("Description", "")
        );
    }
}