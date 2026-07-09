package com.framework.context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private final Map<String, Object> contextStorage = new HashMap<>();

    public void setContext(String key, Object value) {
        contextStorage.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(String key, Class<T> type) {
        Object val = contextStorage.get(key);
        if (val == null) return null;
        if (!type.isInstance(val)) {
            throw new IllegalStateException(String.format(
                    "Context key '%s' holds a %s, not the requested %s.",
                    key, val.getClass().getSimpleName(), type.getSimpleName()));
        }
        return (T) val;
    }

    public String getStringContext(String key) {
        Object val = contextStorage.get(key);
        return val != null ? val.toString() : "";
    }

    public boolean contains(String key) {
        return contextStorage.containsKey(key);
    }
}