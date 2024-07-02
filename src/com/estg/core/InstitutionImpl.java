package com.estg.core;

import com.estg.core.exceptions.InstitutionException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;

import java.time.LocalDateTime;

public class InstitutionImpl implements Institution {
    private String name;
    private AidBox[] aidBoxes;
    private int aidBoxCount;
    private Vehicle[] vehicles;
    private int vehicleCount;
    private Container[] containers;
    private int containerCount;
    private PickingMap currentPickingMap;

    public InstitutionImpl(String name) {
        this.name = name;
        this.aidBoxes = new AidBox[10];
        this.aidBoxCount = 0;
        this.vehicles = new Vehicle[10];
        this.vehicleCount = 0;
        this.containers = new Container[10];
        this.containerCount = 0;
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
    public boolean addAidBox(AidBox aidBox) {
        if (aidBoxCount == aidBoxes.length) {
            AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
            System.arraycopy(aidBoxes, 0, newAidBoxes, 0, aidBoxes.length);
            aidBoxes = newAidBoxes;
        }
        aidBoxes[aidBoxCount++] = aidBox;
        return true;
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) {
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
    public boolean addPickingMap(PickingMap pickingMap) {
        this.currentPickingMap = pickingMap;
        return true;
    }

    @Override
    public double getDistance(AidBox aidBox) {
        // Implementação da lógica para calcular a distância, se necessário
        return 0;
    }

    @Override
    public PickingMap getCurrentPickingMap() {
        return currentPickingMap;
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime start, LocalDateTime end) {
        // Implementação da lógica para retornar os picking maps entre as datas fornecidas
        return new PickingMap[0];
    }

    @Override
    public PickingMap[] getPickingMaps() {
        // Implementação da lógica para retornar todos os picking maps
        return new PickingMap[0];
    }
}
