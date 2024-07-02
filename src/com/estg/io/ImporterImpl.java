package com.estg.io;

import com.estg.core.*;
import com.estg.core.exceptions.*;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.VehicleImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImporterImpl implements Importer {
    private final HTTPProvider httpProvider;

    public ImporterImpl(HTTPProvider httpProvider) {
        this.httpProvider = httpProvider;
    }

    @Override
    public void importData(Institution institution) throws IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("Institution cannot be null");
        }
        try {
            importAidBoxes(institution);
            importContainers(institution);
            importContainerTypes(institution);
            importDistances(institution);
            importVehicles(institution);
            importSensorReadings(institution);
        } catch (AidBoxException | ContainerException | VehicleException | MeasurementException e) {
            throw new RuntimeException(e);
        }
    }

    private void importAidBoxes(Institution institution) throws IOException, AidBoxException, ContainerException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/aidboxes";
        String response = httpProvider.getFromURL(url);
        JSONArray aidBoxesJson = parseJSONArray(response);

        for (Object obj : aidBoxesJson) {
            JSONObject aidBoxJson = (JSONObject) obj;
            String code = (String) aidBoxJson.get("code");
            String zone = (String) aidBoxJson.get("Zona");
            AidBox aidBox = new AidBoxImpl(code, zone);

            JSONArray containersJson = (JSONArray) aidBoxJson.get("containers");
            for (Object containerObj : containersJson) {
                String containerCode = (String) containerObj;
                Container container = ((InstitutionImpl) institution).findContainerByCode(containerCode);
                if (container != null) {
                    aidBox.addContainer(container);
                }
            }
            institution.addAidBox(aidBox);
        }
    }

    private void importContainers(Institution institution) throws IOException, ContainerException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/containers";
        String response = httpProvider.getFromURL(url);
        JSONArray containersJson = parseJSONArray(response);

        for (Object obj : containersJson) {
            JSONObject containerJson = (JSONObject) obj;
            String code = (String) containerJson.get("code");
            double capacity = ((Number) containerJson.get("capacity")).doubleValue();
            String type = (String) containerJson.get("type");
            ContainerType containerType = new ConcreteContainerType(ItemType.fromString(type));
            Container container = new ContainerImpl(code, capacity, containerType);
            ((InstitutionImpl) institution).addContainer(container);
        }
    }

    private void importContainerTypes(Institution institution) throws IOException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/types";
        String response = httpProvider.getFromURL(url);
        JSONArray typesArray = parseJSONArray(response);

        for (Object obj : typesArray) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                for (Object key : jsonObject.keySet()) {
                    String type = (String) key;
                    ((InstitutionImpl) institution).addContainerType(type);
                }
            } else if (obj instanceof String) {
                String type = (String) obj;
                ((InstitutionImpl) institution).addContainerType(type);
            }
        }
    }


    private void importDistances(Institution institution) throws IOException, AidBoxException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/distances";
        String response = httpProvider.getFromURL(url);
        JSONArray distancesJson = parseJSONArray(response);

        for (Object obj : distancesJson) {
            JSONObject distanceJson = (JSONObject) obj;
            String from = (String) distanceJson.get("from");
            JSONArray toArray = (JSONArray) distanceJson.get("to");

            for (Object toObj : toArray) {
                JSONObject toJson = (JSONObject) toObj;
                String to = (String) toJson.get("name");
                double distance = ((Number) toJson.get("distance")).doubleValue();
                double duration = ((Number) toJson.get("duration")).doubleValue();
                ((InstitutionImpl) institution).addDistance(from, to, distance, duration);
            }
        }
    }

    private void importVehicles(Institution institution) throws IOException, VehicleException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/vehicles";
        String response = httpProvider.getFromURL(url);
        JSONArray vehiclesJson = parseJSONArray(response);

        for (Object obj : vehiclesJson) {
            JSONObject vehicleJson = (JSONObject) obj;
            String code = (String) vehicleJson.get("code");
            JSONObject capacityJson = (JSONObject) vehicleJson.get("capacity");

            String[] types = new String[capacityJson.size()];
            int[] capacities = new int[capacityJson.size()];
            int index = 0;
            for (Object key : capacityJson.keySet()) {
                types[index] = (String) key;
                capacities[index] = ((Number) capacityJson.get(key)).intValue();
                index++;
            }
            Vehicle vehicle = new VehicleImpl(code, types, capacities);
            institution.addVehicle(vehicle);
        }
    }

    private void importSensorReadings(Institution institution) throws IOException, MeasurementException, ContainerException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/readings";
        String response = httpProvider.getFromURL(url);
        JSONArray readingsJson = parseJSONArray(response);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        for (Object obj : readingsJson) {
            JSONObject readingJson = (JSONObject) obj;
            String containerCode = (String) readingJson.get("contentor");
            LocalDateTime date = LocalDateTime.parse((String) readingJson.get("data"), formatter);
            double value = ((Number) readingJson.get("valor")).doubleValue();

            Measurement measurement = new MeasurementImpl(date, value);
            Container container = ((InstitutionImpl) institution).findContainerByCode(containerCode);
            if (container != null) {
                container.addMeasurement(measurement);
            }
        }
    }


    private JSONArray parseJSONArray(String response) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
