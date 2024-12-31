package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        String configFilePath = "configuration_file.json"; // Replace with the actual path

        try {
            // Step 1: Parse the main configuration file
            System.out.println("Parsing configuration file: " + configFilePath);
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(new FileReader(configFilePath), JsonObject.class);

            // Step 2: Initialize and verify GPS/IMU data
            System.out.println("Initializing GPS/IMU...");
            GPSIMUDataBase gpsimuDataBase = GPSIMUDataBase.getInstance();
            gpsimuDataBase.initialize(configFilePath);
            GPSIMU gpsimu = gpsimuDataBase.getGPSIMU();
            System.out.println("Loaded GPS/IMU data:");
            gpsimu.getPoseList().forEach(System.out::println);

            // Step 3: Initialize and verify LiDAR database
            String lidarDataPath = config.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();
            System.out.println("Loading LiDAR data from: " + lidarDataPath);
            LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath);
            System.out.println("Loaded LiDAR data:");
            lidarDataBase.getCloudPoints().forEach(System.out::println);

            // Step 4: Parse and initialize LiDAR workers
            System.out.println("Initializing LiDAR workers:");
            Type lidarConfigListType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> lidarConfigs = gson.fromJson(config.getAsJsonObject("LidarWorkers").get("LidarConfigurations"), lidarConfigListType);
            List<LiDarWorkerTracker> lidarWorkers = new ArrayList<>();

            for (JsonObject lidarConfig : lidarConfigs) {
                int id = lidarConfig.get("id").getAsInt();
                int frequency = lidarConfig.get("frequency").getAsInt();
                LiDarWorkerTracker worker = new LiDarWorkerTracker(id, frequency);
                worker.setStatus(STATUS.UP);
                lidarWorkers.add(worker);
                System.out.println("LiDAR Worker Initialized: " + worker.getFrequency() + " " + worker.getId()+ " " +worker.getStatus());
            }

            // Step 5: Initialize and verify cameras
            System.out.println("Initializing cameras...");
            List<Camera> cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);
            CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);

            System.out.println("Loaded Camera data:");
            for (Camera camera : cameras) {
                System.out.println("Camera ID: " + camera.getId());
                System.out.println("Frequency: " + camera.getFrequency());
                System.out.println("Detected Objects:");
                camera.getDetectedObjectsList().forEach(System.out::println);
                System.out.println("--------------------");
            }

            System.out.println("All components initialized successfully!");
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
