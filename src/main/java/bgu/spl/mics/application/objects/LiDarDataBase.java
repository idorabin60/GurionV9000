package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;

    // Private constructor to prevent instantiation
    private LiDarDataBase(String configFilePath) {
        parseConfigAndLoadData(configFilePath);
    }

    // Static holder for the singleton instance
    private static class SingletonHolder {
        private static LiDarDataBase instance;

        private static void initialize(String configFilePath) {
            if (instance == null) {
                instance = new LiDarDataBase(configFilePath);
            }
        }
    }

    // Public method to get the singleton instance
    public static LiDarDataBase getInstance(String configFilePath) {
        SingletonHolder.initialize(configFilePath);
        return SingletonHolder.instance;
    }

    // Parses the main config file and loads the LiDAR data
    private void parseConfigAndLoadData(String configFilePath) {
        try (FileReader configReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();

            // Parse the main config file
            JsonObject config = gson.fromJson(configReader, JsonObject.class);
            String lidarDataPath = config.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();

            // Load the LiDAR data from the specified path
            loadLidarData(lidarDataPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + configFilePath, e);
        }
    }

    // Loads the LiDAR data from the specified file
    private void loadLidarData(String lidarDataPath) {
        try (FileReader lidarReader = new FileReader(lidarDataPath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();

            cloudPoints = gson.fromJson(lidarReader, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load LiDAR data from file: " + lidarDataPath, e);
        }
    }

    // Getter for cloudPoints
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    @Override
    public String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }
}
