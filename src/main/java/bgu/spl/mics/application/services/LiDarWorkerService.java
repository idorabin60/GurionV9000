package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;

public class LiDarWorkerService extends MicroService {
    private final LiDarWorkerTracker lidarTracker;
    private boolean isErrorDetected;

    public LiDarWorkerService(LiDarWorkerTracker lidarTracker) {
        super("LiDarWorker" + lidarTracker.getId());
        this.lidarTracker = lidarTracker;
        this.isErrorDetected = false;
    }

    @Override
    protected void initialize() {
        lidarTracker.setStatus(STATUS.UP);

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            System.out.println(getName() + " received DetectObjectsEvent at tick " + event.getTime());
            lidarTracker.addDetectedObjectsEvent(event);
            processAndBroadcastEvents();
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (LiDarDataBase.getInstance().getCounterOfTrackedCloudPoints().get() <= 0) {
                System.out.println("FINISHED ALL OF THE LIDAR WORK");
                System.out.println("LastTrackedObjectsList = " + lidarTracker.getLastTrackedObjects());
                sendBroadcast(new TerminatedBroadcast("LiDarService"));
                terminate();
            } else {
                lidarTracker.updateCurrentTick(tick.getCurrentTick());
                processAndBroadcastEvents();
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            lidarTracker.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast("LiDarService"));
            System.out.println("LastTrackedObjectsList = " + lidarTracker.getLastTrackedObjects());
            ErrorOutput.getInstance().addLiDarFrame(this.getName(), this.lidarTracker.getLastTrackedObjects());
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            if (broadcast.getSender().equals("TimeService") || broadcast.getSender().equals("LiDarService")) {
                lidarTracker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast("LiDarService"));
                System.out.println("LastTrackedObjectsList = " + lidarTracker.getLastTrackedObjects());
                terminate();
            }
        });

        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();
    }

    private void processAndBroadcastEvents() {
        lidarTracker.getReadyEvents().forEach(event -> {
            if (isErrorDetected) {
                return;
            }

            event.getTrackedObjects().forEach(trackedObject -> {
                if (trackedObject.getId().equals("ERROR")) {
                    ErrorOutput.getInstance().setError(this.getName() + " disconnected");
                    ErrorOutput.getInstance().setFaultySensor(this.getName());
                    ErrorOutput.getInstance().addLiDarFrame(this.getName(), this.lidarTracker.getLastTrackedObjects());
                    sendBroadcast(new CrashedBroadcast("LiDarService"));
                    lidarTracker.setStatus(STATUS.ERROR);

                    isErrorDetected = true;
                    MessageBusImpl.getInstance().setIsError(true);
                    terminate();
                } else {
                    lidarTracker.getLastTrackedObjects().clear();

                    // Create a deep copy of trackedObject
                    ArrayList<CloudPoint> copiedCoordinates = new ArrayList<>();
                    for (CloudPoint point : trackedObject.getCoordinates()) {
                        copiedCoordinates.add(new CloudPoint(point.getX(), point.getY()));
                    }
                    TrackedObject copiedObject = new TrackedObject(
                            trackedObject.getId(),
                            trackedObject.getTime(),
                            trackedObject.getDescription(),
                            copiedCoordinates
                    );

                    lidarTracker.getLastTrackedObjects().add(copiedObject);
                    System.out.println("look here. ido");
                    System.out.println(trackedObject.toString());
                }
            });

            if (!isErrorDetected) {
                sendEvent(event);
                int numberOfTrackedObjectsInEvent = event.getTrackedObjects().size();
                LiDarDataBase dbInstance = LiDarDataBase.getInstance();
                dbInstance.setCounterOfTrackedCloudPoints(dbInstance.getCounterOfTrackedCloudPoints().get() - numberOfTrackedObjectsInEvent);
                System.out.println(getName() + " processed and broadcasted events");
            }
        });
    }
}
