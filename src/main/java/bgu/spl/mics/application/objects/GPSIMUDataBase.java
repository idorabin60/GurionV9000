package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * GPSIMUDataBase is a singleton class responsible for managing the robot's GPS and IMU data.
 * It initializes and populates the GPSIMU's PoseList from a JSON file.
 */
public class GPSIMUDataBase {
    private GPSIMU gpsimu; // The GPSIMU instance managed by this database

    // Private constructor to prevent direct instantiation
    private GPSIMUDataBase() {
        gpsimu = new GPSIMU(); // Create a new GPSIMU instance
    }

    // Static inner class to hold the Singleton instance
    private static class SingletonHolder {
        private static final GPSIMUDataBase INSTANCE = new GPSIMUDataBase();
    }

    // Public method to get the Singleton instance
    public static GPSIMUDataBase getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // Method to initialize the GPSIMUDataBase with data from the configuration file
    public void initialize(String configFilePath) {
        try (FileReader configReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();

            // Parse the main config file
            JsonObject config = gson.fromJson(configReader, JsonObject.class);
            String poseDataPath = config.get("poseJsonFile").getAsString().substring(1);

            // Load the Pose data from the specified path
            loadPoseData(configFilePath.substring(0,configFilePath.lastIndexOf('/'))+poseDataPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + configFilePath, e);
        }
    }

    // Loads the Pose data from the specified file into the GPSIMU's PoseList
    private void loadPoseData(String poseDataPath) {
        try (FileReader poseReader = new FileReader(poseDataPath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Pose>>() {}.getType();

            ArrayList<Pose> poseList = gson.fromJson(poseReader, listType);

            // Set the PoseList field in the GPSIMU instance
            gpsimu.setPoseList(poseList);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load GPS/IMU Pose data from file: " + poseDataPath, e);
        }
    }

    // Provides access to the GPSIMU instance
    public GPSIMU getGPSIMU() {
        return gpsimu;
    }
}
