package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CameraDataLoader {
    public static void loadCameraData(Camera camera, String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Define the type for the JSON structure
            Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();

            // Parse the JSON into a map
            Map<String, List<StampedDetectedObjects>> data = gson.fromJson(reader, mapType);

            // Extract data for the specific camera by ID
            String cameraKey = "camera" + camera.getId(); // E.g., "camera1"
            if (data.containsKey(cameraKey)) {
                List<StampedDetectedObjects> stampedObjects = data.get(cameraKey);
                camera.loadDetectedObjects(stampedObjects); // Populate the camera map
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

