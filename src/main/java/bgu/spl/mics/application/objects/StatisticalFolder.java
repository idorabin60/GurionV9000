package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * we decided to do this class a singleton
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
        // Fields
    //update - runtime and tracked not atomic
        private AtomicInteger systemRuntime;       // The total runtime of the system, measured in ticks
        private AtomicInteger numDetectedObjects; // The cumulative count of objects detected by cameras
        private AtomicInteger numTrackedObjects;  // The cumulative count of objects tracked by LiDAR workers
        private AtomicInteger numLandmarks;       // The total number of unique landmarks identified
    // Singleton pattern
    private static class SingletonHolder {
        private static final StatisticalFolder instance = new StatisticalFolder();
    }

    public static StatisticalFolder getInstance() {
        return SingletonHolder.instance;
    }

    // Constructor
    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    // Methods to update metrics

    /**
     * Updates the system runtime.
     * @param ticks The number of ticks to add to the system runtime.
     */
    public void setSystemRuntime(int ticks) {
        systemRuntime.set(ticks);
    }

    /**
     * Increments the number of detected objects.
     * @param count The number of detected objects to add.
     */
    public void incrementDetectedObjects(int count) {
        numDetectedObjects.addAndGet(count);
    }

    /**
     * Increments the number of tracked objects.
     * @param count The number of tracked objects to add.
     */
    public void incrementTrackedObjects(int count) {
        numTrackedObjects.addAndGet(count);
    }
    public void setNumLandmarks(int landmarks) {
        numLandmarks.addAndGet(landmarks);
    }
    public int getNumLandmarks() {
        return this.numLandmarks;
    }


    // Getters

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }


    @Override
    public String toString() {
        return "StatisticalFolder{" +
                "systemRuntime=" + systemRuntime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandmarks=" + numLandmarks +
                '}';
    }
}
