package com.framework.context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private final Map<String, Object> contextStorage = new HashMap<>();

    public void setContext(String key, Object value) {
        contextStorage.put(key, value);
    }

    public Object getContext(String key) {
        return contextStorage.get(key);
    }

    public String getStringContext(String key) {
        Object val = contextStorage.get(key);
        return val != null ? val.toString() : "";
    }

    public boolean contains(String key) {
        return contextStorage.containsKey(key);
    }
}