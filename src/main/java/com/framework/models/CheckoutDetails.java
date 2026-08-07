package com.framework.models;

public interface CheckoutDetails {
    String fullName();
    Address shippingAddress();
    String billFullName();
    Address billingAddress();
    PaymentDetails paymentDetails();
}