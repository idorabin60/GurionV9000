package bgu.spl.mics.application.objects;

import java.util.concurrent.CountDownLatch;

// Singleton class to share a CountDownLatch between all services
public class SystemServicesCountDownLatch {
    private static volatile SystemServicesCountDownLatch instance = null;
    private final CountDownLatch countDownLatch; // Immutable latch

    // Private constructor to ensure controlled initialization
    private SystemServicesCountDownLatch(int numberOfServices) {
        this.countDownLatch = new CountDownLatch(numberOfServices);
    }

    /**
     * Initializes the singleton with the given number of services.
     * Must be called before getInstance().
     *
     * @param numberOfServices The number of services to wait for.
     */
    public static void init(int numberOfServices) {
        if (instance == null) {
            synchronized (SystemServicesCountDownLatch.class) {
                if (instance == null) {
                    instance = new SystemServicesCountDownLatch(numberOfServices);
                }
            }
        }
    }

    /**
     * Returns the singleton instance.
     * Throws an exception if the instance has not been initialized.
     *
     * @return The singleton instance of SystemServicesCountDownLatch.
     */
    public static SystemServicesCountDownLatch getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SystemServicesCountDownLatch has not been initialized. Call init() first.");
        }
        return instance;
    }

    /**
     * Returns the CountDownLatch.
     *
     * @return The CountDownLatch instance.
     */
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
