package bgu.spl.mics;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageBusImpl implements MessageBus {
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceMessageQueue;
    private final ConcurrentHashMap<Class<? extends Broadcast>, CopyOnWriteArrayList<MicroService>> broadcastSubscriptions;
    private final ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscriptions;
    private final ConcurrentHashMap<Event<?>, Future<?>> eventFutureMap;
    private static volatile MessageBusImpl instance; // Ensure volatile for thread-safe double-checked locking
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private MessageBusImpl() {
        microServiceMessageQueue = new ConcurrentHashMap<>();
        broadcastSubscriptions = new ConcurrentHashMap<>();
        eventSubscriptions = new ConcurrentHashMap<>();
        eventFutureMap = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (instance == null) {
            synchronized (MessageBusImpl.class) {
                if (instance == null) {
                    instance = new MessageBusImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

            eventSubscriptions.computeIfAbsent(type, key -> new LinkedBlockingQueue<>()).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
            broadcastSubscriptions.computeIfAbsent(type, key -> new CopyOnWriteArrayList<>()).add(m);


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
        lock.readLock().lock();
        try {
            CopyOnWriteArrayList<MicroService> subscribers = broadcastSubscriptions.get(b.getClass());
            if (subscribers != null) {
                for (MicroService m : subscribers) {
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
        lock.writeLock().lock();
        try {
            LinkedBlockingQueue<MicroService> subscribers = eventSubscriptions.get(e.getClass());
            if (subscribers == null || subscribers.isEmpty()) {
                return null; // No subscribers for this event
            }
            MicroService m = subscribers.poll(); // Get the next MicroService in round-robin order
            if (m != null) {
                BlockingQueue<Message> queue = microServiceMessageQueue.get(m);
                if (queue != null) {
                    queue.add(e); // Add the event to the MicroService's queue
                }
                subscribers.add(m); // Re-add for round-robin
                Future<T> future = new Future<>();
                eventFutureMap.put(e, future);
                return future;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return null; // Return null if no subscriber was found
    }

    @Override
    public void register(MicroService m) {
        microServiceMessageQueue.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        lock.writeLock().lock();
        try {
            microServiceMessageQueue.remove(m);
            eventSubscriptions.values().forEach(queue -> queue.remove(m));
            broadcastSubscriptions.values().forEach(list -> list.remove(m));
            eventFutureMap.keySet().removeIf(event -> {
                LinkedBlockingQueue<MicroService> subscribers = eventSubscriptions.get(event.getClass());
                return subscribers != null && subscribers.contains(m);
            });
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> queue = microServiceMessageQueue.get(m);
        if (queue == null) {
            throw new IllegalStateException("MicroService not registered: " + m.getName());
        }
        return queue.take(); // Wait for the next message
    }
}
