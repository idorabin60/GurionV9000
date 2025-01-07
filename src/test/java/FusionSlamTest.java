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
        // Precondition: FusionSlam instance must exist and be initialized as a singleton.
        // The landmarks list should be cleared to ensure a clean state for testing.
        // Postcondition: FusionSlam is initialized with cleared landmarks and predefined poses.
        // Cloud points are prepared for tracked object transformations.
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.getLandmarks().clear();

        cloudPoints1 = new ArrayList<>();
        cloudPoints1.add(new CloudPoint(0.1176, 3.6969));

        cloudPoints2 = new ArrayList<>();
        cloudPoints2.add(new CloudPoint(0.5, 3.9));
        cloudPoints2.add(new CloudPoint(0.2, 3.7));

        cloudPoints3 = new ArrayList<>();
        cloudPoints3.add(new CloudPoint(3.1, -0.4));

        cloudPoints4 = new ArrayList<>();
        cloudPoints4.add(new CloudPoint(-3.6, -1.0));
        cloudPoints4.add(new CloudPoint(-3.7, -1.1));

        fusionSlam.addPose(new Pose(2, 3, -1, 1));
        fusionSlam.addPose(new Pose(-3.2076f, 0.0755f, -87.48f, 2));
        fusionSlam.addPose(new Pose(-5.7074f, 0.1484f, -92.68f, 3));
    }

    /**
     * @inv Cloud point transformation logic is applied correctly.
     * @pre A tracked object (trackedObject1) exists with local cloud point coordinates.
     * @post The tracked object's cloud point coordinates are transformed into global coordinates
     *       based on the provided pose.
     */
    @Test
    void testTrackedObjectToGlobalAndUpdateTheLandmarks() {
        TrackedObject trackedObject1 = new TrackedObject("Wall_1", 1, "Wall", (ArrayList<CloudPoint>) cloudPoints1);
        fusionSlam.trackedObjectToGlobal(trackedObject1, fusionSlam.getPose(1));

        assertEquals(2.182101890308385, trackedObject1.getCoordinates().get(0).getX());
        assertEquals(6.694284541226638, trackedObject1.getCoordinates().get(0).getY());
    }

    /**
     * @inv The landmarks list in FusionSlam must remain consistent with the objects added.
     * @pre A tracked object (trackedObject2) exists and has been transformed to global coordinates.
     * @post The tracked object is successfully added as a landmark, and its coordinates
     *       match the expected transformed values.
     */
    @Test
    void testUpdateLandMarks() {
        TrackedObject trackedObject2 = new TrackedObject("Wall_2", 2, "Wall near door", (ArrayList<CloudPoint>) cloudPoints2);

        fusionSlam.trackedObjectToGlobal(trackedObject2, fusionSlam.getPose(2));
        fusionSlam.addLankMark(new LandMark(trackedObject2.getId(), trackedObject2.getDescription(), trackedObject2.getCoordinates()));

        assertNotNull(fusionSlam.getLankMark("Wall_2"));
        assertEquals(0.710612368454115, fusionSlam.getLankMark("Wall_2").getCoordinates().get(0).getX());
        assertEquals(0.49761536306778176, fusionSlam.getLankMark("Wall_2").getCoordinates().get(1).getX());
    }

    /**
     * @inv Cloud point transformation must produce new global coordinates.
     * @pre A tracked object (trackedObject3) exists with local cloud points.
     * @post The tracked object's global coordinates differ from the fixed expected values,
     *       verifying the correctness of the transformation logic.
     */
    @Test
    void testTrackedObjectToGlobal_CorrectTransformation() {
        TrackedObject trackedObject3 = new TrackedObject("Wall_3", 3, "Wall3", (ArrayList<CloudPoint>) cloudPoints3);
        fusionSlam.trackedObjectToGlobal(trackedObject3, fusionSlam.getPose(3));

        for (CloudPoint point : trackedObject3.getCoordinates()) {
            assertNotEquals(2.182101890308385, point.getX());
            assertNotEquals(6.694284541226638, point.getY());
        }
    }
}
