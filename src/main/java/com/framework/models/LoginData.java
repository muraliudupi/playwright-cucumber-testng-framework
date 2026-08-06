package com.framework.models;

import java.util.Map;

public record LoginData(
        String testCaseId,
        LoginDetails details,
        String description
) {
    public static LoginData fromMap(Map<String, String> data) {

        return new LoginData(
                data.getOrDefault("TestCaseID", ""),
                LoginDetails.fromMap(data),
                data.getOrDefault("Description", "")
        );
    }
}
