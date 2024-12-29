package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.Arrays;
import  java.util.ArrayList;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    // Fields
    private String id;
    private int time;
    private String description;
    private ArrayList<cloudPoints> coordinates;

    // Constructor
    public TrackedObject(String id, int time, String description,ArrayList<cloudPoints> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<cloudPoints>  getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<cloudPoints>  coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "TrackedObject{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", coordinates=" + ArraysList.toString(coordinates) +
                '}';
    }
}
