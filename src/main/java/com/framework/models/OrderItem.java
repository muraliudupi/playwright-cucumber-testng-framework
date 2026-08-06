package com.framework.models;

import java.util.Map;

public record OrderItem(
        String productLabel,
        int quantity
) {
    public static OrderItem fromMap(Map<String, String> data) {
        String qtyStr = data.getOrDefault("Quantity", "1");
        int qty = 1;
        try {
            qty = Integer.parseInt(qtyStr.trim());
        } catch (NumberFormatException ignored) {}

        return new OrderItem(
                data.getOrDefault("ProductLabel", ""),
                qty
        );
    }
}