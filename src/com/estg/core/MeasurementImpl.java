package com.estg.core;

import java.time.LocalDateTime;

public class MeasurementImpl implements Measurement {
    private LocalDateTime date;
    private double value;

    public MeasurementImpl(LocalDateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public double getValue() {
        return value;
    }
}
