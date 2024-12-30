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
        String configFilePath = "configuration_file.json"; // Replace with the actual path

        try {
            // Step 1: Parse the main configuration file
            System.out.println("Parsing configuration file: " + configFilePath);
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(new FileReader(configFilePath), JsonObject.class);

            // Step 2: Parse and load LiDAR data
            String lidarDataPath = config.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();
            System.out.println("Loading LiDAR data from: " + lidarDataPath);
            LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath);

            // Verify loaded LiDAR data
            System.out.println("Loaded LiDAR data:");
            lidarDataBase.getCloudPoints().forEach(System.out::println);

            // Step 3: Parse LiDAR worker configurations
            Type lidarConfigListType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> lidarConfigs = gson.fromJson(config.getAsJsonObject("LidarWorkers").get("LidarConfigurations"), lidarConfigListType);

            System.out.println("Initializing LiDAR workers:");
            for (JsonObject lidarConfig : lidarConfigs) {
                int id = lidarConfig.get("id").getAsInt();
                int frequency = lidarConfig.get("frequency").getAsInt();

                // Create and print LiDAR worker details
                LiDarWorkerTracker worker = new LiDarWorkerTracker(id, frequency);
                System.out.println("LiDAR Worker Initialized: ");
                System.out.println("ID: " + worker.getId());
                System.out.println("Frequency: " + worker.getFrequency());
                System.out.println("Status: " + worker.getStatus());
                System.out.println("Last Tracked Objects: " + worker.getLastTrackedObjects());
            }

        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Error initializing LiDAR components: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
