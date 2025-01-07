import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageBusImplTest {
    private MessageBusImpl msgBus;
    private CameraService service1;

    @BeforeEach
    void setup() {
        // Precondition: The MessageBusImpl instance is initialized and ready for registration.
        // A CameraService instance (service1) is created and registered to the MessageBusImpl.
        // Postcondition: service1 is registered to the MessageBusImpl, and the microservice queue contains service1.
        msgBus = MessageBusImpl.getInstance();
        List<StampedDetectedObjects> l1 = new ArrayList<>();
        service1 = new CameraService(new Camera(1, 1, STATUS.UP));
        msgBus.register(service1);
    }

    /**
     * @inv The size of the MessageBus microservice registration queue is non-negative.
     * @pre The MessageBusImpl does not contain any registered microservices other than service1.
     * @post The MessageBusImpl contains service2 as a registered microservice.
     */
    @Test
    void registerTest() {
        CameraService service2 = new CameraService(new Camera(2, 2, null)); // Example microservice
        // Verify that service2 is not registered before registration
        assertFalse(msgBus.getMicroServiceQueue().containsKey(service2));
        // Register service2 to the MessageBus
        msgBus.register(service2);
        // Verify that service2 is now registered
        assertTrue(msgBus.getMicroServiceQueue().containsKey(service2));
    }

    /**
     * @inv The size of the MessageBus microservice registration queue is non-negative.
     * @pre The MessageBusImpl contains service1 as a registered microservice.
     * @post The MessageBusImpl no longer contains service1 as a registered microservice.
     */
    @Test
    void unregisterTest() {
        // Verify that service1 is registered before unregistration
        assertTrue(msgBus.getMicroServiceQueue().containsKey(service1));
        // Unregister service1 from the MessageBus
        msgBus.unregister(service1);
        // Verify that service1 is no longer registered
        assertFalse(msgBus.getMicroServiceQueue().containsKey(service1));
    }

    /**
     * @inv The size of the broadcast subscription queue is non-negative.
     * @pre No microservice is subscribed to receive broadcasts of type CrashedBroadcast.
     * @post camService is subscribed to receive broadcasts of type CrashedBroadcast.
     */
    @Test
    void subscribeBroadcastTest() {
        // Verify that no service is subscribed to broadcasts initially
        assertTrue(msgBus.getBroadcastSubscriptions().isEmpty());
        // Create a new CameraService instance and subscribe it to CrashedBroadcast
        MicroService camService = new CameraService(new Camera(1, 5, STATUS.UP));
        msgBus.subscribeBroadcast(CrashedBroadcast.class, camService);
        // Verify that camService is subscribed to CrashedBroadcast
        CopyOnWriteArrayList<MicroService> subscribers = msgBus.getBroadcastSubscriptions().get(CrashedBroadcast.class);
        assertTrue(subscribers.contains(camService));
    }
}