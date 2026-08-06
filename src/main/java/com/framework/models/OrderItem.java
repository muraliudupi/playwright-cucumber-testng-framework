package com.framework.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public record OrderItem(
        String productLabel,
        int quantity
) {
    private static final Logger LOG = LoggerFactory.getLogger(TransferData.class);

    public static OrderItem fromMap(Map<String, String> data) {
        String qtyStr = data.getOrDefault("Quantity", "0").trim();

        int qty = 0;

        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                LOG.warn("Quantity is non-positive: '{}'", qty);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Invalid numeric value for Quantity: '{}'", qtyStr);
        }

        return new OrderItem(
                data.getOrDefault("ProductLabel", ""),
                qty
        );
    }
}