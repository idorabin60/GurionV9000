package bgu.spl.mics.application.objects;

public class ErrorOutput {

    private String error; //A description of the source of the ERROR
    private String faultySensor; //The sensor that causes that caused the error
    private String cameraLastFrameName;
    private StampedDetectedObjects lastCamerasFrame; //The last frame detected by Camera
    private String lidarLastFrameName;
    private StampedCloudPoints lastLiDarWorkerTrackersFrame; //the last frame detected by Lidar
    private Pose[]poses;
    private StatisticalFolder statistics;

    // Constructor to initialize all fields
    public ErrorOutput(String error, String faultySensor, String cameraLastFrameName,
                       StampedDetectedObjects lastCamerasFrame, String lidarLastFrameName,
                       StampedCloudPoints lastLiDarWorkerTrackersFrame, Pose[] poses,
                       StatisticalFolder statistics) {
        this.error = error;
        this.faultySensor = faultySensor;
        this.cameraLastFrameName = cameraLastFrameName;
        this.lastCamerasFrame = lastCamerasFrame;
        this.lidarLastFrameName = lidarLastFrameName;
        this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
        this.poses = poses;
        this.statistics = statistics;
    }
}
