package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;

public class AidBoxImpl implements AidBox {
    private String code;
    private String zone;
    private Container[] containers;
    private int containerCount;

    public AidBoxImpl(String code, String zone) {
        this.code = code;
        this.zone = zone;
        this.containers = new Container[10]; // Capacidade inicial
        this.containerCount = 0;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public double getDistance(AidBox other) throws AidBoxException {
        // Implementar lógica para calcular a distância
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double getDuration(AidBox other) throws AidBoxException {
        // Implementar lógica para calcular a duração
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean addContainer(Container container) throws ContainerException {
        if (containerCount == containers.length) {
            // Redimensionar array se necessário
            Container[] newContainers = new Container[containers.length * 2];
            System.arraycopy(containers, 0, newContainers, 0, containers.length);
            containers = newContainers;
        }
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].equals(container)) {
                throw new ContainerException("Container já existe nesta AidBox");
            }
        }
        containers[containerCount++] = container;
        return true;
    }

    @Override
    public Container getContainer(ContainerType type) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getType().equals(type)) {
                return containers[i];
            }
        }
        return null;
    }

    @Override
    public Container[] getContainers() {
        Container[] activeContainers = new Container[containerCount];
        System.arraycopy(containers, 0, activeContainers, 0, containerCount);
        return activeContainers;
    }

    @Override
    public void removeContainer(Container container) throws AidBoxException {
        boolean found = false;
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].equals(container)) {
                found = true;
                containers[i] = containers[--containerCount];
                containers[containerCount] = null; // Liberar referência
                break;
            }
        }
        if (!found) {
            throw new AidBoxException("Container não encontrado nesta AidBox");
        }
    }

    @Override
    public Object clone() {
        try {
            AidBoxImpl cloned = (AidBoxImpl) super.clone();
            cloned.containers = new Container[containers.length];
            System.arraycopy(containers, 0, cloned.containers, 0, containerCount);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Isto nunca deve acontecer porque estamos clonando
        }
    }

    // Método adicional para verificar se um contêiner está presente
    public boolean containsContainer(Container container) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].equals(container)) {
                return true;
            }
        }
        return false;
    }
}
