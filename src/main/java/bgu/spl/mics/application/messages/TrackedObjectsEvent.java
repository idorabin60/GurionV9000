package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * TrackedObjectsEvent represents an event containing a list of tracked objects
 * that have been processed by the LiDAR system.
 */
public class TrackedObjectsEvent implements Event<Boolean> {
    private final List<TrackedObject> trackedObjects;

    /**
     * Constructor for TrackedObjectsEvent.
     *
     * @param trackedObjects List of TrackedObject instances processed by the LiDAR system.
     */
    public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {
        this.trackedObjects = trackedObjects;
    }

    /**
     * Retrieves the list of tracked objects in this event.
     *
     * @return List of tracked objects.
     */
    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    @Override
    public String toString() {
        return "TrackedObjectsEvent{" +
                "trackedObjects=" + trackedObjects +
                '}';
    }
}
