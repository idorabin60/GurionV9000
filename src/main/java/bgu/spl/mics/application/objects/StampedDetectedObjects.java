package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time; // The tick timestamp
    private List<DetectedObject> detectedObjects; // List of detected objects

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }
    // Getters
    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    @Override
    public String toString() {
        return "StampedDetectedObjects{time=" + time + ", detectedObjects=" + detectedObjects + '}';
    }
}