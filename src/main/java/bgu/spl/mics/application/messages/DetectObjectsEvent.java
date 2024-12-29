package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class DetectObjectsEvent implements Event<Boolean> {

    private final StampedDetectedObjects stampedDetectedObjectInCertinTimeByCertinCamera;

    public DetectObjectsEvent(StampedDetectedObjects stampedDetectedObjectInCertinTimeByCertinCamera) {
        this.stampedDetectedObjectInCertinTimeByCertinCamera = stampedDetectedObjectInCertinTimeByCertinCamera;
    }

    public List<DetectedObject> getDetectedObjects() {
        return stampedDetectedObjectInCertinTimeByCertinCamera.getDetectedObjects();
    }

    public int getTime() {
        return stampedDetectedObjectInCertinTimeByCertinCamera.getTime();
    }

}