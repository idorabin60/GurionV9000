package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

public class LiDarWorkerService extends MicroService {
    private final LiDarWorkerTracker lidarTracker;

    public LiDarWorkerService(LiDarWorkerTracker lidarTracker) {
        super("LiDarWorker" + lidarTracker.getId());
        this.lidarTracker = lidarTracker;
    }

    @Override
    protected void initialize() {
        lidarTracker.setStatus(STATUS.UP);

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            System.out.println(getName() + " received DetectObjectsEvent at tick " + event.getTime());
            lidarTracker.addDetectedObjectsEvent(event);
            complete(event,true);
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            lidarTracker.updateCurrentTick(tick.getCurrentTick());

            // Process events and send them directly from the microservice
            lidarTracker.getReadyEvents().forEach(event -> {
                sendEvent(event);
                System.out.println(getName() + " sent TrackedObjectsEvent at tick " + tick.getCurrentTick());
            });
        });
        subscribeEvent(DetectObjectsEvent.class, event -> {
            System.out.println(getName() + " received DetectObjectsEvent at tick " + event.getTime());
            lidarTracker.addDetectedObjectsEvent(event);
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            lidarTracker.setStatus(STATUS.DOWN);
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            if (broadcast.getSender().equals("TimeService") || broadcast.getSender().equals("FusionSlamService")) {
                lidarTracker.setStatus(STATUS.DOWN);
                terminate();
            }
        });
    }
}
