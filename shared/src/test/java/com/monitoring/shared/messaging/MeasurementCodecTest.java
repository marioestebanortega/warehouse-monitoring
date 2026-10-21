package com.monitoring.shared.messaging;

import com.monitoring.shared.exception.InvalidMeasurementException;
import com.monitoring.shared.model.SensorReading;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasurementCodecTest {

    @Test
    void decodesTemperatureReading() {
        assertEquals(new SensorReading("t1", 30), MeasurementCodec.decodeReading("sensor_id=t1; value=30"));
    }

    @Test
    void decodesHumidityReading() {
        assertEquals(new SensorReading("h1", 40), MeasurementCodec.decodeReading("sensor_id=h1; value=40"));
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
            "esto no vale",
            "sensor_id=; value=30",
            "sensor_id=t1; value=",
            "sensor_id=t1; value=30;",
            "sensor_id=t1; value=3=0",
            "sensor_id=t1; sensor_id=t2; value=30",
            "sensor_id=t1; value=30; unit=C",
            "sensor_id=t1; value=NaN",
            "sensor_id=t1; value=Infinity",
            "sensor_id=t1; value=30d",
            "sensor_id=t1; value=1e3"
    })
    void rejectsInvalidReading(String reading) {
        assertThrows(InvalidMeasurementException.class, () -> MeasurementCodec.decodeReading(reading));
    }
}
