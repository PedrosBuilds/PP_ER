package com.estg.pickingManagement;

import java.time.LocalDateTime;

public class ReportImpl implements Report {
    private int usedVehicles;
    private int pickedContainers;
    private double totalDistance;
    private double totalDuration;
    private int nonPickedContainers;
    private int notUsedVehicles;
    private LocalDateTime date;

    public ReportImpl(int usedVehicles, int pickedContainers, double totalDistance, double totalDuration,
                      int nonPickedContainers, int notUsedVehicles, LocalDateTime date) {
        this.usedVehicles = usedVehicles;
        this.pickedContainers = pickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.nonPickedContainers = nonPickedContainers;
        this.notUsedVehicles = notUsedVehicles;
        this.date = date;
    }

    @Override
    public int getUsedVehicles() {
        return usedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return pickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return totalDuration;
    }

    @Override
    public int getNonPickedContainers() {
        return nonPickedContainers;
    }

    @Override
    public int getNotUsedVehicles() {
        return notUsedVehicles;
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }
}
