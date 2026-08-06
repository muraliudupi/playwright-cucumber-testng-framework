package com.framework.models;

import java.util.Map;

public record PaymentDetails(
        String fullName,
        String cardNumber,
        String expirationDate,
        String securityCode
) {
    public static PaymentDetails fromMap(Map<String, String> data) {
        return new PaymentDetails(
                data.getOrDefault("FullName", ""),
                data.getOrDefault("CardNumber", ""),
                data.getOrDefault("ExpirationDate", ""),
                data.getOrDefault("SecurityCode", "")
        );
    }
}