package bgu.spl.mics.application.objects;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "configuration_file.json"; // Replace with the correct path

        try {
            // Step 1: Initialize cameras from the configuration file
            System.out.println("Initializing cameras from configuration file...");
            List<Camera> cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);

            // Step 2: Update cameras with detected object data
            System.out.println("Updating cameras with detected object data...");
            CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);

            // Step 3: Print cameras and their detected objects to verify correctness
            System.out.println("\nLoaded Cameras:");
            for (Camera camera : cameras) {
                System.out.println("Camera ID: " + camera.getId());
                System.out.println("Frequency: " + camera.getFrequency());
                System.out.println("Detected Objects:");
                if (camera.getDetectedObjectsList() != null && !camera.getDetectedObjectsList().isEmpty()) {
                    for (StampedDetectedObjects detectedObject : camera.getDetectedObjectsList()) {
                        System.out.println(" - " + detectedObject);
                    }
                } else {
                    System.out.println(" No detected objects.");
                }
                System.out.println("--------------------");
            }
        } catch (RuntimeException e) {
            System.err.println("Error during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
