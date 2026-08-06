package com.framework.models;

import java.util.Map;

public record UpdateContactData (
        String testCaseId,
        String firstName,
        String lastName,
        Address address,
        String phone,
        String description
) {
    public static UpdateContactData fromMap(Map<String, String> data) {

        return new UpdateContactData(
                data.getOrDefault("TestCaseID", ""),
                data.getOrDefault("FirstName",""),
                data.getOrDefault("LastName",""),
                Address.fromMapWithPrefix(data, ""),
                data.getOrDefault("Phone", ""),
                data.getOrDefault("Description", "")
        );
    }
}