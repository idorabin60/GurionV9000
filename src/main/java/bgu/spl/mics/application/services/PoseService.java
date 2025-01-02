package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    private GPSIMU gpsimu;

    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        // TODO Implement this
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        gpsimu.setStatus(STATUS.UP);
        //Subscribe to tickBrodcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (tick.getCurrentTick()<gpsimu.getPoseList().size()) {
                int currentTick = tick.getCurrentTick();
                gpsimu.setCurrentTick(currentTick);
                Pose currentPose = gpsimu.getPose(currentTick);
                // Retrieve the pose for the current tick
                if (currentPose != null) {
                    // Create and send a PoseEvent with the current pose
                    sendEvent(new PoseEvent(currentPose));
                    System.out.println(getName() + " sent PoseEvent at tick " + currentTick);
                } else {
                    System.out.println(getName() + " found no pose for tick " + currentTick);
                }
            }
            else {
                //There is no data in pose
                gpsimu.setStatus(STATUS.DOWN);
                terminate();
                sendBroadcast(new TerminatedBroadcast("PoseService"));
            }

        });

        // Subscribe to crashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, terminate -> {
            ErrorOutput.getInstance().setPoses(this.gpsimu.getPoseListByTime(this.gpsimu.getCurrentTick()));
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(("PoseService")));
            terminate();
        });

        //Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast termBrocast) -> {
            if (termBrocast.getSender().equals("TimeService")||termBrocast.getSender().equals("FusionSlamService")) {
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(("PoseService")));
                terminate();
            }
        });
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();
    }
}
