package com.monitoring.shared.config;

import com.monitoring.shared.exception.ConfigurationException;

public final class RequiredProperty {

    private RequiredProperty() {
    }

    public static double number(String name, String value) {
        String text = text(name, value);
        try {
            double number = Double.parseDouble(text);
            if (!Double.isFinite(number)) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new ConfigurationException("Invalid number '" + text + "' in property '" + name + "'");
        }
    }

    public static String text(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new ConfigurationException("Missing property '" + name + "'");
        }
        return value.strip();
    }
}
