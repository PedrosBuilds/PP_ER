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
    private Container[] containers;
    private int containerCount;
    private final int initialCapacity = 10;

    public ImporterImpl(HTTPProvider httpProvider) {
        this.httpProvider = httpProvider;
        this.containers = new Container[initialCapacity];
        this.containerCount = 0;
    }

    @Override
    public void importData(Institution institution) throws IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("Institution cannot be null");
        }
        try {
            importAidBoxes(institution);
            importContainers();
            importContainerTypes();
            importDistances(institution);
            importVehicles(institution);
            importSensorReadings();
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
                Container container = findContainerByCode(containerCode);
                if (container != null) {
                    aidBox.addContainer(container);
                }
            }
            institution.addAidBox(aidBox);
        }
    }

    private void importContainers() throws IOException, ContainerException {
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
            addContainer(container);
        }
    }

    private void importContainerTypes() throws IOException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/types";
        String response = httpProvider.getFromURL(url);
        JSONArray typesArray = parseJSONArray(response);

        for (Object obj : typesArray) {
            if (obj instanceof String) {
                String type = (String) obj;
                // Armazenar ou usar conforme necessário
            } else if (obj instanceof JSONObject) {
                JSONObject typeJson = (JSONObject) obj;
                String type = (String) typeJson.get("type");
                // Armazenar ou usar conforme necessário
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
                // Armazenar ou processar conforme necessário
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

    private void importSensorReadings() throws IOException, MeasurementException, ContainerException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/readings";
        String response = httpProvider.getFromURL(url);
        JSONArray readingsJson = parseJSONArray(response);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        for (Object obj : readingsJson) {
            JSONObject readingJson = (JSONObject) obj;
            String containerCode = (String) readingJson.get("contentor");
            LocalDateTime date = LocalDateTime.parse((String) readingJson.get("data"), formatter);
            double value = ((Number) readingJson.get("valor")).doubleValue();

            Measurement measurement = new MeasurementImpl(date, value);
            Container container = findContainerByCode(containerCode);
            if (container != null) {
                container.addMeasurement(measurement);
            }
        }
    }

    private void addContainer(Container container) {
        if (containerCount == containers.length) {
            Container[] newContainers = new Container[containers.length * 2];
            System.arraycopy(containers, 0, newContainers, 0, containers.length);
            containers = newContainers;
        }
        containers[containerCount++] = container;
    }

    private Container findContainerByCode(String code) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getCode().equals(code)) {
                return containers[i];
            }
        }
        return null;
    }

    private JSONArray parseJSONArray(String response) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject parseJSONObject(String response) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseJSON(String response) {
        try {
            JSONParser parser = new JSONParser();
            return parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
