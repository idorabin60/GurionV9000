import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    void setUp() {
        // Precondition: The Camera class and its dependencies (e.g., StampedDetectedObjects, DetectedObject)
        // must be available. A valid Camera object is initialized with a unique ID, timestamp, and status.
        // Postcondition: The Camera object is initialized with a predefined list of detected objects,
        // including valid detected objects and one error object.
        List<StampedDetectedObjects> listDetectedObjects = new ArrayList<>();

        List<DetectedObject> objects1 = new ArrayList<>();
        objects1.add(new DetectedObject("Wall_1", "DafnaRoom"));
        objects1.add(new DetectedObject("Wall_2", "IdoRoom"));
        StampedDetectedObjects temp = new StampedDetectedObjects(1, objects1);
        listDetectedObjects.add(temp);

        List<DetectedObject> objects2 = new ArrayList<>();
        objects2.add(new DetectedObject("ERROR", "Camera Disconnect"));
        StampedDetectedObjects errorStamped = new StampedDetectedObjects(2, objects2);
        listDetectedObjects.add(errorStamped);

        List<DetectedObject> objects3 = new ArrayList<>();
        objects3.add(new DetectedObject("Chair_1", "nana"));
        objects3.add(new DetectedObject("Chair_2", "banana"));
        StampedDetectedObjects s4 = new StampedDetectedObjects(4, objects3);
        listDetectedObjects.add(s4);

        camera = new Camera(1, 0, STATUS.UP);
        camera.updateDetectedObjects(listDetectedObjects);
    }

    /**
     * @inv The detected objects list in the Camera object remains consistent and does not change unexpectedly.
     * @pre The Camera object contains a valid list of detected objects, including objects
     *      at timestamp 1.
     * @post The method returns a non-null object for a valid timestamp.
     */
    @Test
    void testGetDetectedObjectAtTimeT_ValidTime() {
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(1);
        assertNotNull(result);
    }

    /**
     * @inv Invalid timestamps must not produce non-null results.
     * @pre The Camera object contains detected objects, but the requested timestamps are invalid
     *      (e.g., negative or not in the list of timestamps).
     * @post The method returns null for invalid timestamps.
     */
    @Test
    void testGetDetectedObjectAtTimeT_InvalidTime() {
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(-1);
        assertNull(result);
        result = camera.getDetectedObjectAtTimeT(3);
        assertNull(result);
    }

    /**
     * @inv The detected objects list remains consistent when accessing existing timestamps.
     * @pre The Camera object contains detected objects at timestamp 1 with specific object IDs
     *      and descriptions.
     * @post The method retrieves the correct object at timestamp 1, validates that it
     *       contains no errors, and verifies the object IDs and descriptions.
     */
    @Test
    void testGetDetectedObjectAtTimeT_FirstElement() {
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(1);

        assertNotNull(result);
        assertFalse(camera.hasError(result.getDetectedObjects()));
        assertEquals(1, result.getTime());
        assertEquals(2, result.getDetectedObjects().size());
        assertEquals("Wall_1", result.getDetectedObjects().get(0).getId());
        assertEquals("DafnaRoom", result.getDetectedObjects().get(0).getDescription());
        assertEquals("Wall_2", result.getDetectedObjects().get(1).getId());
        assertEquals("IdoRoom", result.getDetectedObjects().get(1).getDescription());
    }

    /**
     * @inv The `hasError` method consistently identifies error objects in the detected objects list.
     * @pre The Camera object contains an error object at timestamp 2.
     * @post The method correctly identifies the error in the list of detected objects.
     */
    @Test
    public void testHasErrorWithErrorObject() {
        assertTrue(camera.hasError(camera.getDetectedObjectAtTimeT(2).getDetectedObjects()));
    }
}
