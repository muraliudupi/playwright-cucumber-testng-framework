package com.framework.models;

import java.util.Map;

public record MobileCartData(
        String testCaseId,
        OrderItem item,
        String description
) {
    public static MobileCartData fromMap(Map<String, String> data) {
        return new MobileCartData(
                data.getOrDefault("TestCaseID", ""),
                OrderItem.fromMap(data),
                data.getOrDefault("Description", "")
        );
    }
}