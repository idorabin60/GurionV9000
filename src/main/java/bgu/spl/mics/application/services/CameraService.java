package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

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
        camera.setStatus(STATUS.UP);

        //Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast termBrocast) -> {
            if (termBrocast.getSender().equals("TimeService") || termBrocast.getSender().equals("FusionSlamService")) {
                camera.setStatus(STATUS.DOWN);
                terminate();
            }
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            currentTick = tick.getCurrentTick();
            if (camera.getLifeCycle() < 1) {
                //if there is no data or the list is empty
                camera.setStatus(STATUS.DOWN);
                terminate();
                sendBroadcast(new TerminatedBroadcast("Camera"));
            } else { //there is data to read and status = up
                StampedDetectedObjects objectsAtTimeT = camera.getDetectedObjectAtTimeT(currentTick - camera.getFrequency());
                if (objectsAtTimeT == null) {
                    System.out.println(getName() + " found no detected objects at tick " + currentTick);
                    return; // Skip further processing
                }
                //iterate all the objects and see if there is an object of id:ERROR
                List<DetectedObject> objects = objectsAtTimeT.getDetectedObjects();
                for (DetectedObject obj : objects) {
                    if (obj.getId().equals("ERROR")) {
                        camera.setStatus(STATUS.ERROR);
                        //send crashed Broadcast
                        errorOutput.setError(obj.getDescription());
                        errorOutput.setFaultySensor(this.getName());
                        errorOutput.setCameraLastFrameName(this.getName());
                        sendBroadcast(new CrashedBroadcast(obj.getId(), obj.getDescription()));
                        break;
                    }
                }
                //there is no error
                // Send DetectObjectsEvent
                sendEvent(new DetectObjectsEvent(objectsAtTimeT));
                //decrease camera life cycle
                camera.setLifeCycle(camera.getLifeCycle() - 1);
                System.out.println(getName() + " sent DetectObjectsEvent at Tick " + currentTick + " for camera " + camera.getId());
                //update the statistical folder
                statisticalFolder.incrementDetectedObjects(objectsAtTimeT.getDetectedObjects().size());
            }
        });

        // Subscribe to crashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, terminate -> {
            camera.setStatus(STATUS.DOWN);
            terminate();
        });
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();

    }

}

