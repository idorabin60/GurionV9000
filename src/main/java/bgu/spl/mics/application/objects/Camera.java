package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id;
    private final int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList;
    private int lifeCycle;

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
        this.lifeCycle = 0;
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Getter for frequency
    public int getFrequency() {
        return frequency;
    }

    // Getter for status
    public STATUS getStatus() {
        return status;
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(int lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    // Setter for status
    public void setStatus(STATUS status) {
        this.status = status;
    }

    // Getter for detectedObjectsList
    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public StampedDetectedObjects getDetectedObjectAtTimeT(int timeT) {
        if (timeT < 0) {
            return null;
        }
        for (StampedDetectedObjects detectedObject : detectedObjectsList) {
            if (detectedObject.getTime() == timeT) {
                return detectedObject;
            }
        }
        return null;
    }
    // Setter for detectedObjectsList


    // Method to add a single detected object

    public void updateDetectedObjects(List<StampedDetectedObjects> newDetectedObjects) {
        if (newDetectedObjects == null) {
            throw new IllegalArgumentException("Detected objects list cannot be null");
        }
        this.detectedObjectsList = newDetectedObjects;
        this.lifeCycle = newDetectedObjects.size();
    }
    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", detectedObjectsList=" + detectedObjectsList +
                ", lifeCycle=" + lifeCycle +
                '}';
    }

}
