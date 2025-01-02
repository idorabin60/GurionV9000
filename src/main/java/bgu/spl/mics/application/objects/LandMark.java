package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private  String id;
    private  String description;
    private  ArrayList<CloudPoint> coordinates;

    public LandMark(String id, String description, ArrayList<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<CloudPoint> coordinates) {
        this.coordinates = coordinates;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LandMark { ")
                .append("id='").append(id).append('\'')
                .append(", description='").append(description).append('\'')
                .append(", coordinates=[");

        for (int i = 0; i < coordinates.size(); i++) {
            sb.append(coordinates.get(i).toString());
            if (i < coordinates.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("] }");

        return sb.toString();
    }
}