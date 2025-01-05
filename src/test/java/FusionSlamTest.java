import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class FusionSlamTest {
    private FusionSlam fusionSlam;
    private TrackedObject trackedObject;
    private Pose pose;

    @BeforeEach
    void setUp() {
        fusionSlam = new FusionSlam();
        // Initialize the trackedObject and pose before each test
        trackedObject = new TrackedObject("test", 1, "unitTest", null);
        ArrayList<CloudPoint> coordinates = new ArrayList<>();
        coordinates.add(new CloudPoint(0.1176, 3.6969));
        trackedObject.setCoordinates(coordinates);

        // Initialize Pose (Assuming Pose has a constructor or setters to set values)
        pose = new Pose( 2, 3, -1, 1);  // Example values for pose (x, y)
    }
    @Test
    void testTrackedObjectToGlobal_UpdatesCoordinates() {
        fusionSlam.trackedObjectToGlobal(trackedObject, pose);

        // Assert: Check if the coordinates have been updated correctly
        for (CloudPoint point : trackedObject.getCoordinates()) {
            assertNotEquals(1, point.getX(), "X coordinate should be updated");
            assertNotEquals(1, point.getY(), "Y coordinate should be updated");
        }
    }

    @Test
    void testTrackedObjectToGlobal_CorrectTransformation() {
        fusionSlam.trackedObjectToGlobal(trackedObject, pose);
        for (CloudPoint point : trackedObject.getCoordinates()) {
            assertNotEquals(2.182101890308385, point.getX(), "should be the same");
            assertNotEquals(6.694284541226638, point.getY(), "should be the same");
        }
    }
}