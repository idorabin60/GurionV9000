import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraDataUpdater;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
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
    private List<Camera> cameras;

    @BeforeEach
    void setUp() {
        String configFilePath = "camera_data.json"; // Replace with actual path
        cameras = new ArrayList<Camera>();
        try {
            // Parse configuration file (directly copied here from Main)
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(new FileReader(configFilePath), JsonObject.class);

            // Initialize cameras from configuration
            cameras = CameraDataUpdater.initializeCamerasFromConfig(configFilePath);
            CameraDataUpdater.updateCamerasFromJson(cameras, configFilePath);
        } catch (IOException e) {
            fail("Failed to initialize cameras from configuration: " + e.getMessage());
        }
    }

    //Tests to the method: getDetectedObjectAtTimeT
    @Test
    void testGetDetectedObjectAtTimeT_ValidTime() {
        Camera camera = cameras.get(0);
        int timeT = 1;
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(timeT);
        assertNotNull(result, "Result should not be null for a valid time");
    }

    @Test
    void testGetDetectedObjectAtTimeT_InvalidTime() {
        Camera camera = cameras.get(0);
        int timeT = -1; // Invalid time
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(timeT);
        assertNull(result, "Result should be null for an invalid time");
    }

    @Test
    void testGetDetectedObjectAtTimeT_FirstElement() {
        Camera camera = cameras.get(0);
        int timeT = 2; // First time in the list (replace with your actual first time)
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(timeT);
        // Assertions
        assertNotNull(result, "Result should not be null for the first element");
        assertEquals(2, result.getTime(), "The returned object's time should match the requested time");
        assertEquals(1, result.getDetectedObjects().size(), "The number of detected objects should match");
        assertEquals("Wall_1", result.getDetectedObjects().get(0).getId(), "The first detected object's ID should match");
    }

    //Test to the method: hasError
    @Test
    public void testHasErrorWithErrorObject() {
        Camera camera = cameras.get(0);
        List<DetectedObject> temp = camera.getDetectedObjectAtTimeT(5).getDetectedObjects();
        // Act
        boolean result = camera.hasError(temp);
        // Assert
        assertTrue(result, "The method should return true when an object with id 'ERROR' is found.");
    }
}