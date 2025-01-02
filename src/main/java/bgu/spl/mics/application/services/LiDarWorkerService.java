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
            complete(event, lidarTracker.getLastTrackedObjects());
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints().get() <= 0) {
                System.out.println("FININSHED ALL OF THE LIDAR WORK" + LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints().get());
                sendBroadcast(new TerminatedBroadcast("LiDarService"));
                terminate();
            } else {
                lidarTracker.updateCurrentTick(tick.getCurrentTick());

                // Process events and send them directly from the microservice
                lidarTracker.getReadyEvents().forEach(event -> {
                    sendEvent(event);
                    int numberOfTrackedObjectsInEvent = event.getTrackedObjects().size();

                    // Atomically decrement the counter
                    LiDarDataBase dbInstance = LiDarDataBase.getInstance();
                    dbInstance.setCounterOfTrackedCloudPoints(dbInstance.getCounterOfTrackedCloudPoints().get() - numberOfTrackedObjectsInEvent);

                    System.out.println(getName() + " sent TrackedObjectsEvent at tick " + tick.getCurrentTick());
                });

            }


        });
        subscribeEvent(DetectObjectsEvent.class, event -> {
            System.out.println(getName() + " received DetectObjectsEvent at tick " + event.getTime());
            lidarTracker.addDetectedObjectsEvent(event);
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            lidarTracker.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(("LiDarService")));
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            if (broadcast.getSender().equals("TimeService")||broadcast.getSender().equals("LiDarService")) {
                lidarTracker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(("LiDarService")));
                terminate();
            }
        });
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();

    }
}