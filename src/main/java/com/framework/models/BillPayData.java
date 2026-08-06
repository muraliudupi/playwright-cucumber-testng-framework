package com.framework.models;

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
    public static BillPayData fromMap(Map<String, String> data) {
        double amt = 0.0;
        try {
            amt = Double.parseDouble(data.getOrDefault("Amount", "0.0"));
        } catch (NumberFormatException ignored) {
        }

        return new BillPayData(
                data.getOrDefault("TestCaseID", ""),
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