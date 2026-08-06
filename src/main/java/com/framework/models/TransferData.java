package com.framework.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public record TransferData(
        String testCaseId,
        LoginDetails details,
        String fromAccount,
        String toAccount,
        double amount,
        String description
) {
    private static final Logger LOG = LoggerFactory.getLogger(TransferData.class);

    public static TransferData fromMap(Map<String, String> data) {
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

        return new TransferData(
                testCaseId,
                LoginDetails.fromMap(data),
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("ToAccount", ""),
                amt,
                data.getOrDefault("Description", "")
        );
    }
}
