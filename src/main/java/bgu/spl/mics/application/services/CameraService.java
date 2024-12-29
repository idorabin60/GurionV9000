package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * <p>
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera; // The associated camera
    private int currentTick;


    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera" + camera.getId());
        this.camera = camera;
        this.currentTick = 0;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        System.out.println(getName() + " for camera " + camera.getId() + " started");

        // Subscribe to TerminateBroadcast
//        subscribeBroadcast(TerminateBroadcast.class, terminate -> {
//            System.out.println(getName() + " received TerminateBroadcast");
//            terminate();
//        });
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            currentTick = tick.getCurrentTick();
            if (camera.getStatus() == STATUS.UP) {
                StampedDetectedObjects objectsAtTimeT = camera.getLastDetectedObjectAtTimeT(currentTick-camera.getFrequency());
                if (objectsAtTimeT!=null) {
                    // Send DetectObjectsEvent
                    Future<Boolean> future = sendEvent(new DetectObjectsEvent(objectsAtTimeT));
                    System.out.println(getName() + " sent DetectObjectsEvent at Tick " + currentTick + " for camera " + camera.getId());
                }
            }
        });
    }


    }

