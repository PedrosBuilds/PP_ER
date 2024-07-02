package com.estg.core;

import com.estg.core.exceptions.*;
import com.estg.pickingManagement.*;

import java.time.LocalDateTime;

public class InstitutionImpl implements Institution {
    private String name;
    private AidBox[] aidBoxes;
    private Vehicle[] vehicles;
    private Container[] containers;
    private PickingMap[] pickingMaps;
    private int aidBoxCount;
    private int vehicleCount;
    private int containerCount;
    private int pickingMapCount;

    public InstitutionImpl(String name) {
        this.name = name;
        this.aidBoxes = new AidBox[10];
        this.vehicles = new Vehicle[10];
        this.containers = new Container[10];
        this.pickingMaps = new PickingMap[10];
        this.aidBoxCount = 0;
        this.vehicleCount = 0;
        this.containerCount = 0;
        this.pickingMapCount = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AidBox[] getAidBoxes() {
        AidBox[] activeAidBoxes = new AidBox[aidBoxCount];
        System.arraycopy(aidBoxes, 0, activeAidBoxes, 0, aidBoxCount);
        return activeAidBoxes;
    }

    @Override
    public Container[] getContainers() {
        Container[] activeContainers = new Container[containerCount];
        System.arraycopy(containers, 0, activeContainers, 0, containerCount);
        return activeContainers;
    }

    @Override
    public Vehicle[] getVehicles() {
        Vehicle[] activeVehicles = new Vehicle[vehicleCount];
        System.arraycopy(vehicles, 0, activeVehicles, 0, vehicleCount);
        return activeVehicles;
    }

    @Override
    public boolean addAidBox(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox cannot be null");
        }
        if (aidBoxCount == aidBoxes.length) {
            AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
            System.arraycopy(aidBoxes, 0, newAidBoxes, 0, aidBoxes.length);
            aidBoxes = newAidBoxes;
        }
        aidBoxes[aidBoxCount++] = aidBox;
        return true;
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle cannot be null");
        }
        if (vehicleCount == vehicles.length) {
            Vehicle[] newVehicles = new Vehicle[vehicles.length * 2];
            System.arraycopy(vehicles, 0, newVehicles, 0, vehicles.length);
            vehicles = newVehicles;
        }
        vehicles[vehicleCount++] = vehicle;
        return true;
    }

    @Override
    public boolean addContainer(Container container) {
        if (containerCount == containers.length) {
            Container[] newContainers = new Container[containers.length * 2];
            System.arraycopy(containers, 0, newContainers, 0, containers.length);
            containers = newContainers;
        }
        containers[containerCount++] = container;
        return true;
    }

    @Override
    public boolean addPickingMap(PickingMap pickingMap) throws PickingMapException {
        if (pickingMap == null) {
            throw new PickingMapException("PickingMap cannot be null");
        }
        if (pickingMapCount == pickingMaps.length) {
            PickingMap[] newPickingMaps = new PickingMap[pickingMaps.length * 2];
            System.arraycopy(pickingMaps, 0, newPickingMaps, 0, pickingMaps.length);
            pickingMaps = newPickingMaps;
        }
        pickingMaps[pickingMapCount++] = pickingMap;
        return true;
    }

    @Override
    public double getDistance(AidBox aidBox) throws AidBoxException {
        // Implementação da lógica para calcular a distância, se necessário
        return 0;
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (pickingMapCount == 0) {
            throw new PickingMapException("No PickingMaps available");
        }
        return pickingMaps[pickingMapCount - 1];
    }

    @Override
    public PickingMap[] getPickingMaps() {
        PickingMap[] activePickingMaps = new PickingMap[pickingMapCount];
        System.arraycopy(pickingMaps, 0, activePickingMaps, 0, pickingMapCount);
        return activePickingMaps;
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to) {
        PickingMap[] filteredMaps = new PickingMap[pickingMapCount];
        int count = 0;
        for (int i = 0; i < pickingMapCount; i++) {
            if (!pickingMaps[i].getDate().isBefore(from) && !pickingMaps[i].getDate().isAfter(to)) {
                filteredMaps[count++] = pickingMaps[i];
            }
        }
        PickingMap[] result = new PickingMap[count];
        System.arraycopy(filteredMaps, 0, result, 0, count);
        return result;
    }

    @Override
    public Container getContainer(AidBox aidBox, ContainerType containerType) throws ContainerException {
        // Implementação para retornar um container específico
        return null;
    }

    @Override
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        // Implementação para desabilitar um veículo
    }

    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        // Implementação para habilitar um veículo
    }

    @Override
    public boolean addMeasurement(Measurement measurement, Container container) throws ContainerException, MeasurementException {
        // Implementação para adicionar uma medição a um container específico
        return false;
    }
}
