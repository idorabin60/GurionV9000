package bgu.spl.mics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceMessageQueue;
    private final ConcurrentHashMap<Class<? extends Broadcast>, CopyOnWriteArrayList<MicroService>> broadcastSubscriptions; //mabye it should
    private final ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventSubscriptions;
    private final ConcurrentHashMap<Event<?>, Future<?>> eventFutureMap;
    private static MessageBusImpl instance;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    private MessageBusImpl() {
        microServiceMessageQueue = new ConcurrentHashMap<>();//       broadcastSubscriptions = new ConcurrentHashMap<>();
        eventSubscriptions = new ConcurrentHashMap<>();
        eventFutureMap = new ConcurrentHashMap<>();
        broadcastSubscriptions = new ConcurrentHashMap<>();
    }

    //NEED TO CHANGE IT LIKE IN PS8
    public static MessageBusImpl getInstance() {
        if (instance == null) { // First check (no locking)
            synchronized (MessageBusImpl.class) { //s
                if (instance == null) { // Second check (with locking)
                    instance = new MessageBusImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        lock.readLock().lock();
        // TODO Auto-generated method stub
        eventSubscriptions.computeIfAbsent(type, key -> new LinkedBlockingQueue<>());
        try {
            eventSubscriptions.get(type).put(m);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Subscription interrupted", e);
        } finally {
            lock.readLock().unlock();
        }


    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        lock.readLock().lock();
        try {
            broadcastSubscriptions.computeIfAbsent(type, key -> new CopyOnWriteArrayList<>()).add(m);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        // Add the MicroService to the list of subscribers for the given broadcast type
    }


    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = (Future<T>) eventFutureMap.get(e);
        if (future != null) {
            future.resolve(result); // Resolves the Future with the result.
        }
    }


    @Override
    public void sendBroadcast(Broadcast b) {
        lock.readLock();
        try {
            CopyOnWriteArrayList<MicroService> brodacstBSubs = broadcastSubscriptions.get(b.getClass());
            if (brodacstBSubs != null) {
                for (MicroService m : brodacstBSubs) {
                    BlockingQueue<Message> queue = microServiceMessageQueue.get(m);
                    if (queue != null) {
                        queue.add(b);
                    }
                }
            }

        } finally {
            lock.readLock().unlock();
        }


    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        lock.readLock().lock();
        BlockingQueue<MicroService> subscribers = eventSubscriptions.get(e.getClass());
        if (subscribers == null || subscribers.isEmpty()) {
            return null; // No MicroServices are subscribed to this event type.
        }

        try {
            MicroService m = subscribers.poll(); // Get the next MicroService in the round-robin queue.
            if (m != null) {
                BlockingQueue<Message> queue = microServiceMessageQueue.get(m);
                if (queue != null) {
                    queue.add(e); // Add the event to the MicroService's message queue.
                }
                subscribers.put(m); // Re-add the MicroService to the queue for round-robin behavior.

                // Create and store the Future object for this event.
                Future<T> future = new Future<>();
                eventFutureMap.put(e, future);
                return future;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            lock.readLock().unlock();
        }

        return null; // Return null if no MicroService was available.
    }


    @Override
    public void register(MicroService m) {
        lock.readLock().lock();
        try {
            microServiceMessageQueue.putIfAbsent(m, new LinkedBlockingQueue<>());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override  // check about the
    public void unregister(MicroService m) {
        lock.writeLock().lock(); // Write lock to modify shared data structures
        try {
            // Remove from microServiceMessageQueue
            microServiceMessageQueue.remove(m);

            // Remove from eventSubscriptions
            eventSubscriptions.forEach((eventType, microServiceQueue) -> {
                microServiceQueue.remove(m);
            });

            // Remove from broadcastSubscriptions
            broadcastSubscriptions.forEach((broadcastType, microServiceQueue) -> {
                microServiceQueue.remove(m);
            });

            // Need to check if its redundant or not!
            eventFutureMap.forEach((event, future) -> {
                BlockingQueue<MicroService> subscribers = eventSubscriptions.get(event.getClass());
                if (subscribers != null && subscribers.contains(m)) {
                    future.resolve(null);
                }
            });
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        lock.readLock().lock();
        try {
            BlockingQueue<Message> queue = microServiceMessageQueue.get(m);
            return queue.take(); // Waits for the next message
        } finally {
            lock.readLock().unlock();
        }
    }


}
