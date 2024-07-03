package com.estg.main;

import com.estg.core.*;
import com.estg.core.exceptions.InstitutionException;
import com.estg.io.HTTPProvider;
import com.estg.io.ImporterImpl;
import com.estg.pickingManagement.Vehicle;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static ImporterImpl importer;
    private static InstitutionImpl institution;

    public static void main(String[] args) {
        HTTPProvider httpProvider = new HTTPProvider();
        importer = new ImporterImpl(httpProvider);
        institution = new InstitutionImpl("InstitutionName");

        try {
            showImportOptions();
            showMenu();
        } catch (IOException | InstitutionException e) {
            System.out.println("Failed to import data: " + e.getMessage());
        }
    }

    private static void showImportOptions() throws IOException, InstitutionException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select data import option:");
        System.out.println("1. Load data from WebAPI");
        System.out.println("2. Load data from files");

        int choice = scanner.nextInt();
        boolean fromWeb;
        switch (choice) {
            case 1 -> fromWeb = true;
            case 2 -> fromWeb = false;
            default -> {
                System.out.println("Invalid option. Defaulting to WebAPI.");
                fromWeb = true;
            }
        }

        importData(fromWeb);
    }

    private static void importData(boolean fromWeb) throws IOException, InstitutionException {
        importer.importData(institution, fromWeb);
        System.out.println("Data imported successfully.");
    }

    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. View AidBoxes");
            System.out.println("2. View Containers");
            System.out.println("3. View Container Types");
            System.out.println("4. View Distances");
            System.out.println("5. View Vehicles");
            System.out.println("6. View Sensor Readings");
            System.out.println("7. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> viewAidBoxes();
                case 2 -> viewContainers();
                case 3 -> viewContainerTypes();
                case 4 -> viewDistances();
                case 5 -> viewVehicles();
                case 6 -> viewSensorReadings();
                case 7 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAidBoxes() {
        System.out.println("AidBoxes:");
        for (AidBox aidBox : institution.getAidBoxes()) {
            System.out.println("Code: " + aidBox.getCode() + ", Zone: " + aidBox.getZone());
        }
    }

    private static void viewContainers() {
        System.out.println("Containers:");
        for (Container container : institution.getAllContainers()) {
            System.out.println("Code: " + container.getCode() + ", Capacity: " + container.getCapacity() + ", Type: " + container.getType());
        }
    }

    private static void viewContainerTypes() {
        System.out.println("Container Types:");
        for (String type : institution.getContainerTypes()) {
            System.out.println("Type: " + type);
        }
    }

    private static void viewDistances() {
        System.out.println("Distances:");
        for (InstitutionImpl.Distance distance : institution.getDistances()) {
            System.out.println("From: " + distance.getFrom() + ", To: " + distance.getTo() + ", Distance: " + distance.getDistance() + ", Duration: " + distance.getDuration());
        }
    }

    private static void viewVehicles() {
        System.out.println("Vehicles:");
        for (Vehicle vehicle : institution.getVehicles()) {
            System.out.println("Code: " + vehicle.getCode());
        }
    }

    private static void viewSensorReadings() {
        System.out.println("Sensor Readings:");
        for (Container container : institution.getAllContainers()) {
            for (Measurement measurement : container.getMeasurements()) {
                System.out.println("Container: " + container.getCode() + ", Date: " + measurement.getDate() + ", Value: " + measurement.getValue());
            }
        }
    }
}
