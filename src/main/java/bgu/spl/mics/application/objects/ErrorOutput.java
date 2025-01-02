package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorOutput {

    private String error; //A description of the source of the ERROR
    private String faultySensor; //The sensor that causes that caused the error
    private  ConcurrentHashMap<String, StampedDetectedObjects> lastFramesCameras;
    private  ConcurrentHashMap<String, List<TrackedObject>> lastFramesLiDars;
    private Pose[]poses;

    // Constructor to initialize all fields
    public ErrorOutput() {
        this.error = "";
        this.faultySensor = "";
        this.lastFramesCameras = new ConcurrentHashMap<String, StampedDetectedObjects>();
        this.lastFramesLiDars = new ConcurrentHashMap<String, List<TrackedObject>>();
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

    public void addCameraFrame (String name,StampedDetectedObjects object){
        this.lastFramesCameras.put(name, object);
    }
    public Pose[] getPoses() {
        return poses;
    }

    public void setPoses(Pose[] poses) {
        this.poses = poses;
    }

}//gjh
