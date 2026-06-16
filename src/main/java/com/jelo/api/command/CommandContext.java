package com.jelo.api.command;

import com.jelo.api.command.argument.Argument;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
    private final Map<String, Object> parsedData = new HashMap<>();

    public void set(String id, Object value) {
        parsedData.put(id, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Argument<T> argument) {
        return (T) parsedData.get(argument.getId());
    }
}