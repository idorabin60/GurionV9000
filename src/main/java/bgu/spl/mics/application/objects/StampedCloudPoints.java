package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {

    // Fields
    private String id;
    private int time;
    private List<List<Double>> cloudPoints;

    // Constructor
    public StampedCloudPoints() {
        this.id = "";
        this.time = 0;
        this.cloudPoints = new ArrayList<List<Double>>();
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

    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }

    public void setCloudPoints(List<List<Double>> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

    @Override
    public String toString() {
        return "StampedCloudPoints{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", cloudPoints=" + cloudPoints +
                '}';
    }
}
