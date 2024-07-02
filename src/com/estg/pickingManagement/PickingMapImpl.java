package com.estg.pickingManagement;

import com.estg.pickingManagement.exceptions.PickingMapException;

import java.time.LocalDateTime;

public class PickingMapImpl implements PickingMap {
    private LocalDateTime date;
    private Route[] routes;
    private int routeCount;

    public PickingMapImpl(LocalDateTime date, Route[] initialRoutes) {
        this.date = date;
        this.routes = new Route[initialRoutes.length];
        this.routeCount = 0;
        for (Route route : initialRoutes) {
            this.routes[this.routeCount++] = route;
        }
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public Route[] getRoutes() {
        Route[] result = new Route[routeCount];
        for (int i = 0; i < routeCount; i++) {
            result[i] = routes[i];
        }
        return result;
    }

    public void addRoute(Route route) {
        if (route == null) {
            return;
        }
        if (routeCount == routes.length) {
            Route[] newRoutes = new Route[routes.length * 2];
            for (int i = 0; i < routes.length; i++) {
                newRoutes[i] = routes[i];
            }
            routes = newRoutes;
        }
        routes[routeCount++] = route;
    }

    public void removeRoute(Route route) throws PickingMapException {
        if (route == null) {
            throw new PickingMapException("Route cannot be null");
        }

        int index = -1;
        for (int i = 0; i < routeCount; i++) {
            if (routes[i].equals(route)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new PickingMapException("Route not found");
        }

        for (int i = index; i < routeCount - 1; i++) {
            routes[i] = routes[i + 1];
        }
        routes[--routeCount] = null;
    }
}
