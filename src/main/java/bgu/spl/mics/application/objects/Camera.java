package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id; // Unique identifier for the camera
    private final int frequency; // Time interval in ticks
    private STATUS status; // Camera's operational status
    private final Map<Integer, List<DetectedObject>> detectedObjectsMap; // Map of tick to detected objects

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsMap = new HashMap<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public Map<Integer, List<DetectedObject>> getDetectedObjectsMap() {
        return detectedObjectsMap;
    }

    // Populate the map from a list of StampedDetectedObjects
    public void loadDetectedObjects(List<StampedDetectedObjects> stampedObjects) {
        for (StampedDetectedObjects entry : stampedObjects) {
            detectedObjectsMap.put(entry.getTime(), entry.getDetectedObjects());
        }
    }

    // Query detected objects by tick
    public List<DetectedObject> getDetectedObjectsAtTick(int tick) {
        return detectedObjectsMap.getOrDefault(tick, List.of());
    }

    @Override
    public String toString() {
        return "Camera{id=" + id + ", frequency=" + frequency + ", status=" + status +
                ", detectedObjectsMap=" + detectedObjectsMap + '}';
    }
    // TODO: Define fields and methods.
}
