package com.estg.io;

import com.estg.core.*;
import com.estg.core.exceptions.*;
import com.estg.io.Importer;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.VehicleImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class ImporterImpl implements Importer {
    private final HTTPProvider httpProvider;
    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .optionalStart()
            .appendLiteral('Z')
            .optionalEnd()
            .toFormatter();

    public ImporterImpl(HTTPProvider httpProvider) {
        this.httpProvider = httpProvider;
    }

    @Override
    public void importData(Institution institution) throws IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("Institution cannot be null");
        }
        InstitutionImpl instImpl = (InstitutionImpl) institution;
        try {
            importAidBoxesFromWeb(instImpl);
            importContainersFromWeb(instImpl);
            importContainerTypesFromWeb(instImpl);
            importDistancesFromWeb(instImpl);
            importVehiclesFromWeb(instImpl);
            importSensorReadingsFromWeb(instImpl);
        } catch (AidBoxException | ContainerException | VehicleException | MeasurementException e) {
            throw new RuntimeException(e);
        }
    }

    public void importData(Institution institution, boolean fromWeb) throws IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("Institution cannot be null");
        }
        InstitutionImpl instImpl = (InstitutionImpl) institution;
        try {
            if (fromWeb) {
                importAidBoxesFromWeb(instImpl);
                importContainersFromWeb(instImpl);
                importContainerTypesFromWeb(instImpl);
                importDistancesFromWeb(instImpl);
                importVehiclesFromWeb(instImpl);
                importSensorReadingsFromWeb(instImpl);
            } else {
                importAidBoxesFromFile(instImpl);
                importContainersFromFile(instImpl);
                importContainerTypesFromFile(instImpl);
                importDistancesFromFile(instImpl);
                importVehiclesFromFile(instImpl);
                importSensorReadingsFromFile(instImpl);
            }
        } catch (AidBoxException | ContainerException | VehicleException | MeasurementException e) {
            throw new RuntimeException(e);
        }
    }

    private void importAidBoxesFromWeb(InstitutionImpl institution) throws IOException, AidBoxException, ContainerException {
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
                Container container = institution.findContainerByCode(containerCode);
                if (container != null) {
                    aidBox.addContainer(container);
                }
            }
            institution.addAidBox(aidBox);
        }
    }

    private void importContainersFromWeb(InstitutionImpl institution) throws IOException, ContainerException {
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
            institution.addContainer(container);
        }
    }

    private void importContainerTypesFromWeb(InstitutionImpl institution) throws IOException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/types";
        String response = httpProvider.getFromURL(url);
        JSONArray typesArray = parseJSONArray(response);

        for (Object obj : typesArray) {
            JSONObject typeObj = (JSONObject) obj; // Ajuste para tratar corretamente os objetos JSON
            String type = (String) typeObj.get("type"); // Assumindo que o campo de interesse é "type"
            institution.addContainerType(type);
        }
    }

    private void importDistancesFromWeb(InstitutionImpl institution) throws IOException, AidBoxException {
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
                institution.addDistance(from, to, distance, duration);
            }
        }
    }

    private void importVehiclesFromWeb(InstitutionImpl institution) throws IOException, VehicleException {
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

    private void importSensorReadingsFromWeb(InstitutionImpl institution) throws IOException, MeasurementException, ContainerException {
        String url = "https://data.mongodb-api.com/app/data-docuz/endpoint/readings";
        String response = httpProvider.getFromURL(url);
        JSONArray readingsJson = parseJSONArray(response);

        for (Object obj : readingsJson) {
            JSONObject readingJson = (JSONObject) obj;
            String containerCode = (String) readingJson.get("contentor");
            String dateString = (String) readingJson.get("data");
            LocalDateTime date = parseDate(dateString);
            double value = ((Number) readingJson.get("valor")).doubleValue();

            Measurement measurement = new MeasurementImpl(date, value);
            Container container = institution.findContainerByCode(containerCode);
            if (container != null) {
                container.addMeasurement(measurement);
            }
        }
    }

    private void importAidBoxesFromFile(InstitutionImpl institution) throws IOException, AidBoxException, ContainerException {
        try (FileReader reader = new FileReader("aidBoxes.json")) {
            JSONArray aidBoxesJson = (JSONArray) parseJSON(reader);

            for (Object obj : aidBoxesJson) {
                JSONObject aidBoxJson = (JSONObject) obj;
                String code = (String) aidBoxJson.get("code");
                String zone = (String) aidBoxJson.get("Zona");
                AidBox aidBox = new AidBoxImpl(code, zone);

                JSONArray containersJson = (JSONArray) aidBoxJson.get("containers");
                for (Object containerObj : containersJson) {
                    String containerCode = (String) containerObj;
                    Container container = institution.findContainerByCode(containerCode);
                    if (container != null) {
                        aidBox.addContainer(container);
                    }
                }
                institution.addAidBox(aidBox);
            }
        }
    }

    private void importContainersFromFile(InstitutionImpl institution) throws IOException, ContainerException {
        try (FileReader reader = new FileReader("containers.json")) {
            JSONArray containersJson = (JSONArray) parseJSON(reader);

            for (Object obj : containersJson) {
                JSONObject containerJson = (JSONObject) obj;
                String code = (String) containerJson.get("code");
                double capacity = ((Number) containerJson.get("capacity")).doubleValue();
                String type = (String) containerJson.get("type");
                ContainerType containerType = new ConcreteContainerType(ItemType.fromString(type));
                Container container = new ContainerImpl(code, capacity, containerType);
                institution.addContainer(container);
            }
        }
    }

    private void importContainerTypesFromFile(InstitutionImpl institution) throws IOException {
        try (FileReader reader = new FileReader("types.json")) {
            JSONArray typesArray = (JSONArray) parseJSON(reader);

            for (Object obj : typesArray) {
                JSONObject typeObj = (JSONObject) obj; // Ajuste para tratar corretamente os objetos JSON
                String type = (String) typeObj.get("type"); // Assumindo que o campo de interesse é "type"
                institution.addContainerType(type);
            }
        }
    }

    private void importDistancesFromFile(InstitutionImpl institution) throws IOException, AidBoxException {
        try (FileReader reader = new FileReader("distances.json")) {
            JSONArray distancesJson = (JSONArray) parseJSON(reader);

            for (Object obj : distancesJson) {
                JSONObject distanceJson = (JSONObject) obj;
                String from = (String) distanceJson.get("from");
                JSONArray toArray = (JSONArray) distanceJson.get("to");

                for (Object toObj : toArray) {
                    JSONObject toJson = (JSONObject) toObj;
                    String to = (String) toJson.get("name");
                    double distance = ((Number) toJson.get("distance")).doubleValue();
                    double duration = ((Number) toJson.get("duration")).doubleValue();
                    institution.addDistance(from, to, distance, duration);
                }
            }
        }
    }

    private void importVehiclesFromFile(InstitutionImpl institution) throws IOException, VehicleException {
        try (FileReader reader = new FileReader("vehicles.json")) {
            JSONArray vehiclesJson = (JSONArray) parseJSON(reader);

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
    }

    private void importSensorReadingsFromFile(InstitutionImpl institution) throws IOException, MeasurementException, ContainerException {
        try (FileReader reader = new FileReader("readings.json")) {
            JSONArray readingsJson = (JSONArray) parseJSON(reader);

            for (Object obj : readingsJson) {
                JSONObject readingJson = (JSONObject) obj;
                String containerCode = (String) readingJson.get("contentor");
                String dateString = (String) readingJson.get("data");
                LocalDateTime date = parseDate(dateString);
                double value = ((Number) readingJson.get("valor")).doubleValue();

                Measurement measurement = new MeasurementImpl(date, value);
                Container container = institution.findContainerByCode(containerCode);
                if (container != null) {
                    container.addMeasurement(measurement);
                }
            }
        }
    }

    private LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error parsing date: " + dateString, e);
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

    private JSONArray parseJSON(FileReader reader) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(reader);
        } catch (ParseException | IOException e) {
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
