package com.framework.models;

import java.util.Map;

public record Address(
        String address,
        String city,
        String state,
        String zip,
        String country
) {
    public static Address fromMapWithPrefix(Map<String, String> data, String prefix) {
        String addressKey = prefix.isEmpty() ? "Address" : prefix + "Address";
        return new Address(
                data.getOrDefault(addressKey, ""),
                data.getOrDefault(prefix.isEmpty() ? "City" : prefix + "City", ""),
                data.getOrDefault(prefix.isEmpty() ? "State" : prefix + "State", ""),
                data.getOrDefault(prefix.isEmpty() ? "Zip" : prefix + "Zip", ""),
                data.getOrDefault(prefix.isEmpty() ? "Country" : prefix + "Country", "")
        );
    }
}