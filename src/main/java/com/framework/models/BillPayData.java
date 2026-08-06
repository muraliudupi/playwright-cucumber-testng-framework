package com.framework.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public record BillPayData(
        String testCaseId,
        String payeeName,
        Address address,
        String phone,
        String accountNumber,
        double amount,
        String fromAccount,
        String description

) {
    private static final Logger LOG = LoggerFactory.getLogger(TransferData.class);

    public static BillPayData fromMap(Map<String, String> data) {
        String testCaseId = data.getOrDefault("TestCaseID", "");
        String rawAmount = data.getOrDefault("Amount", "0.0").trim();

        double amt = 0.0;

        try {
            amt = Double.parseDouble(rawAmount);
            if (amt <= 0.0) {
                LOG.warn("Test Data Warning [TestCaseID: '{}']: Amount is non-positive: '{}'", testCaseId, amt);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Test Data Warning [TestCaseID: '{}']: Invalid numeric value for Amount: '{}'", testCaseId, rawAmount);
        }

        return new BillPayData(
                testCaseId,
                data.getOrDefault("PayeeName", ""),
                Address.fromMapWithPrefix(data, ""),
                data.getOrDefault("Phone", ""),
                data.getOrDefault("AccountNumber", ""),
                amt,
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("Description", "")
        );
    }
}