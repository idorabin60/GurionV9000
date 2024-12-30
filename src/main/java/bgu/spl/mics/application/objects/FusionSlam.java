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
        private List<Pose> poses;     // Represents previous poses needed for calculations

        // Constructor
        public FusionSlam(ArrayList<LandMark> landmarks, List<Pose> poses) {
            this.landmarks = landmarks;
            this.poses = poses;
        }

        // Getters and Setters
        public ArrayList<LandMark> getLandmarks() {
            return landmarks;
        }

        public void setLandmarks(ArrayList<LandMark> landmarks) {
            this.landmarks = landmarks;
        }

        public List<Pose> getPoses() {
            return poses;
        }

        public void setPoses(List<Pose> poses) {
            this.poses = poses;
        }

//        @Override
//        public String toString() {
//            return "FusionSLAM{" +
//                    "landmarks=" + Arrays.toString(landmarks) +
//                    ", poses=" + poses +
//                    '}';
//        }
    }