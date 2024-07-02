package com.estg.core;

public class ContainerTypeImpl implements ContainerType {
    private String type;

    public ContainerTypeImpl(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ContainerTypeImpl that = (ContainerTypeImpl) obj;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
