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
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> queues;
	private final ConcurrentHashMap<Class<? extends Message>, List<MicroService>> subscriptions;
	private final ConcurrentHashMap<Class<? extends Message>, AtomicInteger> roundRobinIndices;
	private static MessageBusImpl instance;

	private MessageBusImpl() {
		queues = new ConcurrentHashMap<>();
		subscriptions = new ConcurrentHashMap<>();
		roundRobinIndices = new ConcurrentHashMap<>();
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

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

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
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!queues.containsKey(m)) {
			BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
			queues.put(m, messageQueue);
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
