package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public enum Status {
    Up, Down, Error
}

public class Camera {
    private int id;
    private int frequency;
    private Status status;
    private ArrayList<StampedDetectedObjects> detectedObjectsList;

    public Camera(int id, int frequency, Status status, ArrayList<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = detectedObjectsList;
    }

    public int getId() {
        return this.id;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public Status getStatus() {
        return this.status;
    }

    // TODO: Define fields and methods.
}
