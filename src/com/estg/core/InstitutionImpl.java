package com.estg.core;

import com.estg.core.exceptions.*;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InstitutionImpl implements Institution {
    private String name;
    private AidBox[] aidBoxes;
    private int aidBoxCount;
    private Container[] containers;
    private int containerCount;
    private List<Vehicle> vehicles;
    private List<String> containerTypes;
    private List<Distance> distances;

    public InstitutionImpl(String name) {
        this.name = name;
        this.aidBoxes = new AidBox[10];
        this.aidBoxCount = 0;
        this.containers = new Container[10];
        this.containerCount = 0;
        this.vehicles = new ArrayList<>();
        this.containerTypes = new ArrayList<>();
        this.distances = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addAidBox(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox cannot be null");
        }
        for (int i = 0; i < aidBoxCount; i++) {
            if (aidBoxes[i].getCode().equals(aidBox.getCode())) {
                return false;
            }
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
    public boolean addMeasurement(Measurement measurement, Container container) throws ContainerException, MeasurementException {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].equals(container)) {
                containers[i].addMeasurement(measurement);
                return true;
            }
        }
        return false;
    }

    @Override
    public AidBox[] getAidBoxes() {
        AidBox[] activeAidBoxes = new AidBox[aidBoxCount];
        System.arraycopy(aidBoxes, 0, activeAidBoxes, 0, aidBoxCount);
        return activeAidBoxes;
    }

    @Override
    public Container getContainer(AidBox aidBox, ContainerType type) throws ContainerException {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getType().equals(type)) {
                return containers[i];
            }
        }
        throw new ContainerException("Container not found");
    }

    @Override
    public Vehicle[] getVehicles() {
        return vehicles.toArray(new Vehicle[0]);
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle cannot be null");
        }
        for (Vehicle v : vehicles) {
            if (v.getCode().equals(vehicle.getCode())) {
                return false;
            }
        }
        vehicles.add(vehicle);
        return true;
    }

    @Override
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null || !vehicles.contains(vehicle)) {
            throw new VehicleException("Vehicle not found");
        }
        // Implementação do método de desativar o veículo
    }

    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null || !vehicles.contains(vehicle)) {
            throw new VehicleException("Vehicle not found");
        }
        // Implementação do método de ativar o veículo
    }

    @Override
    public PickingMap[] getPickingMaps() {
        // Implementar o método
        return new PickingMap[0];
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to) {
        // Implementar o método
        return new PickingMap[0];
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        // Implementar o método
        return null;
    }

    @Override
    public boolean addPickingMap(PickingMap pickingMap) throws PickingMapException {
        // Implementar o método
        return false;
    }

    @Override
    public double getDistance(AidBox aidBox) throws AidBoxException {
        // Implementar o método para calcular a distância
        return 0;
    }

    public void addContainer(Container container) {
        if (containerCount == containers.length) {
            Container[] newContainers = new Container[containers.length * 2];
            System.arraycopy(containers, 0, newContainers, 0, containers.length);
            containers = newContainers;
        }
        containers[containerCount++] = container;
    }

    public Container findContainerByCode(String code) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getCode().equals(code)) {
                return containers[i];
            }
        }
        return null;
    }

    public void addContainerType(String type) {
        containerTypes.add(type);
    }

    public void addDistance(String from, String to, double distance, double duration) {
        distances.add(new Distance(from, to, distance, duration));
    }

    public String[] getContainerTypes() {
        return containerTypes.toArray(new String[0]);
    }

    public Distance[] getDistances() {
        return distances.toArray(new Distance[0]);
    }

    public Container[] getAllContainers() {
        Container[] activeContainers = new Container[containerCount];
        System.arraycopy(containers, 0, activeContainers, 0, containerCount);
        return activeContainers;
    }

    public static class Distance {
        private String from;
        private String to;
        private double distance;
        private double duration;

        public Distance(String from, String to, double distance, double duration) {
            this.from = from;
            this.to = to;
            this.distance = distance;
            this.duration = duration;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }
}
