package com.framework.models;

import java.util.Map;

public record MobileLoginData(
        String testCaseId,
        LoginDetails details,
        String description
) {
    public static MobileLoginData fromMap(Map<String, String> data) {

        return new MobileLoginData(
                data.getOrDefault("TestCaseID", ""),
                LoginDetails.fromMap(data),
                data.getOrDefault("Description", "")
        );
    }
}
