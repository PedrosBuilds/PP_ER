package com.estg.core;

import com.estg.core.exceptions.MeasurementException;

import java.time.LocalDate;

public class ContainerImpl implements Container {
    private String code;
    private double capacity;
    private ContainerType type;
    private Measurement[] measurements;
    private int measurementCount;

    public ContainerImpl(String code, double capacity, ContainerType type) {
        this.code = code;
        this.capacity = capacity;
        this.type = type;
        this.measurements = new Measurement[10]; // Capacidade inicial
        this.measurementCount = 0;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public ContainerType getType() {
        return type;
    }

    @Override
    public Measurement[] getMeasurements() {
        Measurement[] activeMeasurements = new Measurement[measurementCount];
        System.arraycopy(measurements, 0, activeMeasurements, 0, measurementCount);
        return activeMeasurements;
    }

    @Override
    public Measurement[] getMeasurements(LocalDate date) {
        int count = 0;
        for (int i = 0; i < measurementCount; i++) {
            if (measurements[i].getDate().toLocalDate().equals(date)) {
                count++;
            }
        }
        Measurement[] filteredMeasurements = new Measurement[count];
        int index = 0;
        for (int i = 0; i < measurementCount; i++) {
            if (measurements[i].getDate().toLocalDate().equals(date)) {
                filteredMeasurements[index++] = measurements[i];
            }
        }
        return filteredMeasurements;
    }

    @Override
    public boolean addMeasurement(Measurement measurement) throws MeasurementException {
        if (measurementCount == measurements.length) {
            Measurement[] newMeasurements = new Measurement[measurements.length * 2];
            System.arraycopy(measurements, 0, newMeasurements, 0, measurements.length);
            measurements = newMeasurements;
        }
        measurements[measurementCount++] = measurement;
        return true;
    }
}
