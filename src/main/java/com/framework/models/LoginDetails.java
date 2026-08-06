package com.framework.models;

import java.util.Map;

public record LoginDetails(
        String username,
        String password
) {
    public static LoginDetails fromMap(Map<String, String> data) {
        return new LoginDetails(
                data.getOrDefault("Username", ""),
                data.getOrDefault("Password", "")
        );
    }
}