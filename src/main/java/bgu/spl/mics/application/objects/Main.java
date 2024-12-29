package bgu.spl.mics.application.objects;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Path to the configuration file
        String configFilePath = "configuration_file.json";

        // Initialize cameras from the configuration file
        List<Camera> cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);

        // Update cameras with stamped detected objects from the camera data JSON file
        CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);

        // Print updated cameras
        for (Camera camera : cameras) {
            System.out.println("hi ido");
            System.out.println(camera.getDetectedObjectAtTimeT()DetectedObjectAtTimeT(2));
            System.out.println("Camera ID: " + camera.getId());
            System.out.println("Detected Objects: " + camera.getDetectedObjectsList());
        }
    }
}
