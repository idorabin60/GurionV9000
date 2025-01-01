package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Define configuration file path
        String configFilePath = "configuration_file.json"; // Replace with the actual path

        try {
            // Parse configuration file
            JsonObject config = parseConfigFile(configFilePath);

            // Load and verify LiDAR data
            String lidarDataPath = config.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();
            LiDarDataBase lidarDataBase = loadLidarData(lidarDataPath);

            // Initialize LiDAR workers
            List<JsonObject> lidarConfigs = parseLidarWorkerConfigs(config);
            initializeLidarWorkers(lidarConfigs);

            // Initialize cameras
            List<Camera> cameras = initializeCameras(configFilePath);
            System.out.println("\nInitialized Cameras:");
            cameras.forEach(System.out::println);

        } catch (IOException e) {
            handleError("Failed to load configuration file", e);
        } catch (RuntimeException e) {
            handleError("Error initializing components", e);
        }
    }

    private static JsonObject parseConfigFile(String configFilePath) throws IOException {
        System.out.println("Parsing configuration file: " + configFilePath);
        Gson gson = new Gson();
        return gson.fromJson(new FileReader(configFilePath), JsonObject.class);
    }

    private static LiDarDataBase loadLidarData(String lidarDataPath) {
        System.out.println("Loading LiDAR data from: " + lidarDataPath);
        LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath);

        // Verify loaded LiDAR data
        System.out.println("Loaded LiDAR data:");
        lidarDataBase.getCloudPoints().forEach(System.out::println);

        return lidarDataBase;
    }

    private static List<JsonObject> parseLidarWorkerConfigs(JsonObject config) {
        System.out.println("Parsing LiDAR worker configurations:");
        Gson gson = new Gson();
        Type lidarConfigListType = new TypeToken<List<JsonObject>>() {}.getType();
        return gson.fromJson(config.getAsJsonObject("LidarWorkers").get("LidarConfigurations"), lidarConfigListType);
    }

    private static void initializeLidarWorkers(List<JsonObject> lidarConfigs) {
        System.out.println("Initializing LiDAR workers:");
        for (JsonObject lidarConfig : lidarConfigs) {
            int id = lidarConfig.get("id").getAsInt();
            int frequency = lidarConfig.get("frequency").getAsInt();

            // Create and print LiDAR worker details
            LiDarWorkerTracker worker = new LiDarWorkerTracker(id, frequency);
            System.out.println("LiDAR Worker Initialized:");
            System.out.println("ID: " + worker.getId());
            System.out.println("Frequency: " + worker.getFrequency());
            System.out.println("Status: " + worker.getStatus());
            System.out.println("Last Tracked Objects: " + worker.getLastTrackedObjects());
        }
    }

    private static List<Camera> initializeCameras(String configFilePath) {
        // Initialize cameras and update detected objects
        List<Camera> cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);
        CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);
        return cameras;
    }

    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
