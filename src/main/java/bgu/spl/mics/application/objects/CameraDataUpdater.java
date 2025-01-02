package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraDataUpdater {
    /**
     * Reads the configuration file and initializes a list of cameras based on the configurations.
     *
     * @param configFilePath Path to the configuration file.
     * @return A list of initialized Camera objects.
     */
    public static List<Camera> initializeCamerasFromConfig(String configFilePath) {
        try (FileReader configReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();

            // Parse the configuration file
            JsonObject config = gson.fromJson(configReader, JsonObject.class);
            JsonObject camerasConfig = config.getAsJsonObject("Cameras");
            Type cameraConfigListType = new TypeToken<List<JsonObject>>() {}.getType();

            // Get the list of camera configurations
            List<JsonObject> cameraConfigs = gson.fromJson(
                    camerasConfig.get("CamerasConfigurations"), cameraConfigListType);

            // Initialize cameras
            List<Camera> cameras = new ArrayList<>();
            for (JsonObject cameraConfig : cameraConfigs) {
                int id = cameraConfig.get("id").getAsInt();
                int frequency = cameraConfig.get("frequency").getAsInt();
                cameras.add(new Camera(id, frequency, STATUS.UP));
            }

            return cameras;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + configFilePath, e);
        }
    }

    /**
     * Updates the cameras with detected objects from the camera data JSON file.
     *
     * @param cameras        List of Camera objects to be updated.
     * @param configFilePath Path to the configuration file containing camera data path.
     */
    public static void updateCamerasFromJson(List<Camera> cameras, String configFilePath) {
        try (FileReader configReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();

            // Parse the configuration file to get the camera data path
            JsonObject config = gson.fromJson(configReader, JsonObject.class);
            String cameraDataPath = config.getAsJsonObject("Cameras").get("camera_datas_path").getAsString();

            // Read and parse the camera data JSON file
            try (FileReader cameraDataReader = new FileReader(cameraDataPath)) {
                JsonObject cameraDataJson = gson.fromJson(cameraDataReader, JsonObject.class);

                // Process each camera key
                for (Camera camera : cameras) {
                    String cameraKey = "camera" + camera.getId(); // Match keys like "camera1"
                    if (cameraDataJson.has(cameraKey)) {
                        // Flatten the nested arrays
                        List<List<StampedDetectedObjects>> nestedLists = gson.fromJson(
                                cameraDataJson.getAsJsonArray(cameraKey),
                                new TypeToken<List<List<StampedDetectedObjects>>>() {}.getType()
                        );

                        List<StampedDetectedObjects> flattenedList = new ArrayList<>();
                        for (List<StampedDetectedObjects> innerList : nestedLists) {
                            flattenedList.addAll(innerList);
                        }

                        // Update the camera with the flattened list
                        camera.updateDetectedObjects(flattenedList);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update cameras from JSON: " + e.getMessage(), e);
        }
    }

}
