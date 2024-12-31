package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private FusionSlam fusionSlam;
    private AtomicInteger numsOfCameras;
    private AtomicInteger numsOfLiDars;

    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam=fusionSlam.getInstance();
        this.numsOfLiDars =  new AtomicInteger(0);
        this.numsOfCameras= new AtomicInteger(0);
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        //Handle TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent event) -> {
            List<TrackedObject> trackedObjects = event.getTrackedObjects();
            for (TrackedObject object : trackedObjects){
                //convert coordinates to global
                fusionSlam.trackedObjectToGlobal(object,fusionSlam.getPose(object.getTime()));
                LandMark landMarkIsExists = fusionSlam.getLankMark(object.getId());
                if (landMarkIsExists==null){ //A new lankMark
                    fusionSlam.addLankMark(new LandMark(object.getId(), object.getDescription(), object.getCoordinates()));
                    statisticalFolder.incrementDetectedObjects(1);
                }
                else{ //Need to update coordinates
                    fusionSlam.updateLandmarkCoordinates(landMarkIsExists,object);
                }
            }
            complete(event, true);
        });

        //Handle PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent event) -> {
            fusionSlam.addPose(event.getPose());
            complete(event, event.getPose());
        });

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (numsOfCameras.get()<=0 && numsOfLiDars.get()<=0 ){
                sendBroadcast(new TerminatedBroadcast("FusionSlamService"));
            }
            else {
                statisticalFolder.setSystemRuntime(tick.getCurrentTick());
            }
        });

        //Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast termBrocast) -> {
            //NEED TO ASK IDO
            if (termBrocast.getSender().equals("TimeService")) {
                ///NEED TO COMPLETE
                terminate();
            }
            else if (termBrocast.getSender().equals("Camera"))
                numsOfCameras.addAndGet(-1);
            else if (termBrocast.getSender().equals("LiDar"))
                numsOfLiDars.addAndGet(-1);

        });

        // Subscribe to crashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, terminate -> {
            // DO WITH IDO - NEED TO END THE PROGRAM
            terminate();
        });

    }
}
