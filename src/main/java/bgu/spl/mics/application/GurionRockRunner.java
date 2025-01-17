package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    private static final List<LiDarWorkerTracker> lidarWorkers = new ArrayList<>();

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        if (args.length < 1) {
            System.err.println("Usage: java Main <config-file-path>");
            return;
        }

        // Get the configuration file path from the arguments
        String configFilePath = args[0];
        String configDir = new File(configFilePath).getParent(); // Extract directory path
        String abspath = args[0].substring(0, args[0].lastIndexOf('/'));
        try {
            // Parse configuration file
            JsonObject config = parseConfigFile(configFilePath);
            int tickTime = config.get("TickTime").getAsInt();
            int duration = config.get("Duration").getAsInt();
            System.out.println("TickTime: " + tickTime);
            String lidarDataPath = config.getAsJsonObject("LiDarWorkers").get("lidars_data_path").getAsString().substring(1);
            System.out.println("LidarDataPath: " + lidarDataPath);
            System.out.println("Attempting to load LiDAR data from: " + lidarDataPath);

            //init Lidar DB
            LiDarDataBase lidarDataBase = loadLidarData(abspath + lidarDataPath);
            lidarDataBase.setCounterOfTrackedCloudPoints(LiDarDataBase.getInstance().getCloudPoints().size());
            System.out.println("lidar db counter!!!:" + LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints());

            // Initialize LiDAR workers
            List<JsonObject> lidarConfigs = parseLidarWorkerConfigs(config);
            initializeLidarWorkers(lidarConfigs);

            // Initialize cameras
            List<Camera> cameras = initializeCameras(configFilePath);

            // Initialize GPSIMU
            GPSIMU gpsimu = initializeGPSIMU(configFilePath);


            System.out.println("Lidars data");
            lidarWorkers.forEach(liDarWorkerTracker -> {
                System.out.println(liDarWorkerTracker.toString());
            });


            System.out.println("\n");
            System.out.println("GpsData:");

            //Initing the microServicesList
            List<Thread> microserviceThreads = new ArrayList<>();
            //Creating Services:
            //Camera services:
            cameras.forEach(camera -> {
                microserviceThreads.add(new Thread(new CameraService(camera)));
            });

            //Lidars Services:
            lidarWorkers.forEach(liDarWorkerTracker -> {
                microserviceThreads.add(new Thread(new LiDarWorkerService(liDarWorkerTracker)));
            });


            //FusionSlam Service:
            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam, cameras.size(), lidarWorkers.size());
            microserviceThreads.add(new Thread(fusionSlamService));

            //Pose Service:
            microserviceThreads.add(new Thread(new PoseService(gpsimu)));

            //Time Service:
            microserviceThreads.add(new Thread(new TimeService(tickTime, duration)));

            //letch inting:
            int numberOfServices = cameras.size() + lidarWorkers.size() + 2;
            SystemServicesCountDownLatch.init(numberOfServices);
            for (Thread thread : microserviceThreads) {
                thread.start();
            }
            for (Thread thread : microserviceThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("finisheddd");

            if (FusionSlam.getInstance().isThereIsError()) {
                ErrorOutput.getInstance().createErrorOutputFile(configDir); // Pass configDir to ErrorOutput
            } else {
                createOutputJsonFile(fusionSlam, StatisticalFolder.getInstance(), configDir);
                System.out.println("Output JSON file created: output_file.json");
            }


        } catch (IOException e) {
            handleError("Failed to load configuration file", e);
        } catch (RuntimeException e) {
            handleError("Error initializing components", e);
        }
    }

    private static JsonObject parseConfigFile(String configFilePath) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(new FileReader(configFilePath), JsonObject.class);
    }

    private static LiDarDataBase loadLidarData(String lidarDataPath) {
        LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath);

        // Verify loaded LiDAR data

        return lidarDataBase;
    }

    private static List<JsonObject> parseLidarWorkerConfigs(JsonObject config) {
        Gson gson = new Gson();
        Type lidarConfigListType = new TypeToken<List<JsonObject>>() {
        }.getType();
        return gson.fromJson(config.getAsJsonObject("LiDarWorkers").get("LidarConfigurations"), lidarConfigListType);
    }

    private static void initializeLidarWorkers(List<JsonObject> lidarConfigs) {
        for (JsonObject lidarConfig : lidarConfigs) {
            int id = lidarConfig.get("id").getAsInt();
            int frequency = lidarConfig.get("frequency").getAsInt();

            // Create and add LiDAR worker to the list
            LiDarWorkerTracker worker = new LiDarWorkerTracker(id, frequency);
            lidarWorkers.add(worker);

        }//
    }

    private static List<Camera> initializeCameras(String configFilePath) {
        // Initialize cameras and update detected objects
        List<Camera> cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);
        CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);
        return cameras;
    }

    private static GPSIMU initializeGPSIMU(String configFilePath) {
        GPSIMUDataBase gpsimuDataBase = GPSIMUDataBase.getInstance();
        gpsimuDataBase.initialize(configFilePath);
        return gpsimuDataBase.getGPSIMU();
    }

    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    private static void createOutputJsonFile(FusionSlam fusionSlam, StatisticalFolder statisticalFolder, String dirPath) throws IOException {
        Gson gson = new Gson();
        JsonObject outputJson = new JsonObject();
        File outputFile = new File(dirPath, "output_file.json"); // Save to specified directory


        outputJson.addProperty("systemRuntime", statisticalFolder.getSystemRuntime());
        outputJson.addProperty("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        outputJson.addProperty("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        outputJson.addProperty("numLandmarks", statisticalFolder.getNumLandmarks());

        JsonObject landmarksJson = new JsonObject();
        fusionSlam.getLandmarks().forEach(landmark -> {
            JsonObject landmarkJson = new JsonObject();
            landmarkJson.addProperty("id", landmark.getId());
            landmarkJson.addProperty("description", landmark.getDescription());

            List<JsonObject> coordinates = new ArrayList<>();
            landmark.getCoordinates().forEach(coord -> {
                JsonObject coordinate = new JsonObject();
                coordinate.addProperty("x", coord.getX());
                coordinate.addProperty("y", coord.getY());
                coordinates.add(coordinate);
            });
            Type listType = new TypeToken<List<JsonObject>>() {
            }.getType();
            landmarkJson.add("coordinates", gson.toJsonTree(coordinates, listType));
            landmarksJson.add(landmark.getId(), landmarkJson);
        });
        outputJson.add("landMarks", landmarksJson);

        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(outputJson, writer);
        }
    }


}

