import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class FusionSlamTest {
    private FusionSlam fusionSlam;
    private List<CloudPoint> cloudPoints1;
    private List<CloudPoint> cloudPoints2;
    private List<CloudPoint> cloudPoints3;
    private List<CloudPoint> cloudPoints4;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.getLandmarks().clear();

        cloudPoints1 = new ArrayList<CloudPoint>();
        cloudPoints1.add(new CloudPoint(0.1176, 3.6969));
        cloudPoints2 =new ArrayList<CloudPoint>();
        cloudPoints2.add(new CloudPoint(0.5,3.9));
        cloudPoints2.add(new CloudPoint(0.2, 3.7));
        cloudPoints3 = new ArrayList<CloudPoint>();
        cloudPoints3.add(new CloudPoint(3.1, -0.4));
        cloudPoints4=new ArrayList<CloudPoint>();
        cloudPoints4.add(new CloudPoint(-3.6, -1.0));
        cloudPoints4.add(new CloudPoint(-3.7, -1.1));

        fusionSlam.addPose(new Pose(2, 3, -1, 1));
        fusionSlam.addPose(new Pose(-3.2076f, 0.0755f,-87.48f,2));
        fusionSlam.addPose(new Pose( -5.7074f,0.1484f,-92.68f,3 ));

    }

    @Test
    void testTrackedObjectToGlobalAndUpdateTheLandmarks() {
        TrackedObject trackedObject1 = new TrackedObject("Wall_1", 1, "Wall", (ArrayList<CloudPoint>) cloudPoints1);
        fusionSlam.trackedObjectToGlobal(trackedObject1, fusionSlam.getPose(1));
        assertEquals(2.182101890308385, trackedObject1.getCoordinates().get(0).getX());
        assertEquals(6.694284541226638, trackedObject1.getCoordinates().get(0).getY());
    }
    @Test
    void testUpdateLandMarks() {
        TrackedObject trackedObject2 = new TrackedObject("Wall_2", 2, "Wall near door", (ArrayList<CloudPoint>) cloudPoints2);

        fusionSlam.trackedObjectToGlobal(trackedObject2, fusionSlam.getPose(2));
        fusionSlam.addLankMark(new LandMark(trackedObject2.getId(), trackedObject2.getDescription(), trackedObject2.getCoordinates()));
        assertNotNull(fusionSlam.getLankMark("Wall_2"));
        assertEquals(0.710612368454115, fusionSlam.getLankMark("Wall_2").getCoordinates().get(0).getX());
        assertEquals(0.49761536306778176, fusionSlam.getLankMark("Wall_2").getCoordinates().get(1).getX());

    }

    @Test
    void testTrackedObjectToGlobal_CorrectTransformation() {
        TrackedObject trackedObject3 = new TrackedObject("Wall_3", 3, "Wall3", (ArrayList<CloudPoint>) cloudPoints3);
        fusionSlam.trackedObjectToGlobal(trackedObject3, fusionSlam.getPose(3));
        for (CloudPoint point: trackedObject3.getCoordinates()) {
            assertNotEquals(2.182101890308385, point.getX());
            assertNotEquals(6.694284541226638, point.getY());
        }
    }
}