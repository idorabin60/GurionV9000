package bgu.spl.mics.application.objects;

public class ErrorOutput {

    private String error; //A description of the source of the ERROR
    private String faultySensor; //The sensor that causes that caused the error
    private String cameraLastFrameName;
    private StampedDetectedObjects lastCamerasFrame; //The last frame detected by Camera
    private String lidarLastFrameName;
    private StampedCloudPoints lastLiDarWorkerTrackersFrame; //the last frame detected by Lidar
    private Pose[]poses;

    // Constructor to initialize all fields
    public ErrorOutput() {
        this.error = "";
        this.faultySensor = "";
        this.cameraLastFrameName = "";
        this.lastCamerasFrame = null;
        this.lidarLastFrameName = "";
       this.lastLiDarWorkerTrackersFrame = new StampedCloudPoints();
       this.poses = null;
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

    public String getCameraLastFrameName() {
        return cameraLastFrameName;
    }

    public void setCameraLastFrameName(String cameraLastFrameName) {
        this.cameraLastFrameName = cameraLastFrameName;
    }

    public StampedDetectedObjects getLastCamerasFrame() {
        return lastCamerasFrame;
    }

    public void setLastCamerasFrame(StampedDetectedObjects lastCamerasFrame) {
        this.lastCamerasFrame = lastCamerasFrame;
    }

    public String getLidarLastFrameName() {
        return lidarLastFrameName;
    }

    public void setLidarLastFrameName(String lidarLastFrameName) {
        this.lidarLastFrameName = lidarLastFrameName;
    }

    public StampedCloudPoints getLastLiDarWorkerTrackersFrame() {
        return lastLiDarWorkerTrackersFrame;
    }

    public void setLastLiDarWorkerTrackersFrame(StampedCloudPoints lastLiDarWorkerTrackersFrame) {
        this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
    }

    public Pose[] getPoses() {
        return poses;
    }

    public void setPoses(Pose[] poses) {
        this.poses = poses;
    }

}
