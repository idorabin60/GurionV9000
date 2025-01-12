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
        System.out.println("Current working directory: " + new File(".").getAbsolutePath());
        System.out.println("Configuration file path: " + configFilePath);

        try {
            // Parse configuration file
            JsonObject config = parseConfigFile(configFilePath);
            int tickTime = config.get("TickTime").getAsInt();
            int duration = config.get("Duration").getAsInt();
            System.out.println("TickTime: " + tickTime);

            // Resolve and load LiDAR data
            String lidarDataPath = config.getAsJsonObject("LiDarWorkers").get("lidars_data_path").getAsString();
            System.out.println("Attempting to load LiDAR data from: " + lidarDataPath);
            LiDarDataBase lidarDataBase = loadLidarData(lidarDataPath);
            lidarDataBase.setCounterOfTrackedCloudPoints(LiDarDataBase.getInstance().getCloudPoints().size());
            System.out.println("LiDAR DB counter: " + LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints());

            // Initialize LiDAR workers
            List<JsonObject> lidarConfigs = parseLidarWorkerConfigs(config);
            initializeLidarWorkers(lidarConfigs);
            lidarWorkers.forEach(worker -> System.out.println(worker.toString()));


            // Resolve and initialize cameras
            List<Camera> cameras = parseCameraConfigs(config.getAsJsonObject("Cameras"));
            cameras.forEach(camera -> System.out.println(camera.toString()));

            // Initialize GPSIMU
            GPSIMU gpsimu = initializeGPSIMU(config.get("poseJsonFile").getAsString());
            gpsimu.getPoseList().forEach(pose -> System.out.println(pose.toString()));

            System.out.println("LiDARs data:");
            lidarWorkers.forEach(worker -> System.out.println(worker.toString()));

            // Initialize microservices
            List<Thread> microserviceThreads = new ArrayList<>();
            cameras.forEach(camera -> microserviceThreads.add(new Thread(new CameraService(camera))));
            lidarWorkers.forEach(worker -> microserviceThreads.add(new Thread(new LiDarWorkerService(worker))));
            microserviceThreads.add(new Thread(new FusionSlamService(FusionSlam.getInstance(), cameras.size(), lidarWorkers.size())));
            microserviceThreads.add(new Thread(new PoseService(gpsimu)));
            microserviceThreads.add(new Thread(new TimeService(tickTime, duration)));

            // Start microservices
            SystemServicesCountDownLatch.init(cameras.size() + lidarWorkers.size() + 2);
            for (Thread thread : microserviceThreads) thread.start();
            for (Thread thread : microserviceThreads) thread.join();

            System.out.println("Simulation finished.");

            if (FusionSlam.getInstance().isThereIsError()) {
                ErrorOutput.getInstance().createErrorOutputFile();
            } else {
                createOutputJsonFile(FusionSlam.getInstance(), StatisticalFolder.getInstance());
                System.out.println("Output JSON file created: output_file.json");
            }
        } catch (IOException e) {
            handleError("Failed to load configuration file", e);
        } catch (RuntimeException | InterruptedException e) {
            handleError("Error during simulation", e);
        }
    }

    private static JsonObject parseConfigFile(String configFilePath) throws IOException {
        Gson gson = new Gson();
        JsonObject config = gson.fromJson(new FileReader(configFilePath), JsonObject.class);

        File configFile = new File(configFilePath);
        String baseDir = configFile.getParent();

        resolvePath(config, "LiDarWorkers", "lidars_data_path", baseDir);
        resolvePath(config, "Cameras", "camera_datas_path", baseDir);
        resolvePath(config, null, "poseJsonFile", baseDir);

        return config;
    }

    private static void resolvePath(JsonObject config, String section, String key, String baseDir) {
        if (section != null && config.has(section)) {
            JsonObject sectionObject = config.getAsJsonObject(section);
            if (sectionObject.has(key)) {
                String relativePath = sectionObject.get(key).getAsString();
                File resolvedFile = new File(baseDir, relativePath.startsWith("./") ? relativePath.substring(2) : relativePath);
                sectionObject.addProperty(key, resolvedFile.getAbsolutePath());
            }
        } else if (config.has(key)) {
            String relativePath = config.get(key).getAsString();
            File resolvedFile = new File(baseDir, relativePath.startsWith("./") ? relativePath.substring(2) : relativePath);
            config.addProperty(key, resolvedFile.getAbsolutePath());
        }
    }

    private static LiDarDataBase loadLidarData(String lidarDataPath) {
        File lidarFile = new File(lidarDataPath);
        if (!lidarFile.exists()) {
            throw new RuntimeException("LiDAR data file does not exist: " + lidarFile.getAbsolutePath());
        }
        return LiDarDataBase.getInstance(lidarDataPath);
    }

    private static List<JsonObject> parseLidarWorkerConfigs(JsonObject config) {
        Gson gson = new Gson();
        Type lidarConfigListType = new TypeToken<List<JsonObject>>() {}.getType();
        return gson.fromJson(config.getAsJsonObject("LiDarWorkers").get("LidarConfigurations"), lidarConfigListType);
    }

    private static void initializeLidarWorkers(List<JsonObject> lidarConfigs) {
        lidarConfigs.forEach(config -> {
            int id = config.get("id").getAsInt();
            int frequency = config.get("frequency").getAsInt();
            lidarWorkers.add(new LiDarWorkerTracker(id, frequency));
        });
    }

    private static List<Camera> parseCameraConfigs(JsonObject camerasConfig) {
        List<Camera> cameras = new ArrayList<>();
        if (camerasConfig != null && camerasConfig.has("CamerasConfigurations")) {
            Type cameraConfigListType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> cameraConfigs = new Gson().fromJson(
                    camerasConfig.get("CamerasConfigurations"), cameraConfigListType);

            for (JsonObject cameraConfig : cameraConfigs) {
                int id = cameraConfig.get("id").getAsInt();
                int frequency = cameraConfig.get("frequency").getAsInt();
                cameras.add(new Camera(id, frequency, STATUS.UP));
            }
        } else {
            throw new RuntimeException("CamerasConfigurations is missing or invalid in configuration file.");
        }
        return cameras;
    }

    private static GPSIMU initializeGPSIMU(String poseJsonFilePath) {
        GPSIMUDataBase gpsimuDataBase = GPSIMUDataBase.getInstance();
        gpsimuDataBase.initialize(poseJsonFilePath);
        return gpsimuDataBase.getGPSIMU();
    }

    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    private static void createOutputJsonFile(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) throws IOException {
        Gson gson = new Gson();
        JsonObject outputJson = new JsonObject();

        outputJson.addProperty("systemRuntime", statisticalFolder.getSystemRuntime());
        outputJson.addProperty("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        outputJson.addProperty("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        outputJson.addProperty("numLandmarks", statisticalFolder.getNumLandmarks());

        JsonObject landmarksJson = new JsonObject();
        fusionSlam.getLandmarks().forEach(landmark -> {
            JsonObject landmarkJson = new JsonObject();
            landmarkJson.addProperty("id", landmark.getId());
            landmarkJson.addProperty("description", landmark.getDescription());
            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
            landmarkJson.add("coordinates", gson.toJsonTree(landmark.getCoordinates(), listType));
            landmarksJson.add(landmark.getId(), landmarkJson);
        });
        outputJson.add("landMarks", landmarksJson);

        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(outputJson, writer);
        }
    }
}
