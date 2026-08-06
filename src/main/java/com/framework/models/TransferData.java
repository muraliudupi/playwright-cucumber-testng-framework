package com.framework.models;

import java.util.Map;

public record TransferData(
        String testCaseId,
        LoginDetails details,
        String fromAccount,
        String toAccount,
        double amount,
        String description
) {
    public static TransferData fromMap(Map<String, String> data) {
        double amt = 0.0;
        try {
            amt = Double.parseDouble(data.getOrDefault("Amount", "0.0"));
        } catch (NumberFormatException ignored) {}

        return new TransferData(
                data.getOrDefault("TestCaseID", ""),
                LoginDetails.fromMap(data),
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("ToAccount", ""),
                amt,
                data.getOrDefault("Description", "")
        );
    }
}
