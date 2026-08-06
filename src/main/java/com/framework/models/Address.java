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
        return new Address(
                data.getOrDefault(prefix + "Address", data.getOrDefault("Address", "")),
                data.getOrDefault(prefix + "City", data.getOrDefault("City", "")),
                data.getOrDefault(prefix + "State", data.getOrDefault("State", "")),
                data.getOrDefault(prefix + "Zip", data.getOrDefault("Zip", "")),
                data.getOrDefault(prefix + "Country", data.getOrDefault("Country", ""))
        );
    }
}