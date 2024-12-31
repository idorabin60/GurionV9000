package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    private List<TrackedObjectsEvent> trackedObjectsEventList;
    private int currentTick;




    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.lastTrackedObjects = new ArrayList<>();
        this.status = STATUS.UP;
        this.trackedObjectsEventList = new ArrayList<>();
        this.currentTick = 0;

    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }
    public void updateCurrentTick(int tick){
        this.currentTick = tick;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }

    public void addTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.add(trackedObject);
    }

    public void removeTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.remove(trackedObject);
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public List<TrackedObjectsEvent> getReadyEvents() {
        List<TrackedObjectsEvent> readyEvents = trackedObjectsEventList.stream()
                .filter(event -> currentTick >= event.getTrackedObjects().get(0).getTime() + frequency)
                .collect(Collectors.toList());

        // Remove sent events from the list
        trackedObjectsEventList.removeAll(readyEvents);

        return readyEvents;
    }
    public void addDetectedObjectsEvent(DetectObjectsEvent event) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        LiDarDataBase db = LiDarDataBase.getInstance();

        for (DetectedObject detectedObject : event.getDetectedObjects()) {
            String objectId = detectedObject.getId();
            int eventTime = event.getTime();

            StampedCloudPoints matchingCloudPoints = db.getCloudPoints().stream()
                    .filter(cp -> cp.getId().equals(objectId) && cp.getTime() == eventTime)
                    .findFirst()
                    .orElse(null);

            if (matchingCloudPoints != null) {
                ArrayList<CloudPoint> coordinates = new ArrayList<>();
                matchingCloudPoints.getCloudPoints().forEach(p -> coordinates.add(new CloudPoint(p.get(0), p.get(1))));

                trackedObjects.add(new TrackedObject(objectId, eventTime, detectedObject.getDescription(), coordinates));
            }
        }

        if (!trackedObjects.isEmpty()) {
            trackedObjectsEventList.add(new TrackedObjectsEvent(trackedObjects));
            System.out.println("Added TrackedObjectsEvent with " + trackedObjects.size() + " objects to the list.");
        }
    }
    public List<TrackedObjectsEvent> getTrackedObjectsEventList() {
        return trackedObjectsEventList;
    }

}