package com.framework.models;

import java.util.Map;

public record AccountOpen(
        String testCaseId,
        LoginDetails details,
        String fromAccount,
        String description
) {
    public static AccountOpen fromMap(Map<String, String> data) {
        return new AccountOpen(
                data.getOrDefault("TestCaseID", ""),
                LoginDetails.fromMap(data),
                data.getOrDefault("FromAccount", ""),
                data.getOrDefault("Description", "")
        );
    }
}
