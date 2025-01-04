package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusionSlam;
    private final AtomicInteger numsOfCameras;
    private final AtomicInteger numsOfLiDars;
    private final AtomicInteger numsOfMainService; // Tracks TimeService and PoseService
    private boolean thereIsError;

    // Pending list for tracked objects waiting for poses
    private final ConcurrentHashMap<Integer, List<TrackedObject>> pendingTrackedObjects;

    public FusionSlamService(FusionSlam fusionSlam, int numberOfCameras, int numberOfLiDars) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam.getInstance();
        this.numsOfLiDars = new AtomicInteger(numberOfLiDars);
        this.numsOfCameras = new AtomicInteger(numberOfCameras);
        this.numsOfMainService = new AtomicInteger(2); // TimeService and PoseService
        this.thereIsError = false;
        this.pendingTrackedObjects = new ConcurrentHashMap<>();
    }

    @Override
    protected void initialize() {

        // Handle TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            List<TrackedObject> trackedObjects = event.getTrackedObjects();
            StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());

            for (TrackedObject object : trackedObjects) {
                int time = object.getTime();
                Pose correspondingPose = fusionSlam.getPose(time);

                if (correspondingPose != null) {
                    fusionSlam.trackedObjectToGlobal(object, correspondingPose);
                    LandMark landMark = fusionSlam.getLankMark(object.getId());
                    if (landMark == null) {
                        fusionSlam.addLankMark(new LandMark(object.getId(), object.getDescription(), object.getCoordinates()));
                    } else {
                        fusionSlam.updateLandmarkCoordinates(landMark, object);
                    }
                } else {
                    pendingTrackedObjects.computeIfAbsent(time, k -> new ArrayList<>()).add(object);
                }
            }

            complete(event, true);
        });

        // Handle PoseEvent
        subscribeEvent(PoseEvent.class, event -> {
            Pose pose = event.getPose();
            int time = pose.getTime();
            fusionSlam.addPose(pose);

            List<TrackedObject> pendingObjects = pendingTrackedObjects.remove(time);
            if (pendingObjects != null) {
                for (TrackedObject object : pendingObjects) {
                    fusionSlam.trackedObjectToGlobal(object, pose);
                    LandMark landMark = fusionSlam.getLankMark(object.getId());
                    if (landMark == null) {
                        fusionSlam.addLankMark(new LandMark(object.getId(), object.getDescription(), object.getCoordinates()));
                    } else {
                        fusionSlam.updateLandmarkCoordinates(landMark, object);
                    }
                }
            }

            complete(event, pose);
        });

        // Handle TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (numsOfCameras.get() <= 0 && numsOfLiDars.get() <= 0) {
                sendBroadcast(new TerminatedBroadcast("FusionSlamService"));
            } else {
                StatisticalFolder.getInstance().setSystemRuntime(tick.getCurrentTick());
            }
        });

        // Handle TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, termBroadcast -> {
            if ("TimeService".equals(termBroadcast.getSender()) || "PoseService".equals(termBroadcast.getSender())) {
                numsOfMainService.decrementAndGet();
            } else if ("CameraService".equals(termBroadcast.getSender())) {
                numsOfCameras.decrementAndGet();
            } else if ("LiDarService".equals(termBroadcast.getSender())) {
                numsOfLiDars.decrementAndGet();
            }

            boolean noMoreCamerasOrLiDars = numsOfCameras.get() <= 0 && numsOfLiDars.get() <= 0;
            if (noMoreCamerasOrLiDars && numsOfMainService.get() == 0) {
                if (!fusionSlam.getLandmarks().isEmpty()) {
                    StatisticalFolder.getInstance().setNumLandmarks(fusionSlam.getLandmarks().size());
                }
                System.out.println(StatisticalFolder.getInstance());
                if (thereIsError) {
                    FusionSlam.getInstance().setThereIsError(true);
                    System.out.println("THE SENSOR OF ERROR: " + ErrorOutput.getInstance().getFaultySensor() +
                            " THE ERROR: " + ErrorOutput.getInstance().getError());
                }
                terminate();
            } else {
                sendBroadcast(new TerminatedBroadcast("FusionSlamService"));
            }
        });

        // Handle CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, crashBroadcast -> {
            thereIsError = true;
            if ("CameraService".equals(crashBroadcast.getSender())) {
                numsOfCameras.decrementAndGet();
            } else {
                numsOfLiDars.decrementAndGet();
            }
        });

        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();
    }
}
