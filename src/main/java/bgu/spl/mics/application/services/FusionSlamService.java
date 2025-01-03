package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
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
    private AtomicInteger numsOfMainService; //the shelter of TimeService and PoseService
    private boolean thereIsError;

    public FusionSlamService(FusionSlam fusionSlam,int numberOfCameras, int numberOfLiDars) {
        super("FusionSlam");
        this.fusionSlam=fusionSlam.getInstance();
        this.numsOfLiDars =  new AtomicInteger(numberOfLiDars);
        this.numsOfCameras= new AtomicInteger(numberOfCameras);
        this.numsOfMainService = new AtomicInteger(2);
        thereIsError = false;
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
            StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
            for (TrackedObject object : trackedObjects){
                //convert coordinates to global
                if ( fusionSlam.getPose(object.getTime())!=null) {
                    fusionSlam.trackedObjectToGlobal(object, fusionSlam.getPose(object.getTime()));
                    LandMark landMarkIsExists = fusionSlam.getLankMark(object.getId());
                    if (landMarkIsExists == null) { //A new lankMark
                        fusionSlam.addLankMark(new LandMark(object.getId(), object.getDescription(), object.getCoordinates()));
                    } else { //Need to update coordinates
                        fusionSlam.updateLandmarkCoordinates(landMarkIsExists, object);
                    }
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
            //ASK IDO
            if (numsOfCameras.get()<=0 && numsOfLiDars.get()<=0 ){
                sendBroadcast(new TerminatedBroadcast("FusionSlamService"));
            }
            else {
                StatisticalFolder.getInstance().setSystemRuntime(tick.getCurrentTick());
            }
        });

        //Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast termBrocast) -> {
            System.out.println(termBrocast.getSender());
             if (termBrocast.getSender().equals("TimeService") || termBrocast.getSender().equals("PoseService") ) {
                this.numsOfMainService.addAndGet(-1);
            }
             else if (termBrocast.getSender().equals("CameraService")) {
                 numsOfCameras.addAndGet(-1);
             }
             else if (termBrocast.getSender().equals("LiDarService")){
                 numsOfLiDars.addAndGet(-1);
             }
            boolean isEmptyCamerasAndLidars= (numsOfCameras.get()<=0 && numsOfLiDars.get()<=0 );
            if (isEmptyCamerasAndLidars && numsOfMainService.get()==0){
                StatisticalFolder.getInstance().setNumLandmarks(fusionSlam.getLandmarks().size());
                System.out.println(StatisticalFolder.getInstance().toString());
                terminate();
            }
            else {
                try {
                    Thread.sleep(500);
                    sendBroadcast(new TerminatedBroadcast("FusionSlamService"));

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        // Subscribe to crashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, terminate -> {
             if (terminate.getSender().equals("CameraService")) {
                numsOfCameras.addAndGet(-1);
            }
            else {
                numsOfLiDars.addAndGet(-1);
            }
        });
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();

    }
}
