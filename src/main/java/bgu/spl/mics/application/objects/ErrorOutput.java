package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorOutput {

    private String error; // A description of the source of the ERROR
    private String faultySensor; // The sensor that caused the error
    private ConcurrentHashMap<String, StampedDetectedObjects> lastFramesCameras;
    private ConcurrentHashMap<String, List<TrackedObject>> lastFramesLiDars;
    private ArrayList<Pose> poses;

    // Constructor to initialize all fields
    public ErrorOutput() {
        this.error = "";
        this.faultySensor = "";
        this.lastFramesCameras = new ConcurrentHashMap<>();
        this.lastFramesLiDars = new ConcurrentHashMap<>();
        this.poses = new ArrayList<>();
    }

    // Singleton pattern
    private static class SingletonHolder {
        private static final ErrorOutput instance = new ErrorOutput();
    }

    public static ErrorOutput getInstance() {
        return ErrorOutput.SingletonHolder.instance;
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void addLiDarFrame(String name, List<TrackedObject> liDarFrame) {
        this.lastFramesLiDars.put(name, liDarFrame);
    }

    public void addCameraFrame(String name, StampedDetectedObjects object) {
        this.lastFramesCameras.put(name, object);
    }

    public void setPoses(ArrayList<Pose> currentPoses) {
        this.poses = currentPoses;
    }

    public ArrayList<Pose> getPoses() {
        return poses;
    }

    public void createErrorOutputFile() {
        System.out.println("Creating error output file LOOOOOKKK HEEEEREEE IDDOOO" );
        System.out.println(this.lastFramesLiDars.toString());
        Gson gson = new Gson();
        String filePath = "ido_rabin_error.json";

        StatisticalFolder stats = StatisticalFolder.getInstance();
        FusionSlam fusionSlam = FusionSlam.getInstance();

        try (FileWriter writer = new FileWriter(filePath)) {
            JsonObject output = new JsonObject();

            // Add faultySensor and error
            output.addProperty("faultySensor", this.faultySensor);
            output.addProperty("error", this.error);

            // Add lastCamerasFrame
            JsonObject camerasFrame = new JsonObject();
            this.lastFramesCameras.forEach((name, frame) -> camerasFrame.add(name, gson.toJsonTree(frame)));
            output.add("lastCamerasFrame", camerasFrame);

            // Add lastLiDarWorkerTrackersFrame
            JsonObject lidarFrame = new JsonObject();
            this.lastFramesLiDars.forEach((name, frames) -> lidarFrame.add(name, gson.toJsonTree(frames)));
            output.add("lastLiDarWorkerTrackersFrame", lidarFrame);

            // Add poses
            JsonArray posesArray = new JsonArray();
            this.poses.forEach(pose -> posesArray.add(gson.toJsonTree(pose)));
            output.add("poses", posesArray);

            // Add statistics
            JsonObject statistics = new JsonObject();
            statistics.addProperty("systemRuntime", stats.getSystemRuntime());
            statistics.addProperty("numDetectedObjects", stats.getNumDetectedObjects());
            statistics.addProperty("numTrackedObjects", stats.getNumTrackedObjects());
            statistics.addProperty("numLandmarks", stats.getNumLandmarks());

            // Add landmarks to statistics
            JsonObject landmarks = new JsonObject();
            fusionSlam.getLandmarks().forEach(landMark -> landmarks.add(landMark.getId(), gson.toJsonTree(landMark)));
            statistics.add("landMarks", landmarks);

            output.add("statistics", statistics);

            // Write JSON to file
            writer.write(gson.toJson(output));
            System.out.println("Error output file created successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write error output file: " + e.getMessage());
        }
    }
}