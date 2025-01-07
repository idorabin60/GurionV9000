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
    void setup(){
        msgBus = MessageBusImpl.getInstance();
        List<StampedDetectedObjects> l1 = new ArrayList<>();
        service1 = new CameraService(new Camera(1,1, STATUS.UP));
        msgBus.register(service1);
    }

    /**@inv msgBus microservice registration queue size is non-negative
     * @pre msgBus does not contain any subscribed MicroServices other than service 1
     * @post msgBus contains service2 as a subscribed MicroServices
     */
    @Test
    void registerTest() {
        CameraService service2 = new CameraService(new Camera(2, 2, null)); // Example MicroServices
        // check that the new service is not registered
        assertFalse(msgBus.getMicroServiceQueue().containsKey(service2));
        // register the new service
        msgBus.register(service2);
        // check again
        assertTrue(msgBus.getMicroServiceQueue().containsKey(service2));
    }

    /**@inv msgBus microservice registration queue size is non-negative
     * @pre msgBus contains service1 as a subscribed MicroService
     * @post msgBus does Not contain service1 as a subscribed MicroService
     */
    @Test
    void unregisterTest() {
        // check that the service is registered
        assertTrue(msgBus.getMicroServiceQueue().containsKey(service1));
        // unregister this service
        msgBus.unregister(service1);
        // check again
        assertFalse(msgBus.getMicroServiceQueue().containsKey(service1));
    }

    /**@inv subscribers queue size is non-negative
     * @pre no service is subscribed to get broadcasts
     * @post a new service camService is subscribed to get broadcasts
     */
    @Test
    void subscribeBroadcastTest() {
        assertTrue(msgBus.getBroadcastSubscriptions().isEmpty());
        MicroService camService = new CameraService(new Camera(1, 5, STATUS.UP));
        msgBus.subscribeBroadcast(CrashedBroadcast.class, camService);
        CopyOnWriteArrayList<MicroService> subscribers = msgBus.getBroadcastSubscriptions().get(CrashedBroadcast.class);
        assertTrue(subscribers.contains(camService));
    }
}