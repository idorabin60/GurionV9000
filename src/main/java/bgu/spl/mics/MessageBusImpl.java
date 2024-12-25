package bgu.spl.mics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceMessageQueue;
	private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscriptions;
	private final ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventSubscriptions;
	private  final  ConcurrentHashMap <Event<?>, Future<?>> eventFutureMap;
	private static MessageBusImpl instance;

	private MessageBusImpl() {
		microServiceMessageQueue = new ConcurrentHashMap<>();
		broadcastSubscriptions = new ConcurrentHashMap<>();
		eventSubscriptions = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

    @Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		eventSubscriptions.computeIfAbsent(type, key -> new LinkedBlockingQueue<>());
		try {
			eventSubscriptions.get(type).put(m);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Subscription interrupted", e);
		}


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		broadcastSubscriptions.computeIfAbsent(type,key->new LinkedBlockingQueue<>());
		try {
			broadcastSubscriptions.get(type).put(m);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Subscription interrupted", e);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		 MicroService m = eventSubscriptions.get(e.getClass()).poll();
		 microServiceMessageQueue.get(m).add(e);
		 try {
			 eventSubscriptions.get(e.getClass()).put(m);

		 } catch (InterruptedException ex) {
			 throw new RuntimeException(ex);
		 }
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!microServiceMessageQueue.containsKey(m)) {
			BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
			microServiceMessageQueue.put(m, messageQueue);
		}
		// TODO Auto-generated method stub

	}


	@Override  // ask yotam osher snir and dafna about their implemnation
	public void unregister(MicroService m) {
		microServiceMessageQueue.remove(m);

		eventSubscriptions.forEach((eventType, microServiceQueue) -> {
			microServiceQueue.remove(m);
		});

		broadcastSubscriptions.forEach((broadcastType, microServiceQueue) -> {
			microServiceQueue.remove(m);
		});

		eventFutureMap.forEach((event, future) -> {
			BlockingQueue<MicroService> subscribers = eventSubscriptions.get(event.getClass());
			if (subscribers != null && subscribers.contains(m)) {
				future.resolve(null);
			}
		});
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microServiceMessageQueue.get(m).take();
	}

	

}
