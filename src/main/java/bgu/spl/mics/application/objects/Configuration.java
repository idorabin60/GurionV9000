package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

// Root class
class Configuration {
    @SerializedName("Cameras")
    private Cameras cameras;

    @SerializedName("LidarWorkers")
    private LidarWorkers lidarWorkers;

    @SerializedName("poseJsonFile")
    private String poseJsonFile;

    @SerializedName("TickTime")
    private int tickTime;

    @SerializedName("Duration")
    private int duration;

    // Getters and Setters
    public Cameras getCameras() { return cameras; }
    public void setCameras(Cameras cameras) { this.cameras = cameras; }

    public LidarWorkers getLidarWorkers() { return lidarWorkers; }
    public void setLidarWorkers(LidarWorkers lidarWorkers) { this.lidarWorkers = lidarWorkers; }

    public String getPoseJsonFile() { return poseJsonFile; }
    public void setPoseJsonFile(String poseJsonFile) { this.poseJsonFile = poseJsonFile; }

    public int getTickTime() { return tickTime; }
    public void setTickTime(int tickTime) { this.tickTime = tickTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}

// Cameras class
class Cameras {
    @SerializedName("CamerasConfigurations")
    private List<CameraConfiguration> camerasConfigurations;

    @SerializedName("camera_datas_path")
    private String cameraDatasPath;

    // Getters and Setters
    public List<CameraConfiguration> getCamerasConfigurations() { return camerasConfigurations; }
    public void setCamerasConfigurations(List<CameraConfiguration> camerasConfigurations) {
        this.camerasConfigurations = camerasConfigurations;
    }

    public String getCameraDatasPath() { return cameraDatasPath; }
    public void setCameraDatasPath(String cameraDatasPath) { this.cameraDatasPath = cameraDatasPath; }
}

// CameraConfiguration class
class CameraConfiguration {
    @SerializedName("id")
    private int id;

    @SerializedName("frequency")
    private int frequency;

    @SerializedName("camera_key")
    private String cameraKey;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public String getCameraKey() { return cameraKey; }
    public void setCameraKey(String cameraKey) { this.cameraKey = cameraKey; }
}

// LidarWorkers class
class LidarWorkers {
    @SerializedName("LidarConfigurations")
    private List<LidarConfiguration> lidarConfigurations;

    @SerializedName("lidars_data_path")
    private String lidarsDataPath;

    // Getters and Setters
    public List<LidarConfiguration> getLidarConfigurations() { return lidarConfigurations; }
    public void setLidarConfigurations(List<LidarConfiguration> lidarConfigurations) {
        this.lidarConfigurations = lidarConfigurations;
    }

    public String getLidarsDataPath() { return lidarsDataPath; }
    public void setLidarsDataPath(String lidarsDataPath) { this.lidarsDataPath = lidarsDataPath; }
}

// LidarConfiguration class
class LidarConfiguration {
    @SerializedName("id")
    private int id;

    @SerializedName("frequency")
    private int frequency;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
}

