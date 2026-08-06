package com.framework.models;

import java.util.Map;

public record RegisterData(
        String testCaseId,
        String firstName,
        String lastName,
        Address address,
        String phone,
        String ssn,
        LoginDetails loginDetails,
        String description
) {
    public static RegisterData fromMap(Map<String, String> data) {

        return new RegisterData(
                data.getOrDefault("TestCaseID", ""),
                data.getOrDefault("FirstName",""),
                data.getOrDefault("LastName",""),
                Address.fromMapWithPrefix(data, ""),
                data.getOrDefault("Phone", ""),
                data.getOrDefault("SSN", ""),
                LoginDetails.fromMap(data),
                data.getOrDefault("Description", "")
        );
    }
}