# Warehouse Monitoring

Monitors temperature and humidity in warehouses and raises an alarm when values go above the allowed limit.

## Initial design

```
temperature sensor --+
                     +--> warehouse --> central service --> alarm
humidity sensor -----+
```

- Each warehouse has temperature and humidity sensors that send their readings.
- The warehouse collects the readings and sends them to the central service.
- The central service checks each reading against its limit and shows an alarm if it is exceeded.
- There can be more than one warehouse.

## Limits

| Sensor      | Limit |
|-------------|------:|
| Temperature | 35 °C |
| Humidity    | 50 %  |

An alarm is raised only when the value is above the limit.

## User stories

| #  | Story                                     | Issue |
|----|-------------------------------------------|-------|
| 00 | Project setup                             | #1    |
| 01 | Read a sensor message                     | #2    |
| 02 | Limits per sensor type                    | #3    |
| 03 | Raise an alarm                            | #4    |
| 04 | Send readings to the central service      | #5    |
| 05 | Receive readings from sensors             | #6    |
| 06 | Connect warehouses and central service    | #7    |
| 07 | Testing and prove all                   | #8    |
