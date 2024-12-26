package bgu.spl.mics.application.objects;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
import java.util.ArrayList;

public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private final ArrayList<StampedDetectedObjects> detectedObjectsList;

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ArrayList<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public void addDetectedObjects(int time, ArrayList<DetectedObject> detectedObjects) {
        detectedObjectsList.add(new StampedDetectedObjects(time, detectedObjects));
    }

    @Override
    public String toString() {
        return "Camera{id=" + id + ", frequency=" + frequency + ", status=" + status + ", detectedObjectsList=" + detectedObjectsList + "}";
    }
}