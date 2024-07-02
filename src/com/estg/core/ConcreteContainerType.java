package com.estg.core;

/**
 * Concrete implementation of the ContainerType interface
 */
public class ConcreteContainerType implements ContainerType {
    private ItemType itemType;

    public ConcreteContainerType(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public String toString() {
        return itemType.toString();
    }
}
