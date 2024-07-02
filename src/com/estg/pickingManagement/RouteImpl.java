package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.exceptions.AidBoxException;
import com.estg.pickingManagement.exceptions.RouteException;

public class RouteImpl implements Route {
    private AidBox[] aidBoxes;
    private Vehicle vehicle;

    public RouteImpl(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.aidBoxes = new AidBox[0];
    }

    @Override
    public void addAidBox(AidBox aidBox) throws RouteException {
        if (aidBox == null) {
            throw new RouteException("AidBox cannot be null");
        }
        if (containsAidBox(aidBox)) {
            throw new RouteException("AidBox already in route");
        }
        if (!isCompatible(aidBox)) {
            throw new RouteException("AidBox not compatible with vehicle");
        }
        AidBox[] newAidBoxes = new AidBox[aidBoxes.length + 1];
        for (int i = 0; i < aidBoxes.length; i++) {
            newAidBoxes[i] = aidBoxes[i];
        }
        newAidBoxes[aidBoxes.length] = aidBox;
        aidBoxes = newAidBoxes;
    }

    @Override
    public AidBox removeAidBox(AidBox aidBox) throws RouteException {
        if (aidBox == null || !containsAidBox(aidBox)) {
            throw new RouteException("AidBox not in route");
        }
        AidBox[] newAidBoxes = new AidBox[aidBoxes.length - 1];
        int index = 0;
        AidBox removedAidBox = null;
        for (int i = 0; i < aidBoxes.length; i++) {
            if (aidBoxes[i].equals(aidBox)) {
                removedAidBox = aidBoxes[i];
                continue;
            }
            newAidBoxes[index++] = aidBoxes[i];
        }
        aidBoxes = newAidBoxes;
        return removedAidBox;
    }

    @Override
    public boolean containsAidBox(AidBox aidBox) {
        for (AidBox box : aidBoxes) {
            if (box.equals(aidBox)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void replaceAidBox(AidBox from, AidBox to) throws RouteException {
        if (from == null || to == null) {
            throw new RouteException("AidBox cannot be null");
        }
        if (!containsAidBox(from)) {
            throw new RouteException("AidBox to replace not in route");
        }
        if (containsAidBox(to)) {
            throw new RouteException("AidBox to insert already in route");
        }
        if (!isCompatible(to)) {
            throw new RouteException("AidBox to insert not compatible with vehicle");
        }
        for (int i = 0; i < aidBoxes.length; i++) {
            if (aidBoxes[i].equals(from)) {
                aidBoxes[i] = to;
                break;
            }
        }
    }

    @Override
    public void insertAfter(AidBox after, AidBox toInsert) throws RouteException {
        if (after == null || toInsert == null) {
            throw new RouteException("AidBox cannot be null");
        }
        if (!containsAidBox(after)) {
            throw new RouteException("AidBox to insert after not in route");
        }
        if (containsAidBox(toInsert)) {
            throw new RouteException("AidBox to insert already in route");
        }
        if (!isCompatible(toInsert)) {
            throw new RouteException("AidBox to insert not compatible with vehicle");
        }

        AidBox[] newAidBoxes = new AidBox[aidBoxes.length + 1];
        int index = 0;
        for (int i = 0; i < aidBoxes.length; i++) {
            newAidBoxes[index++] = aidBoxes[i];
            if (aidBoxes[i].equals(after)) {
                newAidBoxes[index++] = toInsert;
            }
        }
        aidBoxes = newAidBoxes;
    }

    @Override
    public AidBox[] getRoute() {
        AidBox[] copy = new AidBox[aidBoxes.length];
        for (int i = 0; i < aidBoxes.length; i++) {
            copy[i] = aidBoxes[i];
        }
        return copy;
    }

    @Override
    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public double getTotalDistance() {
        // This method should calculate the total distance of the route.
        // Placeholder implementation:
        return 0.0;
    }

    @Override
    public double getTotalDuration() {
        // This method should calculate the total duration of the route.
        // Placeholder implementation:
        return 0.0;
    }

    @Override
    public Report getReport() {
        // This method should generate and return a report for the route.
        // Placeholder implementation:
        return null;
    }

    private boolean isCompatible(AidBox aidBox) {
        // Implement logic to check if aidBox is compatible with the vehicle
        return true; // Placeholder implementation
    }
}
