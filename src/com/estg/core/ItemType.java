package com.estg.core;

public enum ItemType {
    CLOTHING,
    MEDICINE,
    PERISHABLE_FOOD,
    NON_PERISHABLE_FOOD,
    BOOKS,
    UNKNOWN;

    public static ItemType fromString(String type) {
        try {
            return ItemType.valueOf(type.toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
