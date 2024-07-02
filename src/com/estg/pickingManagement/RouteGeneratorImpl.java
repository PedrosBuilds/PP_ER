package com.estg.pickingManagement;

import com.estg.core.Institution;
import com.estg.core.AidBox;

public class RouteGeneratorImpl implements RouteGenerator {
    @Override
    public Route[] generateRoutes(Institution institution) {
        // Implementar lógica de geração de rotas
        return new Route[0];
    }

    private boolean canVehicleTransportAidBox(Vehicle vehicle, AidBox aidBox) {
        if (vehicle instanceof VehicleImpl) {
            return ((VehicleImpl) vehicle).canTransport(aidBox);
        }
        return false;
    }
}
