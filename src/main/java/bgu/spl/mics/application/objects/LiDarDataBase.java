package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;
    private AtomicInteger counterOfTrackedCloudPoints = new AtomicInteger(0);

    // Private constructor to prevent instantiation
    private LiDarDataBase(String lidarDataPath) {
        loadLidarData(lidarDataPath);
    }

    // Static holder for the singleton instance
    private static class SingletonHolder {
        private static LiDarDataBase instance;

        private static void initialize(String lidarDataPath) {
            if (instance == null) {
                instance = new LiDarDataBase(lidarDataPath);
            }
        }
    }

    public static LiDarDataBase getInstance() {
        if (SingletonHolder.instance == null) {
            throw new IllegalStateException("LiDarDataBase not initialized. Call initialize() first.");
        }
        return SingletonHolder.instance;
    }

    public static LiDarDataBase getInstance(String lidarDataPath) {
        SingletonHolder.initialize(lidarDataPath);
        return SingletonHolder.instance;
    }

    private void loadLidarData(String lidarDataPath) {
        try (FileReader lidarReader = new FileReader(lidarDataPath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();

            // Parse the JSON array into a list of StampedCloudPoints
            cloudPoints = gson.fromJson(lidarReader, listType);

            // Validate the loaded data
            if (cloudPoints == null || cloudPoints.isEmpty()) {
                throw new RuntimeException("LiDAR data is empty or invalid");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load LiDAR data from file: " + lidarDataPath, e);
        }
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    @Override
    public String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }

    public boolean hasData(String objectId, int time) {
        return cloudPoints.stream()
                .anyMatch(entry -> entry.getId().equals(objectId) && entry.getTime() == time);
    }
    public void  setCounterOfTrackedCloudPoints(int counterOfTrackedCloudPoints) {
        this.counterOfTrackedCloudPoints.set(counterOfTrackedCloudPoints);
    }
    public AtomicInteger getCounterOfTrackedCloudPoints() {
        return counterOfTrackedCloudPoints;
    }
}
