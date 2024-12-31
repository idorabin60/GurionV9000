package bgu.spl.mics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Collections;

//TODO: add synchronization!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
    private static class SingletonHolder {
        private static final MessageBusImpl instance = new MessageBusImpl();
    }

    private static MessageBusImpl instance;
    private ConcurrentHashMap<Event<?>, Future<?>> futures;
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> missions;
    private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> eventHandlers;
    private ConcurrentHashMap<Class<? extends Message>, List<MicroService>> broadcastSubscriptions;

    private MessageBusImpl(){
        futures = new ConcurrentHashMap<>();
        missions = new ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>>();
        eventHandlers = new ConcurrentHashMap<>();
        broadcastSubscriptions = new ConcurrentHashMap<Class<? extends Message>, List<MicroService>>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        // TODO Auto-generated method stub
        eventHandlers.putIfAbsent(type, new LinkedBlockingQueue<>());
        try{
            eventHandlers.get(type).put(m);
        }
        catch (InterruptedException i){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // TODO Auto-generated method stub
        broadcastSubscriptions.putIfAbsent(type, Collections.synchronizedList(new ArrayList<>()));
        List<MicroService> receiverList = broadcastSubscriptions.get(type);
        receiverList.add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // TODO Auto-generated method stub
        Future<T> f = (Future<T>)futures.get(e);
        if (f!=null)
            f.resolve(result);
//		futures.remove(e);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        // TODO Auto-generated method stub
        List<MicroService> broadcastTo = broadcastSubscriptions.get(b);
        if(broadcastTo==null)
            return;
        for (MicroService mic: broadcastTo){
            try {
                LinkedBlockingQueue<Message> bq = missions.get(mic);
                bq.put(b);
            }
            catch (InterruptedException i){
                Thread.currentThread().interrupt();
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        // TODO Auto-generated method stub
        LinkedBlockingQueue<MicroService> handlers = eventHandlers.get(e);
        if (handlers.isEmpty() || handlers == null){
            return null;
        }
        //Round-Robin starts here
        try {
            MicroService handler = handlers.take();
            LinkedBlockingQueue<Message> q = missions.get(handler);
            q.put(e);
            handlers.put(handler);
        }catch (InterruptedException i){
            Thread.currentThread().interrupt();
        }
        //Round-Robin ends here
        Future<T> f = new Future<>();
        futures.put(e,f);
        return f;
    }

    @Override
    public void register(MicroService m) {
        // TODO Auto-generated method stub
        missions.put(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        // TODO Auto-generated method stub
        missions.remove(m);
        for (LinkedBlockingQueue<MicroService> s : eventHandlers.values() ){
            if(s.contains(m)){
                s.remove(m);
            }
        }
        for (List<MicroService> s : broadcastSubscriptions.values() ){
            if(s.contains(m)){
                s.remove(m);
            }
        }


    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if(!missions.get(m).isEmpty()) {
            try {
                return missions.get(m).take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    public static MessageBusImpl getInstance(){
        return SingletonHolder.instance;
    }
}