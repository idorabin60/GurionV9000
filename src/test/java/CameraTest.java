import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraDataUpdater;
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
        String configFilePath = "configuration_file.json"; // Replace with actual path
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

    @Test
    void testGetDetectedObjectAtTimeT_ValidTime() {
        Camera camera = cameras.get(0);
        int timeT = 1; // Replace with a valid timestamp from your configuration
        StampedDetectedObjects result = camera.getDetectedObjectAtTimeT(timeT);
        assertNotNull(result, "Result should not be null for a valid time");
    }
}




