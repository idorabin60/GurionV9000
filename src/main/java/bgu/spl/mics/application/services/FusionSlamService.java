package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.*;

import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    FusionSlam fusionSlam;
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam=fusionSlam;
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
            complete(event, true);
        });

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            //NEED TO ASK IDO IF TICK = 1
            statisticalFolder.incrementSystemRuntime(1);
        });

        //Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast termBrocast) -> {
            //NEED TO ASK IDO
            if (termBrocast.getSender().equals("TimeService")) {
                ///NEED TO COMPLETE
                terminate();
            }
        });

        // Subscribe to crashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, terminate -> {
            // DO WITH IDO - NEED TO END THE PROGRAM
            terminate();
        });

    }
}
