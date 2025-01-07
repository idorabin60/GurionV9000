import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    void setUp() {
        List<StampedDetectedObjects> listDetectedObjects = new ArrayList<>();

        List<DetectedObject> objects1 = new ArrayList<>();
        objects1.add(new DetectedObject("Wall_1", "DafnaRoom"));
        objects1.add(new DetectedObject("Wall_2", "IdoRoom"));
        StampedDetectedObjects temp = new StampedDetectedObjects(1, objects1);
        listDetectedObjects.add(temp);


        List<DetectedObject> objects2 = new ArrayList<>();
        objects2.add(new DetectedObject("ERROR", "Camera Dissconect"));
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

    //Tests to the method: getDetectedObjectAtTimeT
    @Test
    void testGetDetectedObjectAtTimeT_ValidTime() {
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(1);
        assertNotNull(result);
    }

    @Test
    void testGetDetectedObjectAtTimeT_InvalidTime() {
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(-1);
        assertNull(result);
        result = camera.getDetectedObjectAtTimeT(3);
        assertNull(result);
    }

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

    //Test to the method: hasError
    @Test
    public void testHasErrorWithErrorObject() {
        assertTrue(camera.hasError(camera.getDetectedObjectAtTimeT(2).getDetectedObjects()));
    }
}