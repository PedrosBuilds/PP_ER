package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.VehicleImpl;

import java.time.LocalDateTime;

public class InstitutionImpl implements Institution {
    private String name;
    private AidBox[] aidBoxes;
    private int aidBoxCount;
    private Container[] containers;
    private int containerCount;
    private Vehicle[] vehicles;
    private int vehicleCount;
    private PickingMap[] pickingMaps;
    private int pickingMapCount;
    private String[][] distanceKeys;
    private double[] distances;
    private double[] durations;
    private int distanceCount;

    public InstitutionImpl(String name) {
        this.name = name;
        this.aidBoxes = new AidBox[10]; // Capacidade inicial
        this.containers = new Container[10]; // Capacidade inicial
        this.vehicles = new Vehicle[10]; // Capacidade inicial
        this.pickingMaps = new PickingMap[10]; // Capacidade inicial
        this.distanceKeys = new String[10][2]; // Capacidade inicial
        this.distances = new double[10];
        this.durations = new double[10];
        this.aidBoxCount = 0;
        this.containerCount = 0;
        this.vehicleCount = 0;
        this.pickingMapCount = 0;
        this.distanceCount = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addAidBox(AidBox aidBox) throws AidBoxException {
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
        for (AidBox ab : aidBoxes) {
            if (ab != null) {
                try {
                    Container foundContainer = ab.getContainer(container.getType());
                    if (foundContainer != null && foundContainer.equals(container)) {
                        return container.addMeasurement(measurement);
                    }
                } catch (Exception e) {
                    // Container não encontrado
                }
            }
        }
        throw new ContainerException("Container não encontrado em nenhuma AidBox");
    }

    @Override
    public AidBox[] getAidBoxes() {
        AidBox[] activeAidBoxes = new AidBox[aidBoxCount];
        System.arraycopy(aidBoxes, 0, activeAidBoxes, 0, aidBoxCount);
        return activeAidBoxes;
    }

    @Override
    public Container getContainer(AidBox aidBox, ContainerType type) throws ContainerException {
        return aidBox.getContainer(type);
    }

    @Override
    public Vehicle[] getVehicles() {
        Vehicle[] activeVehicles = new Vehicle[vehicleCount];
        System.arraycopy(vehicles, 0, activeVehicles, 0, vehicleCount);
        return activeVehicles;
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle cannot be null");
        }
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].getCode().equals(vehicle.getCode())) {
                return false; // Vehicle already exists
            }
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
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle cannot be null");
        }
        for (Vehicle v : vehicles) {
            if (v != null && v.getCode().equals(vehicle.getCode())) {
                ((VehicleImpl) v).setEnabled(false);
                return;
            }
        }
        throw new VehicleException("Vehicle not found");
    }

    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle cannot be null");
        }
        for (Vehicle v : vehicles) {
            if (v != null && v.getCode().equals(vehicle.getCode())) {
                ((VehicleImpl) v).setEnabled(true);
                return;
            }
        }
        throw new VehicleException("Vehicle not found");
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
        for (PickingMap pm : pickingMaps) {
            if (pm != null && !pm.getDate().isBefore(from) && !pm.getDate().isAfter(to)) {
                filteredMaps[count++] = pm;
            }
        }
        PickingMap[] result = new PickingMap[count];
        System.arraycopy(filteredMaps, 0, result, 0, count);
        return result;
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (pickingMapCount == 0) {
            throw new PickingMapException("No picking maps available");
        }
        return pickingMaps[pickingMapCount - 1];
    }

    @Override
    public boolean addPickingMap(PickingMap pickingMap) throws PickingMapException {
        if (pickingMap == null) {
            throw new PickingMapException("Picking map cannot be null");
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
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Container[] getAllContainers() {
        Container[] activeContainers = new Container[containerCount];
        System.arraycopy(containers, 0, activeContainers, 0, containerCount);
        return activeContainers;
    }

    public void addContainer(Container container) throws ContainerException {
        if (containerCount == containers.length) {
            Container[] newContainers = new Container[containers.length * 2];
            System.arraycopy(containers, 0, newContainers, 0, containers.length);
            containers = newContainers;
        }
        containers[containerCount++] = container;
    }

    public void addContainerType(String type) {
        // Implementar lógica para adicionar tipos de container, se necessário
    }

    public void addDistance(String from, String to, double distance, double duration) {
        if (distanceCount == distanceKeys.length) {
            String[][] newDistanceKeys = new String[distanceKeys.length * 2][2];
            double[] newDistances = new double[distances.length * 2];
            double[] newDurations = new double[durations.length * 2];
            System.arraycopy(distanceKeys, 0, newDistanceKeys, 0, distanceKeys.length);
            System.arraycopy(distances, 0, newDistances, 0, distances.length);
            System.arraycopy(durations, 0, newDurations, 0, durations.length);
            distanceKeys = newDistanceKeys;
            distances = newDistances;
            durations = newDurations;
        }
        distanceKeys[distanceCount][0] = from;
        distanceKeys[distanceCount][1] = to;
        distances[distanceCount] = distance;
        durations[distanceCount] = duration;
        distanceCount++;
    }

    public Container findContainerByCode(String code) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getCode().equals(code)) {
                return containers[i];
            }
        }
        return null;
    }
}
