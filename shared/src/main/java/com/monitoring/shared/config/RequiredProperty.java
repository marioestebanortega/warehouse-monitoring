package com.monitoring.shared.config;

import com.monitoring.shared.exception.ConfigurationException;
import com.monitoring.shared.model.SensorType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RequiredProperty {

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    private RequiredProperty() {
    }

    public static String text(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new ConfigurationException("Missing property '" + name + "'");
        }
        return value.strip();
    }

    public static double number(String name, String value) {
        return parse(name, value, "number", text -> {
            double number = Double.parseDouble(text);
            if (!Double.isFinite(number)) {
                throw new NumberFormatException(text);
            }
            return number;
        });
    }

    public static int port(String name, String value) {
        return parse(name, value, "port", text -> {
            int port = Integer.parseInt(text);
            if (port < MIN_PORT || port > MAX_PORT) {
                throw new NumberFormatException(text);
            }
            return port;
        });
    }

    public static <T> Map<SensorType, T> bySensorType(String prefix, Map<String, String> values,
                                                      BiFunction<String, String, T> reader) {
        Map<String, String> configured = values == null ? Map.of() : values;
        Map<SensorType, T> result = new EnumMap<>(SensorType.class);
        for (SensorType type : SensorType.values()) {
            String key = type.propertyKey();
            result.put(type, reader.apply(prefix + "." + key, configured.get(key)));
        }
        return result;
    }

    private static <T> T parse(String name, String value, String kind, Function<String, T> parser) {
        String text = text(name, value);
        try {
            return parser.apply(text);
        } catch (NumberFormatException exception) {
            throw new ConfigurationException("Invalid " + kind + " '" + text + "' in property '" + name + "'");
        }
    }
}
