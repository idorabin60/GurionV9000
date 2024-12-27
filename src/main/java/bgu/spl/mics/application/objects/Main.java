package bgu.spl.mics.application.objects;

import com.google.gson.Gson;

import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <config_file_path>");
            return;
        }

        String configFilePath = args[0];

        try {
            // Parse the config file
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(configFilePath)) {
                Configuration config = gson.fromJson(reader, Configuration.class);

                // Extract camera configurations
                List<CameraConfiguration> cameraConfigs = config.getCameras().getCamerasConfigurations();
                String cameraDataPath = config.getCameras().getCameraDatasPath();

                // Create and load cameras
                for (CameraConfiguration camConfig : cameraConfigs) {
                    Camera camera = new Camera(camConfig.getId(), camConfig.getFrequency(), STATUS.UP);
                    CameraDataLoader.loadCameraData(camera, cameraDataPath);

                    // Print camera details
                    System.out.println(camera);

                    // Test specific ticks
                    System.out.println("Objects at tick 2: " + camera.getDetectedObjectsAtTick(2));
                    System.out.println("Objects at tick 4: " + camera.getDetectedObjectsAtTick(4));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
