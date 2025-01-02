package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private ArrayList<Pose> PoseList;

    public GPSIMU() {
        this.currentTick = 0;
        this.status =STATUS.UP; // check if true
        this.PoseList = new ArrayList<>();
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ArrayList<Pose> getPoseListByTime(int time) {
        return new ArrayList<>(PoseList.subList(0, time));
    }

    public ArrayList<Pose> getPoseList(){
        return PoseList;
    }

    public void setPoseList(ArrayList<Pose> poseList) {
        this.PoseList = poseList;
    }
    public Pose getPose(int pose) {
        return PoseList.get(pose);
    }
}