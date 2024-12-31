package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseEvent provides the robot's current pose for a given tick.
 */
public class PoseEvent implements Event<Boolean> {
    private final Pose pose;

    public PoseEvent(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose() {
        return pose;
    }
}
