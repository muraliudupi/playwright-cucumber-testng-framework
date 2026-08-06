package com.framework.models;

import java.util.Map;

public record MobileCheckoutGuestData(
        String testCaseId,
        OrderItem item,
        LoginDetails details,
        String fullName,
        Address shippingAddress,
        PaymentDetails paymentDetails,
        String billFullName,
        Address billingAddress,
        String description
)
        implements CheckoutDetails {
    public static MobileCheckoutGuestData fromMap(Map<String, String> data) {
        return new MobileCheckoutGuestData(
                data.getOrDefault("TestCaseID", ""),
                OrderItem.fromMap(data),
                LoginDetails.fromMap(data),
                data.getOrDefault("FullName",""),
                Address.fromMapWithPrefix(data, ""),
                PaymentDetails.fromMap(data),
                data.getOrDefault("BillFullName",""),
                Address.fromMapWithPrefix(data, "Bill"),
                data.getOrDefault("Description", "")
        );
    }
}