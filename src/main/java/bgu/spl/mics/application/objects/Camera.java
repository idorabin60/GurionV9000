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

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
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

    // Setter for status
    public void setStatus(STATUS status) {
        this.status = status;
    }

    // Getter for detectedObjectsList
    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    // Setter for detectedObjectsList
    public void setDetectedObjectsList(List<StampedDetectedObjects> detectedObjectsList) {
        this.detectedObjectsList = detectedObjectsList;
    }

    // Method to add a single detected object
    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }
}
