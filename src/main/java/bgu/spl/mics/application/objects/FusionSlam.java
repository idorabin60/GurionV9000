package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
        // Fields
        private ArrayList<LandMark> landmarks; // Represents the map of the environment
        private ArrayList<Pose> poses;     // Represents previous poses needed for calculations


        // Constructor
        public FusionSlam(){
            landmarks = new ArrayList<LandMark>();
            poses = new ArrayList<Pose>();
        }

        //Singleton
        private static class FusionSlamHolder{
            private static final FusionSlam instance = new FusionSlam();
        }
        public static FusionSlam getInstance(){
            return FusionSlamHolder.instance;
        }

        // Getters and Setters
        public void addPose(Pose pose) {
            poses.add(pose);
        }
        public Pose getPose(int time){
            try {
                return poses.get(time - 1);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    //Return if there is LankMark like this and null otherwise
    public LandMark getLankMark (String id){
            for (LandMark l : landmarks){
                if (l.getId().equals(id))
                    return l;
            }
            return null;
    }
    public void addLankMark(LandMark l){
            landmarks.add(l);
    }


    //Calculation methods

    private double calcGlobalX(CloudPoint c, Pose pose) {
        double x = c.getX();
        double y = c.getY();
        double rad = Math.toRadians(pose.getYaw());
        return Math.cos(rad) * x - Math.sin(rad) * y + pose.getX();
    }

    private double calcGlobalY(CloudPoint c, Pose pose) {
        double x = c.getX();
        double y = c.getY();
        double rad = Math.toRadians(pose.getYaw());
        return Math.sin(rad) * x + Math.cos(rad) * y + pose.getY();
    }

    private void trackedObjectToGlobal(TrackedObject t, Pose pose) {
        t.getCoordinates().forEach(c -> {
            CloudPoint temp = new CloudPoint(c.getX(), c.getY());
            c.setX(calcGlobalX(temp, pose));
            c.setY(calcGlobalY(temp, pose));
        });
    }

    //Functional methods

    //There is landMark like this - only update the coordinates.
    public void updateLandmarkCoordinates(LandMark l, TrackedObject t) {
        List<CloudPoint> landmarkCoords = l.getCoordinates();
        List<CloudPoint> trackedCoords = t.getCoordinates();

        for (int i = 0; i < trackedCoords.size(); i++) {
            CloudPoint trackedPoint = trackedCoords.get(i);
            if (i < landmarkCoords.size()) {
                CloudPoint landmarkPoint = landmarkCoords.get(i);
                landmarkPoint.setX((landmarkPoint.getX() + trackedPoint.getX()) / 2);
                landmarkPoint.setY((landmarkPoint.getY() + trackedPoint.getY()) / 2);
            } else {
                landmarkCoords.add(trackedPoint);
            }
        }
    }

    }