package com.framework.models;

import java.util.Map;

public record MobileCheckoutData(
        String testCaseId,
        OrderItem item,
        String fullName,
        Address shippingAddress,
        PaymentDetails paymentDetails,
        String billFullName,
        Address billingAddress,
        String description
)
        implements CheckoutDetails {
    public static MobileCheckoutData fromMap(Map<String, String> data) {
        return new MobileCheckoutData(
                data.getOrDefault("TestCaseID", ""),
                OrderItem.fromMap(data),
                data.getOrDefault("FullName",""),
                Address.fromMapWithPrefix(data, ""),
                PaymentDetails.fromMap(data),
                data.getOrDefault("BillFullName",""),
                Address.fromMapWithPrefix(data, "Bill"),
                data.getOrDefault("Description", "")
        );
    }
}