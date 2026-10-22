package com.monitoring.shared.messaging;

import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.model.Measurement;
import com.monitoring.shared.model.SensorReading;
import com.monitoring.shared.model.SensorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasurementCodecTest {

    @Test
    void decodesReading() {
        assertEquals(new SensorReading("t1", 30), MeasurementCodec.decodeReading("sensor_id=t1; value=30"));
    }

    @Test
    void toleratesExtraSpacesAndTrailingNewline() {
        assertEquals(new SensorReading("t1", 30), MeasurementCodec.decodeReading("  sensor_id = t1 ;value=30 \n"));
    }

    @Test
    void decodesDecimalValue() {
        assertEquals(new SensorReading("t1", 30.5), MeasurementCodec.decodeReading("sensor_id=t1; value=30.5"));
    }

    @Test
    void rejectsReadingWithoutValue() {
        InvalidMeasurementException exception = assertThrows(InvalidMeasurementException.class,
                () -> MeasurementCodec.decodeReading("sensor_id=t1"));
        assertEquals("Missing field 'value'", exception.getMessage());
    }

    @Test
    void rejectsReadingWithoutSensorId() {
        InvalidMeasurementException exception = assertThrows(InvalidMeasurementException.class,
                () -> MeasurementCodec.decodeReading("value=30"));
        assertEquals("Missing field 'sensor_id'", exception.getMessage());
    }

    @Test
    void rejectsNonNumericValue() {
        InvalidMeasurementException exception = assertThrows(InvalidMeasurementException.class,
                () -> MeasurementCodec.decodeReading("sensor_id=t1; value=hot"));
        assertEquals("invalid number 'hot' in field 'value'", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "sensor_id=; value=30",
            "sensor_id=t1; value=30;",
            "sensor_id=t1; sensor_id=t2; value=30",
            "sensor_id=t1; value=30; unit=C"
    })
    void rejectsInvalidReading(String reading) {
        assertThrows(InvalidMeasurementException.class, () -> MeasurementCodec.decodeReading(reading));
    }

    @Test
    void encodesMeasurementAsMessage() {
        Measurement measurement = new Measurement("warehouse-1", "t1", SensorType.TEMPERATURE, 30);

        assertEquals("warehouse_id=warehouse-1; sensor_id=t1; type=TEMPERATURE; value=30.0",
                MeasurementCodec.encode(measurement));
    }

    @Test
    void encodesLargeValueWithoutScientificNotation() {
        Measurement measurement = new Measurement("warehouse-1", "h1", SensorType.HUMIDITY, 12345678);

        assertEquals("warehouse_id=warehouse-1; sensor_id=h1; type=HUMIDITY; value=12345678",
                MeasurementCodec.encode(measurement));
    }

    @Test
    void decodesMessageAsMeasurement() {
        assertEquals(new Measurement("warehouse-2", "h1", SensorType.HUMIDITY, 55.5),
                MeasurementCodec.decode("warehouse_id=warehouse-2; sensor_id=h1; type=HUMIDITY; value=55.5"));
    }

    @Test
    void rejectsMessageWithUnknownType() {
        InvalidMeasurementException exception = assertThrows(InvalidMeasurementException.class,
                () -> MeasurementCodec.decode("warehouse_id=w1; sensor_id=p1; type=PRESSURE; value=1"));
        assertEquals("unknown sensor type 'PRESSURE'", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sensor_id=t1; value=30",
            "warehouse_id=w1; sensor_id=t1; type=TEMPERATURE"
    })
    void rejectsInvalidMessage(String message) {
        assertThrows(InvalidMeasurementException.class, () -> MeasurementCodec.decode(message));
    }
}
