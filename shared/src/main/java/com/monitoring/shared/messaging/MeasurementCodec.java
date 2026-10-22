package com.monitoring.shared.messaging;

import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorReading;
import com.monitoring.shared.model.SensorType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public final class MeasurementCodec {

    private static final String FIELD_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String WAREHOUSE_ID = "warehouse_id";
    private static final String SENSOR_ID = "sensor_id";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final List<String> READING_FIELDS = List.of(SENSOR_ID, VALUE);
    private static final List<String> MESSAGE_FIELDS = List.of(WAREHOUSE_ID, SENSOR_ID, TYPE, VALUE);
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

    private MeasurementCodec() {
    }

    public static SensorReading decodeReading(String text) {
        Map<String, String> fields = parseFields(text, READING_FIELDS);
        return new SensorReading(fields.get(SENSOR_ID), parseNumber(VALUE, fields.get(VALUE)));
    }

    public static String encode(Measurement measurement) {
        return new StringJoiner(FIELD_SEPARATOR + " ")
                .add(WAREHOUSE_ID + KEY_VALUE_SEPARATOR + measurement.warehouseId())
                .add(SENSOR_ID + KEY_VALUE_SEPARATOR + measurement.sensorId())
                .add(TYPE + KEY_VALUE_SEPARATOR + measurement.type())
                .add(VALUE + KEY_VALUE_SEPARATOR + BigDecimal.valueOf(measurement.value()).toPlainString())
                .toString();
    }

    public static Measurement decode(String message) {
        Map<String, String> fields = parseFields(message, MESSAGE_FIELDS);
        return new Measurement(fields.get(WAREHOUSE_ID), fields.get(SENSOR_ID), parseType(fields.get(TYPE)),
                parseNumber(VALUE, fields.get(VALUE)));
    }

    private static Map<String, String> parseFields(String text, List<String> expectedKeys) {
        Map<String, String> fields = new HashMap<>();
        for (String field : text.strip().split(FIELD_SEPARATOR, -1)) {
            String[] keyValue = field.split(KEY_VALUE_SEPARATOR, -1);
            if (keyValue.length != 2 || keyValue[0].isBlank() || keyValue[1].isBlank()) {
                throw new InvalidMeasurementException("Malformed field '" + field.strip() + "'");
            }
            String key = keyValue[0].strip();
            if (!expectedKeys.contains(key)) {
                throw new InvalidMeasurementException("Unexpected field '" + key + "'");
            }
            if (fields.putIfAbsent(key, keyValue[1].strip()) != null) {
                throw new InvalidMeasurementException("Duplicated field '" + key + "'");
            }
        }
        for (String key : expectedKeys) {
            if (!fields.containsKey(key)) {
                throw new InvalidMeasurementException("Missing field '" + key + "'");
            }
        }
        return fields;
    }

    private static double parseNumber(String key, String text) {
        if (!NUMBER.matcher(text).matches()) {
            throw new InvalidMeasurementException("invalid number '" + text + "' in field '" + key + "'");
        }
        return Double.parseDouble(text);
    }

    private static SensorType parseType(String text) {
        try {
            return SensorType.valueOf(text);
        } catch (IllegalArgumentException exception) {
            throw new InvalidMeasurementException("unknown sensor type '" + text + "'");
        }
    }
}
