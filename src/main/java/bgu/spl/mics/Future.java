package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private boolean isResolved;
	private Lock lock;
	private Condition resultAvilable;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.isResolved = false;
		this.lock = new ReentrantLock();
		this.resultAvilable = lock.newCondition();
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * 
	 * @return return the result of type T if it is available, if not wait until it
	 *         is available.
	 * 
	 */
	public T get() {
		lock.lock();
		try {
			while (isResolved == false) {
				resultAvilable.await();
			}
			return result;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		// TODO: implement this.
		return null;
	}

	/**
	 * Resolves the result of this Future object.
	 */
	public void resolve(T result) {
		lock.lock();
		try {

			this.result = result;
			this.isResolved = true;
			resultAvilable.signalAll();

		} finally {
			lock.unlock();
		}
		// TODO: implement this.
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		lock.lock();
		try {
			return isResolved;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * 
	 * @param timout the maximal amount of time units to wait for the result.
	 * @param unit   the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 *         wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	public T get(long timeout, TimeUnit unit) {
		lock.lock();
		try {
			if (!isResolved) {
				long timeoutNanos = unit.toNanos(timeout);
				while (!isResolved && timeoutNanos > 0) {
					timeoutNanos = resultAvilable.awaitNanos(timeoutNanos);
				}
			}
			return isResolved ? result : null;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
			return null;
		} finally {
			lock.unlock();
		}
	}

}
