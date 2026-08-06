package com.framework.models;

public interface CheckoutDetails {
    String fullName();
    Address shippingAddress();
    String billFullName();
    PaymentDetails paymentDetails();
}