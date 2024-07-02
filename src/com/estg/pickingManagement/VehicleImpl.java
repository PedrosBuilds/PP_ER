package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.ContainerTypeImpl;

public class VehicleImpl implements Vehicle {
    private String code;
    private String[] types;
    private int[] capacities;

    public VehicleImpl(String code, String[] types, int[] capacities) {
        this.code = code;
        this.types = types;
        this.capacities = capacities;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public double getCapacity(ContainerType type) {
        String typeName = null;
        if (type instanceof ContainerTypeImpl) {
            typeName = ((ContainerTypeImpl) type).getType();
        }
        if (typeName != null) {
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(typeName)) {
                    return capacities[i];
                }
            }
        }
        return 0;
    }

    public boolean canTransport(AidBox aidBox) {
        Container[] containers = aidBox.getContainers();
        for (Container container : containers) {
            double capacityNeeded = container.getCapacity();
            ContainerType type = container.getType();
            if (getCapacity(type) < capacityNeeded) {
                return false;
            }
        }
        return true;
    }
}
