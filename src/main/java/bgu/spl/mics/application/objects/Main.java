package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final List<LiDarWorkerTracker> lidarWorkers = new ArrayList<>();

    public static void main(String[] args) {
        // Define configuration file path
        String configFilePath = "configuration_file.json"; // Replace with the actual path

        try {
            // Parse configuration file
            JsonObject config = parseConfigFile(configFilePath);
            int tickTime = config.get("TickTime").getAsInt();
            int duration = config.get("Duration").getAsInt();
            System.out.println("TickTime: " + tickTime);
            String lidarDataPath = config.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();
            System.out.println("Attempting to load LiDAR data from: " + lidarDataPath);

            //init Lidar DB
            LiDarDataBase lidarDataBase = loadLidarData(lidarDataPath);
            lidarDataBase.setCounterOfTrackedCloudPoints(LiDarDataBase.getInstance().getCloudPoints().size());
            System.out.println("lidar db counter!!!:"+LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints() );

            // Initialize LiDAR workers
            List<JsonObject> lidarConfigs = parseLidarWorkerConfigs(config);
            initializeLidarWorkers(lidarConfigs);

            // Initialize cameras
            List<Camera> cameras = initializeCameras(configFilePath);

            // Initialize GPSIMU
            GPSIMU gpsimu = initializeGPSIMU(configFilePath);

            System.out.println("Priniting objects:");
            System.out.println("\n");

            //Check each filed values:
            System.out.println("Cameras data:");
            cameras.forEach(camera -> {
                System.out.println(camera.toString());
            });
            System.out.println("\n");


            System.out.println("Lidars data");
            lidarWorkers.forEach(liDarWorkerTracker -> {
                System.out.println(liDarWorkerTracker.toString());
            });
            System.out.println("\n");
            System.out.println("Lidar DB data");
//            System.out.println(lidarDataBase.toString());

            System.out.println("\n");
            System.out.println("GpsData:");
            System.out.println(gpsimu.getPoseList().toString());

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
            FusionSlam.getInstance().getLandmarks().forEach(landmark -> {
                System.out.println(landmark.toString());
            });
            System.out.println("Statistical folder data: " + StatisticalFolder.getInstance().toString());


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
        return gson.fromJson(config.getAsJsonObject("LidarWorkers").get("LidarConfigurations"), lidarConfigListType);
    }

    private static void initializeLidarWorkers(List<JsonObject> lidarConfigs) {
        for (JsonObject lidarConfig : lidarConfigs) {
            int id = lidarConfig.get("id").getAsInt();
            int frequency = lidarConfig.get("frequency").getAsInt();

            // Create and add LiDAR worker to the list
            LiDarWorkerTracker worker = new LiDarWorkerTracker(id, frequency);
            lidarWorkers.add(worker);

        }
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

}//bla:
