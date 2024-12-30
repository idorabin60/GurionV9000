package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

public class LiDarWorkerService extends MicroService {
    private final LiDarWorkerTracker lidarTracker;
    private final List<TrackedObjectsEvent> trackedObjectsEventList; // Stores created events
    private int currentTick; // Tracks the current time in ticks

    public LiDarWorkerService(LiDarWorkerTracker lidarTracker) {
        super("LiDarWorker" + lidarTracker.getId());
        this.lidarTracker = lidarTracker;
        this.trackedObjectsEventList = new ArrayList<>();
    }

    @Override
    protected void initialize() {
        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            LiDarDataBase db = LiDarDataBase.getInstance(); // Access the LiDAR database
            List<StampedCloudPoints> cloudPoints = db.getCloudPoints(); // Retrieve all cloud points
            List<TrackedObject> trackedObjects = new ArrayList<>(); // Store tracked objects

            // Iterate through detected objects in the event
            for (DetectedObject detectedObject : event.getDetectedObjects()) {
                String objectId = detectedObject.getId();
                int eventTime = event.getTime();

                // Find matching StampedCloudPoints from the database
                StampedCloudPoints matchingCloudPoints = null;
                for (StampedCloudPoints cloudPoint : cloudPoints) {
                    if (cloudPoint.getId().equals(objectId) && cloudPoint.getTime() == eventTime) {
                        matchingCloudPoints = cloudPoint;
                        break;
                    }
                }

                // If a match is found, create a TrackedObject
                if (matchingCloudPoints != null) {
                    List<List<Double>> points = matchingCloudPoints.getCloudPoints();

                    // Convert cloud points to ArrayList<CloudPoint>
                    ArrayList<CloudPoint> coordinates = new ArrayList<>();
                    points.forEach(point -> coordinates.add(new CloudPoint(point.get(0), point.get(1))));

                    // Create a TrackedObject
                    TrackedObject trackedObject = new TrackedObject(
                            objectId,
                            eventTime,
                            detectedObject.getDescription(),
                            coordinates // Pass as ArrayList<CloudPoint>
                    );
                    trackedObjects.add(trackedObject); // Add to the list
                }
            }

            // Create a TrackedObjectsEvent for these tracked objects and store it
            if (!trackedObjects.isEmpty()) {
                trackedObjectsEventList.add(new TrackedObjectsEvent(trackedObjects));
                System.out.println(getName() + " created TrackedObjectsEvent with " + trackedObjects.size() + " objects.");
            } else {
                System.out.println(getName() + " found no matching cloud points for event at time " + event.getTime());
            }
        });

        // Subscribe to TickBroadcast to handle event sending based on condition
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getCurrentTick();

            // Filter and send events that meet the condition
            trackedObjectsEventList.removeIf(event -> {
                // Retrieve the first TrackedObject's detection time for evaluation
                int detectionTime = event.getTrackedObjects().get(0).getTime();

                if (currentTick >= detectionTime + lidarTracker.getFrequency()) {
                    // Send the event
                    sendEvent(event);
                    System.out.println(getName() + " sent TrackedObjectsEvent at tick " + currentTick);
                    return true; // Remove event after processing
                }
                return false; // Keep event in the list if not ready
            });
        });
    }
} //pushing to main
